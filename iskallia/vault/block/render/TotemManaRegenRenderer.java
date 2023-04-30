package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import iskallia.vault.block.TotemManaRegenBlock;
import iskallia.vault.block.entity.TotemManaRegenTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModRenderTypes;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TotemManaRegenRenderer extends TotemRenderer<TotemManaRegenTileEntity> {
   private final Minecraft minecraft = Minecraft.getInstance();
   private final BlockRenderDispatcher blockRenderDispatcher = this.minecraft.getBlockRenderer();

   public TotemManaRegenRenderer(Context context) {
      super(context);
   }

   @ParametersAreNonnullByDefault
   public void render(
      TotemManaRegenTileEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
      Level level = blockEntity.getLevel();
      if (level != null) {
         TotemManaRegenTileEntity.RenderContext renderContext = blockEntity.getRenderContext();
         if (!Minecraft.getInstance().isPaused()) {
            renderContext.rotation += partialTick * 0.01F;
         }

         BlockPos blockPos = blockEntity.getBlockPos();
         ModelBlockRenderer modelRenderer = this.blockRenderDispatcher.getModelRenderer();
         VertexConsumer solidVertexConsumer = bufferSource.getBuffer(RenderType.solid());
         BlockState blockState = (BlockState)ModBlocks.TOTEM_MANA_REGEN.defaultBlockState().setValue(TotemManaRegenBlock.TYPE, TotemManaRegenBlock.Type.MID);
         poseStack.pushPose();
         poseStack.translate(0.5, 0.0, 0.5);
         poseStack.mulPose(Quaternion.fromXYZ(0.0F, renderContext.rotation, 0.0F));
         poseStack.translate(-0.5, 0.0, -0.5);
         modelRenderer.tesselateBlock(
            level,
            this.blockRenderDispatcher.getBlockModel(blockState),
            blockState,
            blockPos,
            poseStack,
            solidVertexConsumer,
            false,
            level.random,
            0L,
            OverlayTexture.NO_OVERLAY
         );
         poseStack.popPose();
         blockState = (BlockState)ModBlocks.TOTEM_MANA_REGEN.defaultBlockState().setValue(TotemManaRegenBlock.TYPE, TotemManaRegenBlock.Type.TOP);
         poseStack.pushPose();
         poseStack.translate(0.5, 0.0, 0.5);
         poseStack.mulPose(Quaternion.fromXYZ(0.0F, -renderContext.rotation, 0.0F));
         poseStack.translate(-0.5, 0.0, -0.5);
         modelRenderer.tesselateBlock(
            level,
            this.blockRenderDispatcher.getBlockModel(blockState),
            blockState,
            blockPos,
            poseStack,
            solidVertexConsumer,
            false,
            level.random,
            0L,
            OverlayTexture.NO_OVERLAY
         );
         poseStack.popPose();
         blockState = (BlockState)ModBlocks.TOTEM_MANA_REGEN.defaultBlockState().setValue(TotemManaRegenBlock.TYPE, TotemManaRegenBlock.Type.MASKS);
         poseStack.pushPose();
         poseStack.translate(0.0, Mth.sin(renderContext.rotation) * 0.05, 0.0);
         modelRenderer.tesselateBlock(
            level,
            this.blockRenderDispatcher.getBlockModel(blockState),
            blockState,
            blockPos,
            poseStack,
            solidVertexConsumer,
            false,
            level.random,
            0L,
            OverlayTexture.NO_OVERLAY
         );
         poseStack.popPose();
         blockState = (BlockState)ModBlocks.TOTEM_MANA_REGEN.defaultBlockState().setValue(TotemManaRegenBlock.TYPE, TotemManaRegenBlock.Type.FLOATIES);
         poseStack.pushPose();
         poseStack.translate(0.0, Mth.cos(renderContext.rotation) * 0.05, 0.0);
         modelRenderer.tesselateBlock(
            level,
            this.blockRenderDispatcher.getBlockModel(blockState),
            blockState,
            blockPos,
            poseStack,
            solidVertexConsumer,
            false,
            level.random,
            0L,
            OverlayTexture.NO_OVERLAY
         );
         poseStack.popPose();
         VertexConsumer glowVertexConsumer = bufferSource.getBuffer(ModRenderTypes.TOTEM_GLOW_LAYER);
         BlockState blockStatex = (BlockState)ModBlocks.TOTEM_MANA_REGEN
            .defaultBlockState()
            .setValue(TotemManaRegenBlock.TYPE, TotemManaRegenBlock.Type.TOP_GLOW);
         poseStack.pushPose();
         poseStack.translate(0.5, 0.0, 0.5);
         poseStack.mulPose(Quaternion.fromXYZ(0.0F, -renderContext.rotation, 0.0F));
         poseStack.translate(-0.5, 0.0, -0.5);
         modelRenderer.tesselateBlock(
            level,
            this.blockRenderDispatcher.getBlockModel(blockStatex),
            blockStatex,
            blockPos,
            poseStack,
            glowVertexConsumer,
            false,
            level.random,
            0L,
            OverlayTexture.NO_OVERLAY
         );
         poseStack.popPose();
         blockStatex = (BlockState)ModBlocks.TOTEM_MANA_REGEN.defaultBlockState().setValue(TotemManaRegenBlock.TYPE, TotemManaRegenBlock.Type.MASKS_GLOW);
         poseStack.pushPose();
         poseStack.translate(0.0, Mth.sin(renderContext.rotation) * 0.05, 0.0);
         modelRenderer.tesselateBlock(
            level,
            this.blockRenderDispatcher.getBlockModel(blockStatex),
            blockStatex,
            blockPos,
            poseStack,
            glowVertexConsumer,
            false,
            level.random,
            0L,
            OverlayTexture.NO_OVERLAY
         );
         poseStack.popPose();
         blockStatex = (BlockState)ModBlocks.TOTEM_MANA_REGEN.defaultBlockState().setValue(TotemManaRegenBlock.TYPE, TotemManaRegenBlock.Type.MID_GLOW);
         poseStack.pushPose();
         poseStack.translate(0.5, 0.0, 0.5);
         poseStack.mulPose(Quaternion.fromXYZ(0.0F, renderContext.rotation, 0.0F));
         poseStack.translate(-0.5, 0.0, -0.5);
         modelRenderer.tesselateBlock(
            level,
            this.blockRenderDispatcher.getBlockModel(blockStatex),
            blockStatex,
            blockPos,
            poseStack,
            glowVertexConsumer,
            false,
            level.random,
            0L,
            OverlayTexture.NO_OVERLAY
         );
         poseStack.popPose();
      }
   }
}
