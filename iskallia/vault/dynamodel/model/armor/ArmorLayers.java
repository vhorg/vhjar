package iskallia.vault.dynamodel.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   public abstract Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot var1);

   @OnlyIn(Dist.CLIENT)
   public abstract ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot var1);

   @OnlyIn(Dist.CLIENT)
   public static ModelLayerLocation createLayerLocation(EquipmentSlot equipmentSlot, ResourceLocation id) {
      return switch (equipmentSlot) {
         case HEAD -> new ModelLayerLocation(new ResourceLocation(id.getNamespace(), id.getPath() + "_helmet"), "main");
         case CHEST -> new ModelLayerLocation(new ResourceLocation(id.getNamespace(), id.getPath() + "_chestplate"), "main");
         case LEGS -> new ModelLayerLocation(new ResourceLocation(id.getNamespace(), id.getPath() + "_leggings"), "main");
         case FEET -> new ModelLayerLocation(new ResourceLocation(id.getNamespace(), id.getPath() + "_boots"), "main");
         default -> throw new InternalError("Invalid armor slot -> " + equipmentSlot);
      };
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class BaseLayer extends HumanoidModel<LivingEntity> {
      protected ArmorPieceModel definition;

      public BaseLayer(ArmorPieceModel associatedDefinition, ModelPart root) {
         super(root);
         this.definition = associatedDefinition;
      }

      public ArmorPieceModel getDefinition() {
         return this.definition;
      }

      protected void adjustForRender(
         PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
      ) {
      }

      public static MeshDefinition createBaseLayer() {
         MeshDefinition meshDefinition = new MeshDefinition();
         PartDefinition partDefinition = meshDefinition.getRoot();
         partDefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
         partDefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
         partDefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
         partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
         partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
         partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
         partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
         return meshDefinition;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LeggingsLayer extends ArmorLayers.BaseLayer {
      protected final ModelPart body;
      protected final ModelPart right_leg;
      protected final ModelPart left_leg;

      public LeggingsLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
         this.body = root.getChild("body");
         this.right_leg = root.getChild("right_leg");
         this.left_leg = root.getChild("left_leg");
      }

      public void renderToBuffer(
         @Nonnull PoseStack poseStack,
         @Nonnull VertexConsumer vertexConsumer,
         int packedLight,
         int packedOverlay,
         float red,
         float green,
         float blue,
         float alpha
      ) {
         poseStack.pushPose();
         this.adjustForRender(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         this.right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         this.left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         poseStack.popPose();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MainLayer extends ArmorLayers.BaseLayer {
      protected final ModelPart head;
      protected final ModelPart body;
      protected final ModelPart rightArm;
      protected final ModelPart leftArm;
      protected final ModelPart rightLeg;
      protected final ModelPart leftLeg;

      public MainLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
         this.head = root.getChild("head");
         this.body = root.getChild("body");
         this.rightArm = root.getChild("right_arm");
         this.leftArm = root.getChild("left_arm");
         this.rightLeg = root.getChild("right_leg");
         this.leftLeg = root.getChild("left_leg");
      }

      public ModelPart getRightArm() {
         return this.rightArm;
      }

      public ModelPart getLeftArm() {
         return this.leftArm;
      }

      public void renderToBuffer(
         @Nonnull PoseStack poseStack,
         @Nonnull VertexConsumer vertexConsumer,
         int packedLight,
         int packedOverlay,
         float red,
         float green,
         float blue,
         float alpha
      ) {
         poseStack.pushPose();
         this.adjustForRender(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         if (this.definition.getEquipmentSlot() == EquipmentSlot.HEAD) {
            this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         } else if (this.definition.getEquipmentSlot() == EquipmentSlot.CHEST) {
            this.body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            this.rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            this.leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         } else if (this.definition.getEquipmentSlot() == EquipmentSlot.FEET) {
            this.rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            this.leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         }

         poseStack.popPose();
      }

      public void adjustForFirstPersonRender(@Nonnull PoseStack poseStack) {
      }
   }

   @OnlyIn(Dist.CLIENT)
   @FunctionalInterface
   public interface VaultArmorLayerSupplier<T extends ArmorLayers.BaseLayer> {
      T supply(ArmorPieceModel var1, ModelPart var2);
   }
}
