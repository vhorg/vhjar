package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import iskallia.vault.block.entity.AnimatrixTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AnimatrixRenderer implements BlockEntityRenderer<AnimatrixTileEntity> {
   private final Context context;
   private final EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

   public AnimatrixRenderer(Context context) {
      this.context = context;
   }

   public void render(
      AnimatrixTileEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay
   ) {
      if (pBlockEntity.getEntityToRender() != null) {
         Entity entityToRender = pBlockEntity.getEntityToRender();
         entityToRender.noPhysics = true;
         pPoseStack.pushPose();
         pPoseStack.translate(0.5, 1.0, 0.5);
         pPoseStack.mulPose(
            new Quaternion(0.0F, -((Direction)pBlockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)).toYRot(), 0.0F, true)
         );
         pPoseStack.scale(0.9F, 0.9F, 0.9F);
         this.entityRenderDispatcher.render(entityToRender, 0.0, 0.0, 0.0, 0.0F, 1.0F, pPoseStack, pBufferSource, pPackedLight);
         pPoseStack.popPose();
      }
   }
}
