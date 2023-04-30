package iskallia.vault.core.vault.objective;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.block.MonolithBlock;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.client.gui.helper.LightmapHelper;
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
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.MonolithIgniteMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class MonolithObjective extends Objective {
   public static final SupplierKey<Objective> KEY = SupplierKey.of("monolith", Objective.class).with(Version.v1_2, MonolithObjective::new);
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

   protected MonolithObjective() {
   }

   protected MonolithObjective(int target, float objectiveProbability) {
      this.set(COUNT, Integer.valueOf(0));
      this.set(TARGET, Integer.valueOf(target));
      this.set(OBJECTIVE_PROBABILITY, Float.valueOf(objectiveProbability));
   }

   public static MonolithObjective of(int target, float objectiveProbability) {
      return new MonolithObjective(target, objectiveProbability);
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
      CommonEvents.BLOCK_USE
         .in(world)
         .at(BlockUseEvent.Phase.HEAD)
         .of(ModBlocks.MONOLITH)
         .register(
            this,
            data -> {
               if (data.getHand() != InteractionHand.MAIN_HAND) {
                  data.setResult(InteractionResult.SUCCESS);
               } else if (this.get(COUNT) < this.get(TARGET)) {
                  BlockPos pos = data.getPos();
                  if (!(Boolean)data.getState().getValue(MonolithBlock.FILLED)) {
                     if (data.getState().getValue(MonolithBlock.HALF) != DoubleBlockHalf.UPPER
                        || world.getBlockState(pos = pos.below()).getBlock() == ModBlocks.MONOLITH) {
                        if (vault.get(Vault.LISTENERS).getObjectivePriority(data.getPlayer().getUUID(), this) == 0) {
                           world.setBlock(pos, (BlockState)world.getBlockState(pos).setValue(MonolithBlock.FILLED, true), 3);
                           world.setBlock(pos.above(), (BlockState)world.getBlockState(pos.above()).setValue(MonolithBlock.FILLED, true), 3);
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
               }
            }
         );
      CommonEvents.BLOCK_SET
         .at(BlockSetEvent.Type.RETURN)
         .in(world)
         .register(
            this,
            data -> {
               PartialTile target = PartialTile.of(PartialBlockState.of(ModBlocks.PLACEHOLDER), PartialCompoundNbt.empty());
               target.getState().set(PlaceholderBlock.TYPE, PlaceholderBlock.Type.OBJECTIVE);
               if (target.isSubsetOf(PartialTile.of(data.getState()))) {
                  data.getWorld()
                     .setBlock(
                        data.getPos(),
                        (BlockState)((BlockState)ModBlocks.MONOLITH.defaultBlockState().setValue(MonolithBlock.HALF, DoubleBlockHalf.LOWER))
                           .setValue(MonolithBlock.FILLED, false),
                        3
                     );
               }
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

      int midX = window.getGuiScaledWidth() / 2;
      Font font = Minecraft.getInstance().font;
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      Component txt = new TextComponent("Find the Monoliths!").withStyle(ChatFormatting.WHITE);
      font.drawInBatch(
         txt.getVisualOrderText(),
         midX - font.width(txt) / 2.0F,
         6.0F,
         -1,
         true,
         matrixStack.last().pose(),
         buffer,
         false,
         0,
         LightmapHelper.getPackedFullbrightCoords()
      );
      buffer.endBatch();
      int gapWidth = 4;
      int itemBoxWidth = 16;
      int iconWidth = 12;
      int iconHeight = 22;
      int totalWidth = this.get(TARGET) * itemBoxWidth + (this.get(TARGET) - 1) * gapWidth;
      int shiftX = -totalWidth / 2 + 2;
      matrixStack.pushPose();
      matrixStack.translate(midX + shiftX, 25.0, 0.0);

      for (int i = 0; i < this.get(TARGET); i++) {
         matrixStack.pushPose();
         matrixStack.translate(0.0, -itemBoxWidth / 2.0F, 0.0);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, VaultOverlay.VAULT_HUD);
         if (i < this.get(COUNT)) {
            GuiComponent.blit(matrixStack, 0, 0, 103.0F, 84.0F, iconWidth, iconHeight, 256, 256);
         } else {
            GuiComponent.blit(matrixStack, 0, 0, 90.0F, 84.0F, iconWidth, iconHeight, 256, 256);
         }

         matrixStack.popPose();
         matrixStack.translate(itemBoxWidth + gapWidth, 0.0, 0.0);
      }

      matrixStack.popPose();
      return true;
   }

   @Override
   public boolean isActive(Vault vault, Objective objective) {
      if (this.get(COUNT) < this.get(TARGET)) {
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

   protected void playActivationEffects(VirtualWorld world, BlockPos pos) {
      ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new MonolithIgniteMessage(pos));
      world.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
   }
}
