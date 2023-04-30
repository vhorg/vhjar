package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.VaultThrownJavelin;
import iskallia.vault.entity.model.PiercingJavelinModel;
import iskallia.vault.entity.model.ScatterJavelinModel;
import iskallia.vault.entity.model.ScrappyJavelinModel;
import iskallia.vault.entity.model.SightJavelinModel;
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
public class ThrownJavelinRenderer extends EntityRenderer<VaultThrownJavelin> {
   public static final ResourceLocation JAVELIN_LOCATION = VaultMod.id("textures/entity/scrappy_javelin.png");
   public static final ResourceLocation SIGHT_JAVELIN_LOCATION = VaultMod.id("textures/entity/sight_javelin.png");
   public static final ResourceLocation PIERCING_JAVELIN_LOCATION = VaultMod.id("textures/entity/piercing_javelin.png");
   public static final ResourceLocation SCATTER_JAVELIN_LOCATION = VaultMod.id("textures/entity/scatter_javelin.png");
   private final ScrappyJavelinModel model;
   private final ScatterJavelinModel scatterModel;
   private final SightJavelinModel sightModel;
   private final PiercingJavelinModel piercingModel;

   public ThrownJavelinRenderer(Context p_174420_) {
      super(p_174420_);
      this.model = new ScrappyJavelinModel(p_174420_.bakeLayer(ScrappyJavelinModel.MODEL_LOCATION));
      this.scatterModel = new ScatterJavelinModel(p_174420_.bakeLayer(ScatterJavelinModel.MODEL_LOCATION));
      this.sightModel = new SightJavelinModel(p_174420_.bakeLayer(SightJavelinModel.MODEL_LOCATION));
      this.piercingModel = new PiercingJavelinModel(p_174420_.bakeLayer(PiercingJavelinModel.MODEL_LOCATION));
   }

   public void render(VaultThrownJavelin pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      float alpha = (float)(VaultThrownJavelin.MAX_AGE - pEntity.getAge()) / VaultThrownJavelin.MAX_AGE;
      pMatrixStack.pushPose();
      pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
      pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot()) + 90.0F));
      float scale = 1.1F;
      pMatrixStack.scale(scale, scale, scale);
      VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(pEntity)));
      float red = 1.0F;
      if (pEntity.getIsGhost()) {
         red = 0.0F;
         alpha = Mth.clamp(alpha, 0.0F, 0.35F);
      }

      Model model = this.getModel(pEntity);
      if (model != null) {
         model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, red, 1.0F, 1.0F, alpha);
      }

      pMatrixStack.popPose();
      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   public Model getModel(VaultThrownJavelin pEntity) {
      switch (pEntity.getJavelinType()) {
         case BASE:
            return this.model;
         case SCATTER:
            return this.scatterModel;
         case PIERCING:
            return this.piercingModel;
         case SIGHT:
            return this.sightModel;
         default:
            return null;
      }
   }

   public ResourceLocation getTextureLocation(VaultThrownJavelin pEntity) {
      switch (pEntity.getJavelinType()) {
         case BASE:
            return JAVELIN_LOCATION;
         case SCATTER:
            return SCATTER_JAVELIN_LOCATION;
         case PIERCING:
            return PIERCING_JAVELIN_LOCATION;
         case SIGHT:
            return SIGHT_JAVELIN_LOCATION;
         default:
            return JAVELIN_LOCATION;
      }
   }
}
