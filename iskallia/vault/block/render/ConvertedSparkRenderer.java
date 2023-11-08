package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.ConvertedSparkTileEntity;
import iskallia.vault.block.model.WendarrChainModel;
import iskallia.vault.client.util.ClientScheduler;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class ConvertedSparkRenderer implements BlockEntityRenderer<ConvertedSparkTileEntity> {
   WendarrChainModel chain;

   public ConvertedSparkRenderer(Context context) {
      this.chain = new WendarrChainModel(context.bakeLayer(WendarrChainModel.LAYER_LOCATION));
   }

   public void render(
      ConvertedSparkTileEntity tile,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = tile.getLevel();
      if (world != null) {
         float t = 1.0F - (float)(ClientScheduler.INSTANCE.getTickCount() % 2400L) / 2400.0F;
         float t2 = 1.0F - (float)((ClientScheduler.INSTANCE.getTickCount() + 1L) % 2400L) / 2400.0F;
         float t3 = Mth.lerp(partialTicks, t, t2);
         matrixStack.pushPose();
         matrixStack.translate(0.5, 0.5, 0.5);
         matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float)Math.sin(t3 / 30.0F) * 50.0F));
         matrixStack.mulPose(Vector3f.YP.rotationDegrees(360.0F * t3 * 40.0F + tile.getOffsetTicks()));
         matrixStack.mulPose(Vector3f.XP.rotationDegrees((float)Math.sin(t3 / 20.0F) * 90.0F));
         matrixStack.translate(-0.5, -0.5, -0.5);
         VertexConsumer vertexConsumer = WendarrChainModel.MATERIAL.buffer(buffer, RenderType::entityTranslucent);
         this.chain.renderToBuffer(matrixStack, vertexConsumer, 15728880, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
         matrixStack.popPose();
      }
   }
}
