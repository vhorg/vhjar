package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.VaultFireball;
import iskallia.vault.entity.model.FireballModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireballRenderer extends EntityRenderer<VaultFireball> {
   public static final ResourceLocation FIREBALL = VaultMod.id("textures/entity/fireball.png");
   private final FireballModel model;

   public FireballRenderer(Context p_174420_) {
      super(p_174420_);
      this.model = new FireballModel(p_174420_.bakeLayer(FireballModel.MODEL_LOCATION));
   }

   public void render(VaultFireball pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      float alpha = (float)(VaultFireball.MAX_AGE - pEntity.getAge()) / VaultFireball.MAX_AGE;
      pMatrixStack.pushPose();
      pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
      pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot()) + 90.0F));
      pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(pPartialTicks, (pEntity.tickCount - 1) * 15.0F, pEntity.tickCount * 15.0F)));
      float scale = 1.1F;
      if (pEntity.getFireballType() == VaultFireball.FireballType.FIRESHOT) {
         scale = 0.5F;
      }

      pMatrixStack.scale(scale, scale, scale);
      VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(pEntity)));
      float red = 1.0F;
      Model model = this.getModel(pEntity);
      if (model != null) {
         model.renderToBuffer(pMatrixStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, red, 1.0F, 1.0F, alpha);
      }

      pMatrixStack.popPose();
      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   public Model getModel(VaultFireball pEntity) {
      return this.model;
   }

   public ResourceLocation getTextureLocation(VaultFireball pEntity) {
      return FIREBALL;
   }
}
