package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
import iskallia.vault.block.model.BloodOrbModel;
import iskallia.vault.entity.boss.BloodOrbEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BloodOrbRenderer extends EntityRenderer<BloodOrbEntity> {
   private BloodOrbModel<BloodOrbEntity> model;

   public BloodOrbRenderer(Context context) {
      super(context);
      this.model = new BloodOrbModel<>(context.bakeLayer(BloodOrbModel.LAYER_LOCATION));
   }

   public void render(BloodOrbEntity entity, float pEntityYaw, float pPartialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
      matrixStack.pushPose();
      matrixStack.translate(0.0, -0.75, 0.0);
      VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
      this.model.renderToBuffer(matrixStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
   }

   public ResourceLocation getTextureLocation(BloodOrbEntity entity) {
      return VaultMod.id("textures/entity/blood_orb.png");
   }
}
