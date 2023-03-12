package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.LodestoneTileEntity;
import iskallia.vault.block.model.PylonCrystalModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;

public class LodestoneRenderer implements BlockEntityRenderer<LodestoneTileEntity> {
   protected final PylonCrystalModel crystalModel;

   public LodestoneRenderer(Context context) {
      this.crystalModel = new PylonCrystalModel(context.bakeLayer(PylonCrystalModel.MODEL_LOCATION));
   }

   public void render(
      @Nonnull LodestoneTileEntity tileEntity,
      float partialTick,
      @Nonnull PoseStack poseStack,
      @Nonnull MultiBufferSource bufferSource,
      int packetLight,
      int packetOverlay
   ) {
      int a = tileEntity.isConsumed() ? 32 : 255;
      VertexConsumer vertexConsumer = PylonCrystalModel.MATERIAL.buffer(bufferSource, RenderType::entityTranslucent);
      poseStack.pushPose();
      poseStack.translate(0.5, 1.5, 0.5);
      poseStack.mulPose(Vector3f.ZP.rotation((float) Math.PI));
      this.crystalModel.setupAnimations();
      this.crystalModel.renderToBuffer(poseStack, vertexConsumer, packetLight, packetOverlay, 1.0F, 1.0F, 1.0F, a / 255.0F);
      poseStack.popPose();
   }
}
