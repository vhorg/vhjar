package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.block.entity.AlchemyArchiveTileEntity;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.Mth;

public class PotionModifierDiscoveryRenderer implements BlockEntityRenderer<AlchemyArchiveTileEntity> {
   public static final Material BOOK_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, VaultMod.id("entity/potion_modifier_book"));
   private final BookModel bookModel;

   public PotionModifierDiscoveryRenderer(Context ctx) {
      this.bookModel = new BookModel(ctx.bakeLayer(ModelLayers.BOOK));
   }

   public void render(AlchemyArchiveTileEntity tile, float pTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay) {
      poseStack.pushPose();
      poseStack.translate(0.6, 0.85, 0.4);
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
      poseStack.scale(0.8F, 0.8F, 0.8F);
      float flipDegree = Mth.lerp(pTicks, tile.oFlip, tile.flip);
      float flipLeft = Mth.frac(flipDegree + 0.25F) * 1.6F - 0.3F;
      float flipRight = Mth.frac(flipDegree + 0.75F) * 1.6F - 0.3F;
      this.bookModel.setupAnim(frameTime, Mth.clamp(flipLeft, 0.0F, 1.0F), Mth.clamp(flipRight, 0.0F, 1.0F), 1.0F);
      VertexConsumer vertexconsumer = BOOK_TEXTURE.buffer(buffers, RenderType::entitySolid);
      this.bookModel.render(poseStack, vertexconsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      poseStack.popPose();
   }
}
