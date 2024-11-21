package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.entity.boss.GolemHandProjectileEntity;
import iskallia.vault.entity.model.GolemHandProjectileModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class GolemHandProjectileRenderer extends EntityRenderer<GolemHandProjectileEntity> implements IGeoRenderer<GolemHandProjectileEntity> {
   private static final GolemHandProjectileModel modelProvider = new GolemHandProjectileModel();
   private MultiBufferSource rtb;

   public GolemHandProjectileRenderer(Context context) {
      super(context);
   }

   public void render(
      GolemHandProjectileEntity projectile, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight
   ) {
      poseStack.pushPose();
      poseStack.translate(0.0, 0.05, 0.0);
      poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTick, projectile.yRotO, projectile.getYRot()) - 180.0F));
      poseStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTick, projectile.xRotO, projectile.getXRot())));
      GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(projectile));
      Color renderColor = this.getRenderColor(projectile, partialTick, poseStack, bufferSource, null, packedLight);
      RenderType renderType = this.getRenderType(projectile, partialTick, poseStack, bufferSource, null, packedLight, this.getTextureLocation(projectile));
      this.render(
         model,
         projectile,
         partialTick,
         renderType,
         poseStack,
         bufferSource,
         null,
         packedLight,
         getPackedOverlay(projectile, 0.0F),
         renderColor.getRed() / 255.0F,
         renderColor.getGreen() / 255.0F,
         renderColor.getBlue() / 255.0F,
         renderColor.getAlpha() / 255.0F
      );
      super.render(projectile, entityYaw, partialTick, poseStack, bufferSource, packedLight);
      poseStack.popPose();
   }

   public static int getPackedOverlay(Entity entity, float uIn) {
      return OverlayTexture.pack(OverlayTexture.u(uIn), OverlayTexture.v(false));
   }

   public MultiBufferSource getCurrentRTB() {
      return this.rtb;
   }

   public GeoModelProvider getGeoModelProvider() {
      return modelProvider;
   }

   public void setCurrentRTB(MultiBufferSource bufferSource) {
      this.rtb = bufferSource;
   }

   public ResourceLocation getTextureLocation(GolemHandProjectileEntity projectile) {
      return modelProvider.getTextureLocation(projectile);
   }
}
