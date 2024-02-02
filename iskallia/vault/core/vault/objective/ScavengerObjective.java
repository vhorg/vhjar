package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.block.DivineAltarBlock;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.client.gui.helper.FontHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.gui.helper.UIHelper;
import iskallia.vault.client.util.ClientScheduler;
import iskallia.vault.config.ScavengerConfig;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataMap;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.data.adapter.vault.CompoundAdapter;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockSetEvent;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.scavenger.ScavengeTask;
import iskallia.vault.core.vault.objective.scavenger.ScavengerGoal;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.GodBlessingItem;
import iskallia.vault.item.KeystoneItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RenderProperties;

public class ScavengerObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("scavenger", Objective.class).with(Version.v1_0, ScavengerObjective::new);
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<ScavengerObjective.GoalMap> GOALS = FieldKey.of("goals", ScavengerObjective.GoalMap.class)
      .with(Version.v1_0, CompoundAdapter.of(ScavengerObjective.GoalMap::new), DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_2, Adapters.FLOAT, DISK.all())
      .register(FIELDS);
   public static final FieldKey<ScavengerObjective.Config> CONFIG = FieldKey.of("config", ScavengerObjective.Config.class)
      .with(Version.v1_19, Adapters.ofEnum(ScavengerObjective.Config.class, EnumAdapter.Mode.ORDINAL), DISK.all())
      .register(FIELDS);

   protected ScavengerObjective() {
      this.set(GOALS, new ScavengerObjective.GoalMap());
   }

   protected ScavengerObjective(float objectiveProbability, ScavengerObjective.Config config) {
      this.set(GOALS, new ScavengerObjective.GoalMap());
      this.set(OBJECTIVE_PROBABILITY, Float.valueOf(objectiveProbability));
      this.set(CONFIG, config);
   }

   public static ScavengerObjective of(float objectiveProbability, ScavengerObjective.Config config) {
      return new ScavengerObjective(objectiveProbability, config);
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
      CommonEvents.OBJECTIVE_PIECE_GENERATION
         .register(this, data -> this.ifPresent(OBJECTIVE_PROBABILITY, probability -> data.setProbability(probability.floatValue())));
      CommonEvents.BLOCK_SET.at(BlockSetEvent.Type.RETURN).in(world).register(this, data -> {
         PartialTile target = PartialTile.of(PartialBlockState.of(ModBlocks.PLACEHOLDER), PartialCompoundNbt.empty());
         target.getState().set(PlaceholderBlock.TYPE, PlaceholderBlock.Type.OBJECTIVE);
         if (target.isSubsetOf(PartialTile.of(data.getState()))) {
            data.getWorld().setBlock(data.getPos(), ModBlocks.SCAVENGER_ALTAR.defaultBlockState(), 3);
         }
      });
      CommonEvents.SCAVENGER_ALTAR_CONSUME.register(this, data -> {
         if (data.getLevel() == world && data.getTile().getItemPlacedBy() != null) {
            Listener listener = vault.get(Vault.LISTENERS).get(data.getTile().getItemPlacedBy());
            if (listener instanceof Runner) {
               BlockState state = data.getTile().getBlockState();
               if (state.getBlock() == ModBlocks.DIVINE_ALTAR) {
                  if (data.getTile().getHeldItem().getItem() instanceof KeystoneItem keystone) {
                     if (keystone.getGod() != state.getValue(DivineAltarBlock.GOD)) {
                        return;
                     }
                  } else {
                     if (!(data.getTile().getHeldItem().getItem() instanceof GodBlessingItem)) {
                        return;
                     }

                     if (GodBlessingItem.getGod(data.getTile().getHeldItem()) != state.getValue(DivineAltarBlock.GOD)) {
                        return;
                     }
                  }
               }

               boolean creative = listener.getPlayer().<Boolean>map(ServerPlayer::isCreative).orElse(false);
               CompoundTag nbt = data.getTile().getHeldItem().getTag();
               if (creative || nbt != null && nbt.getString("VaultId").equals(vault.get(Vault.ID).toString())) {
                  for (ScavengerGoal goal : this.get(GOALS).get(listener.get(Listener.ID))) {
                     goal.consume(data.getTile().getHeldItem());
                  }
               }
            }
         }
      });

      for (ScavengeTask task : this.getOr(CONFIG, ScavengerObjective.Config.DEFAULT).get().getTasks()) {
         task.initServer(world, vault, this);
      }

      super.initServer(world, vault);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      this.get(GOALS).forEach((uuid, goal) -> goal.forEach(task -> task.tick(world, vault)));
      if (this.get(GOALS).areAllCompleted(vault)) {
         super.tickServer(world, vault);
      }
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener instanceof Runner && listener.getPriority(this) < 0) {
         listener.addObjective(vault, this);
         this.generateGoal(vault, listener);
      }

      ScavengerGoal.ObjList goal = this.get(GOALS).get(listener.get(Listener.ID));
      if (goal != null && goal.areAllCompleted()) {
         super.tickListener(world, vault, listener);
      }
   }

   private void generateGoal(Vault vault, Listener listener) {
      ScavengerGoal.ObjList list = new ScavengerGoal.ObjList();
      this.get(GOALS).put(listener.get(Listener.ID), list);
      JavaRandom random = JavaRandom.ofInternal(vault.get(Vault.SEED) ^ listener.get(Listener.ID).getMostSignificantBits());
      list.addAll(this.getOr(CONFIG, ScavengerObjective.Config.DEFAULT).get().generateGoals(vault.get(Vault.LEVEL).get(), random));
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      ScavengerGoal.ObjList goals = this.get(GOALS).get(player.getUUID());
      if (goals == null || goals.areAllCompleted()) {
         boolean rendered = false;

         for (Objective objective : this.get(CHILDREN)) {
            rendered |= objective.render(vault, matrixStack, window, partialTicks, player);
         }

         if (rendered) {
            return true;
         }
      }

      if (goals == null) {
         return true;
      } else {
         Minecraft mc = Minecraft.getInstance();
         int tabListOffset = mc.gui.getTabList().visible ? mc.player.connection.getOnlinePlayers().size() * 9 + 10 : 0;
         int totalX = 0;
         int midX = window.getGuiScaledWidth() / 2;
         int gapWidth = 7;
         int itemBoxWidth = 32;
         List<ScavengerGoal> filteredGoals = new ArrayList<>(goals);
         filteredGoals.removeIf(ScavengerGoal::isCompleted);
         int totalWidth = filteredGoals.size() * itemBoxWidth + (filteredGoals.size() - 1) * gapWidth;
         int shiftX = -totalWidth / 2 + itemBoxWidth / 2;
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
         matrixStack.pushPose();
         int yOffset = 0;
         matrixStack.pushPose();
         matrixStack.translate(midX + shiftX, itemBoxWidth * 0.75F, 0.0);
         totalX += midX + shiftX;
         int totalY = (int)(tabListOffset + itemBoxWidth * 0.75);

         for (ScavengerGoal goal : filteredGoals) {
            int reqYOffset = renderItemRequirement(matrixStack, goal, itemBoxWidth, totalX, totalY, partialTicks);
            if (reqYOffset > yOffset) {
               yOffset = reqYOffset;
            }

            matrixStack.translate(itemBoxWidth + gapWidth, 0.0, 0.0);
            totalX += itemBoxWidth + gapWidth;
         }

         matrixStack.popPose();
         matrixStack.popPose();
         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static int renderItemRequirement(PoseStack matrixStack, ScavengerGoal goal, int itemBoxWidth, int totalX, int totalY, float partialTicks) {
      List<ScavengerGoal.Entry> entries = new ArrayList<>();
      goal.getEntries().forEachRemaining(entries::add);
      float time = (float)ClientScheduler.INSTANCE.getTickCount() + partialTicks;
      ScavengerGoal.Entry entry = entries.get((int)(time / 20.0F) % entries.size());
      ItemStack requiredStack = entry.getStack();
      ResourceLocation iconPath = entry.getIcon();
      matrixStack.pushPose();
      matrixStack.translate(0.0, -itemBoxWidth / 2.0F, 0.0);
      totalY = (int)(totalY + -itemBoxWidth / 2.0F);
      renderItemStack(matrixStack, requiredStack, totalX, totalY);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, iconPath);
      matrixStack.pushPose();
      matrixStack.translate(-16.0, -2.4, 0.0);
      matrixStack.scale(0.4F, 0.4F, 1.0F);
      ScreenDrawHelper.drawTexturedQuads(buf -> ScreenDrawHelper.rect(buf, matrixStack).dim(16.0F, 16.0F).draw());
      matrixStack.popPose();
      matrixStack.translate(0.0, 10.0, 0.0);
      String requiredText = goal.get(ScavengerGoal.CURRENT) + "/" + goal.get(ScavengerGoal.TOTAL);
      MutableComponent cmp = new TextComponent(requiredText).withStyle(ChatFormatting.GREEN);
      UIHelper.renderCenteredWrappedText(matrixStack, cmp, 30, 0);
      matrixStack.translate(0.0, 10.0, 0.0);
      matrixStack.pushPose();
      matrixStack.scale(0.5F, 0.5F, 1.0F);
      Component name = requiredStack.getHoverName();
      MutableComponent display = name.copy().withStyle(Style.EMPTY.withColor(entry.getColor()));
      int lines = UIHelper.renderCenteredWrappedText(matrixStack, display, 60, 0);
      matrixStack.popPose();
      matrixStack.popPose();
      return 25 + lines * 5;
   }

   @OnlyIn(Dist.CLIENT)
   private static void renderItemStack(PoseStack renderStack, ItemStack item, int totalX, int totalY) {
      Minecraft mc = Minecraft.getInstance();
      ItemRenderer ir = mc.getItemRenderer();
      Font fr = RenderProperties.get(item).getFont(item);
      if (fr == null) {
         fr = mc.font;
      }

      ir.blitOffset = 200.0F;
      ir.renderAndDecorateItem(item, totalX - 8, totalY - 8);
      ir.renderGuiItemDecorations(fr, item, totalX - 8, totalY - 8, null);
      ir.blitOffset = 0.0F;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void renderPartyInfo(PoseStack matrixStack, UUID playerUUID) {
      super.renderPartyInfo(matrixStack, playerUUID);
      List<ItemStack> scavItems = new ArrayList<>();
      ScavengerGoal.ObjList scavengerGoals = this.get(GOALS).get(playerUUID);
      if (scavengerGoals != null) {
         List<ScavengerGoal> filteredGoals = new ArrayList<>(scavengerGoals);
         filteredGoals.removeIf(ScavengerGoal::isCompleted);
         filteredGoals.forEach(goal -> {
            List<ScavengerGoal.Entry> entries = new ArrayList<>();
            goal.getEntries().forEachRemaining(entries::add);
            float time = (float)ClientScheduler.INSTANCE.getTickCount();
            ScavengerGoal.Entry entry = entries.get((int)(time / 20.0F) % entries.size());
            scavItems.add(entry.getStack(goal.get(ScavengerGoal.TOTAL) - goal.get(ScavengerGoal.CURRENT)));
         });
         if (!scavItems.isEmpty()) {
            matrixStack.translate(-10.0 - (scavItems.size() + 1) * 18.0, 3.0, 100.0);

            for (ItemStack stack : scavItems) {
               matrixStack.pushPose();
               matrixStack.scale(1.0F, -1.0F, 1.0F);
               matrixStack.scale(16.0F, 16.0F, 16.0F);
               Minecraft minecraft = Minecraft.getInstance();
               ItemRenderer itemRenderer = minecraft.getItemRenderer();
               BakedModel bakedModel = itemRenderer.getModel(stack, null, null, 0);
               if (!bakedModel.usesBlockLight()) {
                  Lighting.setupForFlatItems();
               }

               BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
               itemRenderer.render(stack, TransformType.GUI, false, matrixStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
               bufferSource.endBatch();
               if (!bakedModel.usesBlockLight()) {
                  Lighting.setupFor3DItems();
               }

               matrixStack.popPose();
               matrixStack.pushPose();
               matrixStack.translate(9.0, 0.0, 200.0);
               FontHelper.drawTextComponent(matrixStack, new TextComponent(String.valueOf(stack.getCount())), true);
               matrixStack.popPose();
               matrixStack.translate(18.0, 0.0, 0.0);
            }
         }
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

   public static enum Config {
      DEFAULT(() -> ModConfigs.SCAVENGER),
      DIVINE_PARADOX(() -> ModConfigs.DIVINE_PARADOX);

      private Supplier<ScavengerConfig> supplier;

      private Config(Supplier<ScavengerConfig> supplier) {
         this.supplier = supplier;
      }

      public ScavengerConfig get() {
         return this.supplier.get();
      }
   }

   public static class GoalMap extends DataMap<ScavengerObjective.GoalMap, UUID, ScavengerGoal.ObjList> {
      public GoalMap() {
         super(new HashMap<>(), Adapters.UUID, CompoundAdapter.of(ScavengerGoal.ObjList::new));
      }

      public boolean areAllCompleted(Vault vault) {
         for (Runner runner : vault.get(Vault.LISTENERS).getAll(Runner.class)) {
            if (this.containsKey(runner.getId()) && !this.get(runner.getId()).areAllCompleted()) {
               return false;
            }
         }

         return true;
      }
   }
}
