package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.block.ObeliskBlock;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.overlay.AbilitiesOverlay;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.SupplierKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.BlockSetEvent;
import iskallia.vault.core.event.common.BlockUseEvent;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.overlay.VaultOverlay;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Runner;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LegacyObeliskObjective extends Objective {
   public static final FieldRegistry FIELDS = Objective.FIELDS.merge(new FieldRegistry());
   public static final FieldKey<Integer> COUNT = FieldKey.of("count", Integer.class)
      .with(Version.v1_0, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Integer> TARGET = FieldKey.of("target", Integer.class)
      .with(Version.v1_0, Adapters.INT_SEGMENTED_3, DISK.all().or(CLIENT.all()))
      .register(FIELDS);
   public static final FieldKey<Float> OBJECTIVE_PROBABILITY = FieldKey.of("objective_probability", Float.class)
      .with(Version.v1_2, Adapters.FLOAT, DISK.all())
      .register(FIELDS);

   protected LegacyObeliskObjective() {
   }

   protected LegacyObeliskObjective(int target, float objectiveProbability) {
      this.set(COUNT, Integer.valueOf(0));
      this.set(TARGET, Integer.valueOf(target));
      this.set(OBJECTIVE_PROBABILITY, Float.valueOf(objectiveProbability));
   }

   @Deprecated
   public static LegacyObeliskObjective of(int target, float objectiveProbability) {
      return new LegacyObeliskObjective(target, objectiveProbability);
   }

   @Override
   public SupplierKey<Objective> getKey() {
      return ObeliskObjective.KEY;
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault) {
      CommonEvents.OBJECTIVE_PIECE_GENERATION.register(this, data -> {
         if (data.getVault() == vault) {
            this.ifPresent(OBJECTIVE_PROBABILITY, probability -> data.setProbability(probability.floatValue()));
         }
      });
      CommonEvents.BLOCK_USE
         .in(world)
         .at(BlockUseEvent.Phase.HEAD)
         .of(ModBlocks.OBELISK)
         .register(
            this,
            data -> {
               if (data.getHand() != InteractionHand.MAIN_HAND) {
                  data.setResult(InteractionResult.SUCCESS);
               } else if (this.get(COUNT) < this.get(TARGET)) {
                  BlockPos pos = data.getPos();
                  if ((Boolean)data.getState().getValue(ObeliskBlock.FILLED)) {
                     data.setResult(InteractionResult.SUCCESS);
                  } else if (data.getState().getValue(ObeliskBlock.HALF) == DoubleBlockHalf.UPPER
                     && world.getBlockState(pos = pos.below()).getBlock() != ModBlocks.OBELISK) {
                     data.setResult(InteractionResult.SUCCESS);
                  } else if (vault.get(Vault.LISTENERS).getObjectivePriority(data.getPlayer().getUUID(), this) != 0) {
                     data.setResult(InteractionResult.SUCCESS);
                  } else {
                     world.setBlock(pos, (BlockState)world.getBlockState(pos).setValue(ObeliskBlock.FILLED, true), 3);
                     world.setBlock(pos.above(), (BlockState)world.getBlockState(pos.above()).setValue(ObeliskBlock.FILLED, true), 3);
                     this.playActivationEffects(world, pos);
                     this.set(COUNT, Integer.valueOf(this.get(COUNT) + 1));

                     for (Objective objective : this.get(CHILDREN)) {
                        if (objective instanceof KillBossObjective killBoss) {
                           killBoss.set(KillBossObjective.BOSS_POS, pos);
                        }
                     }

                     data.setResult(InteractionResult.SUCCESS);
                  }
               }
            }
         );
      BlockState targetState = (BlockState)ModBlocks.PLACEHOLDER.defaultBlockState().setValue(PlaceholderBlock.TYPE, PlaceholderBlock.Type.OBJECTIVE);
      CommonEvents.BLOCK_SET
         .at(BlockSetEvent.Type.RETURN)
         .of(targetState)
         .in(world)
         .register(
            this,
            data -> {
               BlockPos pos = data.getPos();
               BlockState lower = (BlockState)((BlockState)ModBlocks.OBELISK.defaultBlockState().setValue(ObeliskBlock.HALF, DoubleBlockHalf.LOWER))
                  .setValue(ObeliskBlock.FILLED, false);
               BlockState upper = (BlockState)((BlockState)ModBlocks.OBELISK.defaultBlockState().setValue(ObeliskBlock.HALF, DoubleBlockHalf.UPPER))
                  .setValue(ObeliskBlock.FILLED, false);
               data.getWorld().setBlock(pos, lower, 3);
               data.getWorld().setBlock(pos.above(), upper, 3);
            }
         );
      super.initServer(world, vault);
   }

   @Override
   public void tickServer(VirtualWorld world, Vault vault) {
      if (this.get(COUNT) >= this.get(TARGET)) {
         super.tickServer(world, vault);
      }
   }

   @Override
   public void tickListener(VirtualWorld world, Vault vault, Listener listener) {
      if (listener.getPriority(this) < 0) {
         listener.addObjective(vault, this);
      }

      if (listener instanceof Runner && this.get(COUNT) >= this.get(TARGET)) {
         super.tickListener(world, vault, listener);
      }
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public boolean render(Vault vault, PoseStack matrixStack, Window window, float partialTicks, Player player) {
      if (this.get(COUNT) >= this.get(TARGET)) {
         boolean rendered = false;

         for (Objective objective : this.get(CHILDREN)) {
            rendered |= objective.render(vault, matrixStack, window, partialTicks, player);
         }

         if (rendered) {
            return true;
         }
      }

      matrixStack.pushPose();
      matrixStack.translate(15.0, window.getGuiScaledHeight() - 34, 0.0);
      if (AbilitiesOverlay.ABILITY_DATA.shouldRender) {
         matrixStack.translate(0.0, -12.0, 0.0);
      }

      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      FormattedCharSequence text = new TextComponent("Obelisks").withStyle(ChatFormatting.BOLD).getVisualOrderText();
      Minecraft.getInstance()
         .font
         .drawInBatch(text, 0.0F, 0.0F, -1, true, matrixStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords());
      buffer.endBatch();
      if (this.get(TARGET) <= 0) {
         return false;
      } else {
         float scale = 0.6F;
         float gap = 2.0F;
         float margin = 2.0F;
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         int previousTexture = RenderSystem.getShaderTexture(0);
         RenderSystem.setShaderTexture(0, VaultOverlay.VAULT_HUD);
         int iconWidth = 12;
         int iconHeight = 22;
         matrixStack.pushPose();
         matrixStack.translate(0.0, -margin, 0.0);
         matrixStack.translate(0.0, -scale * iconHeight, 0.0);
         matrixStack.scale(scale, scale, scale);

         for (int i = 0; i < this.get(COUNT); i++) {
            GuiComponent.blit(matrixStack, 0, 0, 77.0F, 84.0F, iconWidth, iconHeight, 256, 256);
            matrixStack.translate(scale * gap + iconWidth, 0.0, 0.0);
         }

         for (int i = 0; i < this.get(TARGET) - this.get(COUNT); i++) {
            GuiComponent.blit(matrixStack, 0, 0, 64.0F, 84.0F, iconWidth, iconHeight, 256, 256);
            matrixStack.translate(scale * gap + iconWidth, 0.0, 0.0);
         }

         matrixStack.popPose();
         matrixStack.popPose();
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, previousTexture);
         return true;
      }
   }

   @Override
   public boolean isActive(VirtualWorld world, Vault vault, Objective objective) {
      if (this.get(COUNT) < this.get(TARGET)) {
         return objective == this;
      } else {
         for (Objective child : this.get(CHILDREN)) {
            if (child.isActive(world, vault, objective)) {
               return true;
            }
         }

         return false;
      }
   }

   protected void playActivationEffects(VirtualWorld world, BlockPos pos) {
      for (int i = 0; i < 20; i++) {
         double d0 = world.random.nextGaussian() * 0.02;
         double d1 = world.random.nextGaussian() * 0.02;
         double d2 = world.random.nextGaussian() * 0.02;
         world.sendParticles(
            ParticleTypes.POOF,
            pos.getX() + world.random.nextDouble() - d0,
            pos.getY() + world.random.nextDouble() - d1,
            pos.getZ() + world.random.nextDouble() - d2,
            10,
            d0,
            d1,
            d2,
            1.0
         );
      }

      world.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
   }
}
