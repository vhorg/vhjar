package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.block.HeraldControllerBlock;
import iskallia.vault.block.entity.HeraldControllerTileEntity;
import iskallia.vault.block.model.HeraldControllerModel;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class HeraldControllerRenderer implements BlockEntityRenderer<HeraldControllerTileEntity> {
   protected final HeraldControllerModel controllerModel;
   private final Minecraft mc = Minecraft.getInstance();

   public HeraldControllerRenderer(Context context) {
      this.controllerModel = new HeraldControllerModel(context.bakeLayer(HeraldControllerModel.MODEL_LOCATION));
   }

   public void render(
      @Nonnull HeraldControllerTileEntity tileEntity,
      float partialTick,
      @Nonnull PoseStack poseStack,
      @Nonnull MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay
   ) {
      BlockState blockState = tileEntity.getBlockState();
      Direction facing = (Direction)blockState.getValue(HeraldControllerBlock.FACING);
      boolean filled = (Boolean)blockState.getValue(HeraldControllerBlock.FILLED);
      Material material = filled ? HeraldControllerModel.FILLED_MATERIAL : HeraldControllerModel.MATERIAL;
      VertexConsumer vertexConsumer = material.buffer(bufferSource, RenderType::entityTranslucent);
      poseStack.pushPose();
      poseStack.translate(0.5, 1.5, 0.5);
      poseStack.mulPose(Vector3f.ZP.rotation((float) Math.PI));
      poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F + facing.toYRot()));
      this.controllerModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.popPose();
   }
}
