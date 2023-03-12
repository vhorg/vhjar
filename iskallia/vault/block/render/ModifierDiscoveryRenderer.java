package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.ModifierDiscoveryTileEntity;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.util.Mth;

public class ModifierDiscoveryRenderer implements BlockEntityRenderer<ModifierDiscoveryTileEntity> {
   private final BookModel bookModel;

   public ModifierDiscoveryRenderer(Context ctx) {
      this.bookModel = new BookModel(ctx.bakeLayer(ModelLayers.BOOK));
   }

   public void render(ModifierDiscoveryTileEntity tile, float pTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
      poseStack.pushPose();
      poseStack.translate(0.5, 1.0, 0.5);
      float frameTime = tile.time + pTicks;
      poseStack.translate(0.0, 0.1F + Mth.sin(frameTime * 0.1F) * 0.01F, 0.0);
      float rotationDiff = tile.rot - tile.oRot;

      while (rotationDiff >= (float) Math.PI) {
         rotationDiff -= (float) (Math.PI * 2);
      }

      while (rotationDiff < (float) -Math.PI) {
         rotationDiff += (float) (Math.PI * 2);
      }

      float iRotation = tile.oRot + rotationDiff * pTicks;
      poseStack.mulPose(Vector3f.YP.rotation(-iRotation));
      poseStack.mulPose(Vector3f.ZP.rotationDegrees(80.0F));
      float flipDegree = Mth.lerp(pTicks, tile.oFlip, tile.flip);
      float flipLeft = Mth.frac(flipDegree + 0.25F) * 1.6F - 0.3F;
      float flipRight = Mth.frac(flipDegree + 0.75F) * 1.6F - 0.3F;
      this.bookModel.setupAnim(frameTime, Mth.clamp(flipLeft, 0.0F, 1.0F), Mth.clamp(flipRight, 0.0F, 1.0F), 1.0F);
      VertexConsumer vertexconsumer = EnchantTableRenderer.BOOK_LOCATION.buffer(buffers, RenderType::entitySolid);
      this.bookModel.render(poseStack, vertexconsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.popPose();
   }
}
