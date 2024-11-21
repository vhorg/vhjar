package iskallia.vault.entity.renderer.plastic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.plastic.PlasticSlimeEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.plastic.PlasticSlimeModel;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class PlasticSlimeRenderer extends MobRenderer<PlasticSlimeEntity, PlasticSlimeModel> {
   public static final Map<Integer, ResourceLocation> TEXTURES = Map.of(
      1,
      VaultMod.id("textures/entity/plastic/slime/slime_plastic_t1.png"),
      2,
      VaultMod.id("textures/entity/plastic/slime/slime_plastic_t2.png"),
      3,
      VaultMod.id("textures/entity/plastic/slime/slime_plastic_t3.png"),
      4,
      VaultMod.id("textures/entity/plastic/slime/slime_plastic_t4.png")
   );

   public PlasticSlimeRenderer(Context context) {
      super(context, new PlasticSlimeModel(context.bakeLayer(ModModelLayers.PLASTIC_SLIME)), 0.25F);
      this.addLayer(new PlasticSlimeRenderer.PlasticSlimeOuterLayer(this, context.getModelSet()));
   }

   public void render(
      PlasticSlimeEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight
   ) {
      this.shadowRadius = 0.25F * entity.getSize();
      super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
   }

   protected void scale(PlasticSlimeEntity slime, PoseStack poseStack, float partialTicks) {
      float scale = 0.999F;
      poseStack.scale(scale, scale, scale);
      poseStack.translate(0.0, 0.001F, 0.0);
      float size = slime.getSize();
      float squishAmount = Mth.lerp(partialTicks, slime.oSquish, slime.squish) / (size * 0.5F + 1.0F);
      float squishFactor = 1.0F / (squishAmount + 1.0F);
      poseStack.scale(squishFactor * size, 1.0F / squishFactor * size, squishFactor * size);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull PlasticSlimeEntity slime) {
      return TEXTURES.get(slime.getTier());
   }

   public static class PlasticSlimeOuterLayer extends RenderLayer<PlasticSlimeEntity, PlasticSlimeModel> {
      private final EntityModel<PlasticSlimeEntity> model;

      public PlasticSlimeOuterLayer(RenderLayerParent<PlasticSlimeEntity, PlasticSlimeModel> renderer, EntityModelSet modelSet) {
         super(renderer);
         this.model = new PlasticSlimeModel(modelSet.bakeLayer(ModModelLayers.PLASTIC_SLIME_OUTER));
      }

      public void render(
         @NotNull PoseStack poseStack,
         @NotNull MultiBufferSource buffer,
         int packedLight,
         @NotNull PlasticSlimeEntity slime,
         float limbSwing,
         float limbSwingAmount,
         float partialTicks,
         float age,
         float headYaw,
         float headPitch
      ) {
         Minecraft minecraft = Minecraft.getInstance();
         boolean isInvisible = slime.isInvisible();
         boolean isGlowingAndInvisible = minecraft.shouldEntityAppearGlowing(slime) && isInvisible;
         if (!isInvisible || isGlowingAndInvisible) {
            VertexConsumer vertexConsumer;
            if (isGlowingAndInvisible) {
               vertexConsumer = buffer.getBuffer(RenderType.outline(this.getTextureLocation(slime)));
            } else {
               vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(slime)));
            }

            ((PlasticSlimeModel)this.getParentModel()).copyPropertiesTo(this.model);
            this.model.prepareMobModel(slime, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(slime, limbSwing, limbSwingAmount, age, headYaw, headPitch);
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, LivingEntityRenderer.getOverlayCoords(slime, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}
