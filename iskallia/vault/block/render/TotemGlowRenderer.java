package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.block.entity.TotemTileEntity;
import iskallia.vault.init.ModRenderTypes;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TotemGlowRenderer<T extends TotemTileEntity> extends TotemRenderer<T> {
   private final BlockRenderDispatcher blockRenderDispatcher = Minecraft.getInstance().getBlockRenderer();

   public TotemGlowRenderer(Context context) {
      super(context);
   }

   @ParametersAreNonnullByDefault
   @Override
   public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
      super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
      Level level = blockEntity.getLevel();
      if (level != null) {
         BlockPos blockPos = blockEntity.getBlockPos();
         ModelBlockRenderer modelRenderer = this.blockRenderDispatcher.getModelRenderer();
         BlockState glowBlockState = this.getGlowBlockState();
         VertexConsumer glowVertexConsumer = bufferSource.getBuffer(ModRenderTypes.TOTEM_GLOW_LAYER);
         modelRenderer.tesselateBlock(
            level,
            this.blockRenderDispatcher.getBlockModel(glowBlockState),
            glowBlockState,
            blockPos,
            poseStack,
            glowVertexConsumer,
            false,
            level.random,
            0L,
            OverlayTexture.NO_OVERLAY
         );
      }
   }

   @Nonnull
   protected abstract BlockState getGlowBlockState();
}
