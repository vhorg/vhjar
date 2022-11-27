package iskallia.vault.entity.renderer.elite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.model.ModModelLayers;
import javax.annotation.Nonnull;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EliteEnderOrnamentLayer<T extends EnderMan> extends RenderLayer<T, EndermanModel<T>> {
   public static final ResourceLocation ORNAMENT_TEXTURE = VaultMod.id("textures/entity/elite/enderman_ornament.png");
   protected float angleOffset;
   protected EliteEnderOrnamentLayer.EnderOrnamentModel model;

   public EliteEnderOrnamentLayer(RenderLayerParent<T, EndermanModel<T>> renderer, EntityModelSet modelSet, float angleOffset) {
      super(renderer);
      this.angleOffset = angleOffset;
      this.model = new EliteEnderOrnamentLayer.EnderOrnamentModel(modelSet.bakeLayer(ModModelLayers.ELITE_ENDERMAN_ORNAMENT));
   }

   public void render(
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource bufferSource,
      int pPackedLight,
      @Nonnull T entity,
      float pLimbSwing,
      float pLimbSwingAmount,
      float pPartialTicks,
      float pAgeInTicks,
      float pNetHeadYaw,
      float pHeadPitch
   ) {
      matrixStack.pushPose();
      matrixStack.translate(0.0, -2.0, 0.0);
      float radius = 0.8F;
      float angle = this.angleOffset + pAgeInTicks * 3.5F;
      matrixStack.mulPose(Vector3f.XP.rotationDegrees(30.0F));
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(angle));
      matrixStack.translate(radius, 0.0, 0.0);
      matrixStack.mulPose(Vector3f.ZN.rotationDegrees(angle));
      matrixStack.mulPose(Vector3f.XN.rotationDegrees(30.0F));
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(pNetHeadYaw));
      matrixStack.mulPose(Vector3f.XP.rotationDegrees(pHeadPitch));
      float scale = 0.75F;
      matrixStack.scale(scale, scale, scale);
      VertexConsumer vertexConsumer = bufferSource.getBuffer(this.model.renderType(ORNAMENT_TEXTURE));
      this.model.renderToBuffer(matrixStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.popPose();
   }

   public static class EnderOrnamentModel extends Model {
      private final ModelPart body;

      public EnderOrnamentModel(ModelPart root) {
         super(RenderType::entityCutoutNoCull);
         this.body = root.getChild("body");
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = new MeshDefinition();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F))
               .texOffs(4, 21)
               .addBox(-2.0F, -3.0F, -1.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 24.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 32, 32);
      }

      public void renderToBuffer(
         @Nonnull PoseStack matrixStack,
         @Nonnull VertexConsumer vertexConsumer,
         int packedLight,
         int packedOverlay,
         float red,
         float green,
         float blue,
         float alpha
      ) {
         this.body.render(matrixStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
      }
   }
}
