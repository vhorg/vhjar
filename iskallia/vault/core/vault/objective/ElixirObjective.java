package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataMap;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.elixir.ElixirGoal;
import iskallia.vault.core.vault.objective.elixir.ElixirTask;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ElixirObjective extends Objective {
   public static final ResourceLocation HUD = VaultMod.id("textures/gui/elixir/hud.png");
   public static final SupplierKey<Objective> KEY = SupplierKey.of("elixir", Objective.class).with(Version.v1_12, ElixirObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<ElixirObjective.GoalMap> GOALS = FieldKey.of("goals", ElixirObjective.GoalMap.class)
      .with(Version.v1_12, CompoundAdapter.of(ElixirObjective.GoalMap::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);

   protected ElixirObjective() {
      this.set(GOALS, new ElixirObjective.GoalMap());
   }

   public static ElixirObjective create() {
      return new ElixirObjective();
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      this.get(GOALS).forEach((uuid, goal) -> goal.initServer(world, vault, this, uuid));
      super.initServer(world, vault);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      this.get(GOALS).forEach((uuid, goal) -> goal.tickServer(world, vault, this, uuid));
      if (this.get(GOALS).areAllCompleted(vault)) {
         super.tickServer(world, vault);
      }
   }

   @Override
   public void releaseServer() {
      this.get(GOALS).forEach((uuid, goal) -> goal.releaseServer());
      super.releaseServer();
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener instanceof Runner runner && listener.getPriority(this) < 0) {
         listener.addObjective(vault, this);
         this.generateGoal(world, vault, runner);
      }

      ElixirGoal goal = this.get(GOALS).get(listener.get(Listener.ID));
      if (goal != null && goal.isCompleted()) {
         super.tickListener(world, vault, listener);
      }
   }

   private void generateGoal(VirtualWorld world, Vault vault, Runner listener) {
      ElixirGoal goal = new ElixirGoal();
      this.get(GOALS).put(listener.get(Listener.ID), goal);
      JavaRandom random = JavaRandom.ofInternal(vault.get(Vault.SEED) ^ listener.get(Listener.ID).getMostSignificantBits());
      goal.set(ElixirGoal.TARGET, Integer.valueOf(ModConfigs.ELIXIR.generateTarget(vault.get(Vault.LEVEL).get(), random)));
      goal.set(ElixirGoal.BASE_TARGET, goal.get(ElixirGoal.TARGET));

      for (ElixirTask task : ModConfigs.ELIXIR.generateGoals(vault.get(Vault.LEVEL).get(), random)) {
         goal.get(ElixirGoal.TASKS).add(task);
      }

      goal.initServer(world, vault, this, listener.getId());
   }

   public void addProgress(UUID listener, int elixir) {
      ElixirGoal goal = this.get(GOALS).get(listener);
      if (goal != null) {
         int remainder = goal.add(elixir);
         RandomSource random = JavaRandom.ofNanoTime();
         List<UUID> targets = new ArrayList<>(this.get(GOALS).keySet());
         targets.remove(listener);

         while (!targets.isEmpty() && remainder > 0) {
            int index = random.nextInt(targets.size());
            remainder = this.get(GOALS).get(targets.get(index)).add(remainder);
            targets.remove(index);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      ElixirGoal goal = this.get(GOALS).get(player.getUUID());
      if (goal == null || this.get(GOALS).areAllCompleted(vault)) {
         boolean rendered = false;

         for (Objective objective : this.get(CHILDREN)) {
            rendered |= objective.render(vault, matrixStack, window, partialTicks, player);
         }

         if (rendered) {
            return true;
         }
      }

      if (goal == null) {
         return true;
      } else {
         Component txt;
         int current;
         int total;
         if (goal.isCompleted()) {
            txt = new TextComponent("Assist your Allies!").withStyle(ChatFormatting.WHITE);
            current = this.get(GOALS).getTotalCurrent(vault);
            total = this.get(GOALS).getTotalTarget(vault);
         } else {
            txt = new TextComponent("Gather")
               .withStyle(ChatFormatting.WHITE)
               .append(new TextComponent(" Elixir ").withStyle(Style.EMPTY.withColor(13390079)))
               .append(new TextComponent("from loot, ores & mobs!").withStyle(ChatFormatting.WHITE));
            current = goal.get(ElixirGoal.CURRENT);
            total = goal.get(ElixirGoal.TARGET);
         }

         int midX = window.getGuiScaledWidth() / 2;
         matrixStack.pushPose();
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int previousTexture = RenderSystem.getShaderTexture(0);
         RenderSystem.setShaderTexture(0, HUD);
         float progress = (float)current / total;
         matrixStack.translate(midX - 80, 8.0, 0.0);
         GuiComponent.blit(matrixStack, 0, 0, 0.0F, 0.0F, 200, 26, 200, 50);
         GuiComponent.blit(matrixStack, 0, 8, 0.0F, 28.0F, 15 + (int)(130.0F * progress), 10, 200, 50);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, previousTexture);
         matrixStack.popPose();
         Font font = Minecraft.getInstance().font;
         BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         matrixStack.pushPose();
         matrixStack.scale(0.6F, 0.6F, 0.6F);
         font.drawInBatch(
            txt.getVisualOrderText(),
            midX / 0.6F - font.width(txt) / 2.0F,
            9 + 22,
            -1,
            true,
            matrixStack.last().pose(),
            buffer,
            false,
            0,
            LightmapHelper.getPackedFullbrightCoords()
         );
         buffer.endBatch();
         matrixStack.popPose();
         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void renderPartyInfo(PoseStack matrixStack, UUID playerUUID) {
      super.renderPartyInfo(matrixStack, playerUUID);
      ElixirGoal goal = this.get(GOALS).get(playerUUID);
      if (goal != null) {
         int current = goal.get(ElixirGoal.CURRENT);
         int total = goal.get(ElixirGoal.TARGET);
         matrixStack.pushPose();
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int previousTexture = RenderSystem.getShaderTexture(0);
         RenderSystem.setShaderTexture(0, HUD);
         float progress = (float)current / total;
         matrixStack.translate(-120.0, -2.0, 0.0);
         matrixStack.scale(0.5F, 0.5F, 0.5F);
         GuiComponent.blit(matrixStack, 0, 0, 0.0F, 0.0F, 200, 26, 200, 50);
         GuiComponent.blit(matrixStack, 0, 8, 0.0F, 28.0F, 15 + (int)(130.0F * progress), 10, 200, 50);
         RenderSystem.setShaderTexture(0, previousTexture);
         matrixStack.popPose();
      }
   }

   @Override
   public boolean isActive(Vault vault, Objective objective) {
      if (!this.get(GOALS).areAllCompleted(vault)) {
         return objective == this;
      } else {
         for (Objective child : this.get(CHILDREN)) {
            if (child.isActive(vault, objective)) {
               return true;
            }
         }

         return false;
      }
   }

   public static class GoalMap extends DataMap<ElixirObjective.GoalMap, UUID, ElixirGoal> {
      public GoalMap() {
         super(new HashMap<>(), Adapters.UUID, CompoundAdapter.of(ElixirGoal::new));
      }

      public boolean areAllCompleted(Vault vault) {
         for (Runner runner : vault.get(Vault.LISTENERS).getAll(Runner.class)) {
            if (this.containsKey(runner.getId()) && !this.get(runner.getId()).isCompleted()) {
               return false;
            }
         }

         return true;
      }

      public int getTotalCurrent(Vault vault) {
         int current = 0;

         for (Runner runner : vault.get(Vault.LISTENERS).getAll(Runner.class)) {
            if (this.containsKey(runner.getId())) {
               current += this.get(runner.getId()).get(ElixirGoal.CURRENT);
            }
         }

         return current;
      }

      public int getTotalTarget(Vault vault) {
         int target = 0;

         for (Runner runner : vault.get(Vault.LISTENERS).getAll(Runner.class)) {
            if (this.containsKey(runner.getId())) {
               target += this.get(runner.getId()).get(ElixirGoal.TARGET);
            }
         }

         return target;
      }
   }
}
