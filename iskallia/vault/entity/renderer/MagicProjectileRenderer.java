package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.boss.MagicProjectileEntity;
import iskallia.vault.entity.model.FireballModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MagicProjectileRenderer extends EntityRenderer<MagicProjectileEntity> {
   public static final ResourceLocation FIREBALL = VaultMod.id("textures/entity/artifact_boss_magic_attack.png");
   private final FireballModel model;

   public MagicProjectileRenderer(Context context) {
      super(context);
      this.model = new FireballModel(context.bakeLayer(FireballModel.MODEL_LOCATION));
   }

   public void render(MagicProjectileEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      float alpha = 1.0F;
      if (pEntity.tickCount > 3) {
         int col = pEntity.getColor();
         float r = (col >> 16 & 0xFF) / 255.0F;
         float g = (col >> 8 & 0xFF) / 255.0F;
         float b = (col & 0xFF) / 255.0F;
         pMatrixStack.pushPose();
         pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(pEntity.getYRot() - 90.0F));
         pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot()) + 90.0F));
         pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(pPartialTicks, (pEntity.tickCount - 1) * 15.0F, pEntity.tickCount * 15.0F)));
         float scale = 1.1F;
         pMatrixStack.scale(scale, scale, scale);
         VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(pEntity)));
         if (this.model != null) {
            this.model.renderToBuffer(pMatrixStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, r, g, b, alpha);
         }

         pMatrixStack.popPose();
         super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
      }
   }

   public ResourceLocation getTextureLocation(MagicProjectileEntity pEntity) {
      return FIREBALL;
   }

   public Model getModel(MagicProjectileEntity pEntity) {
      return this.model;
   }
}
