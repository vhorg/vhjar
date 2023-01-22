package iskallia.vault.gear.model.armor.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.util.ModelPartHelper;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArmourVelaraArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ArmourVelaraArmorLayers.LeggingsLayer::createBodyLayer : ArmourVelaraArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ArmourVelaraArmorLayers.LeggingsLayer::new : ArmourVelaraArmorLayers.MainLayer::new;
   }

   @OnlyIn(Dist.CLIENT)
   public static class LeggingsLayer extends ArmorLayers.LeggingsLayer {
      public LeggingsLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -1.0F, -2.5F, 9.0F, 13.0F, 5.0F, new CubeDeformation(0.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(21, 19).addBox(-2.4F, -0.5F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 19).addBox(-2.6F, -0.5F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MainLayer extends ArmorLayers.MainLayer {
      protected ModelPart leftWing;
      protected ModelPart rightWing;

      public MainLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
         ModelPart body = root.getChild("body");
         this.leftWing = body.getChild("left_wing");
         this.rightWing = body.getChild("right_wing");
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
               .texOffs(0, 21)
               .addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(-0.2F))
               .texOffs(0, 0)
               .addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(43, 0)
               .addBox(-6.0F, -13.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(29, 105).addBox(-1.0F, -2.0F, -3.0F, 5.0F, 14.0F, 6.0F, new CubeDeformation(0.25F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition flowerlayer_r1 = left_arm.addOrReplaceChild(
            "flowerlayer_r1",
            CubeListBuilder.create()
               .texOffs(31, 88)
               .addBox(-4.25F, -6.25F, -3.5F, 7.0F, 9.0F, 7.0F, new CubeDeformation(0.4F))
               .texOffs(77, 103)
               .addBox(-4.25F, -6.25F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.26F)),
            PartPose.offsetAndRotation(2.5F, -0.5F, 0.0F, 0.0F, 0.0F, 0.2618F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(54, 103).addBox(-4.0F, -2.0F, -3.0F, 5.0F, 14.0F, 6.0F, new CubeDeformation(0.25F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition flowerlayer_r2 = right_arm.addOrReplaceChild(
            "flowerlayer_r2",
            CubeListBuilder.create()
               .texOffs(86, 43)
               .addBox(-2.75F, -6.25F, -3.5F, 7.0F, 9.0F, 7.0F, new CubeDeformation(0.4F))
               .texOffs(99, 0)
               .addBox(-2.75F, -6.25F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.26F)),
            PartPose.offsetAndRotation(-2.5F, -0.5F, 0.0F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 85)
               .addBox(-4.5F, 0.0F, -3.0F, 9.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(86, 23)
               .addBox(-4.5F, 0.0F, -3.0F, 9.0F, 13.0F, 6.0F, new CubeDeformation(0.26F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_wing = body.addOrReplaceChild(
            "left_wing", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.0F, 3.25F, 0.0F, 0.2182F, 0.0F)
         );
         PartDefinition vine_r1 = left_wing.addOrReplaceChild(
            "vine_r1",
            CubeListBuilder.create()
               .texOffs(117, 27)
               .addBox(-2.5F, -3.0F, 12.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.2F))
               .texOffs(43, 19)
               .addBox(-1.5F, -2.0F, -1.5F, 3.0F, 3.0F, 16.0F, new CubeDeformation(0.21F))
               .texOffs(43, 23)
               .addBox(0.0F, -3.0F, -8.5F, 0.0F, 22.0F, 42.0F, new CubeDeformation(0.0F))
               .texOffs(92, 121)
               .addBox(-2.5F, -3.0F, 12.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.01F))
               .texOffs(76, 3)
               .addBox(-1.5F, -2.0F, -1.5F, 3.0F, 3.0F, 16.0F, new CubeDeformation(0.01F)),
            PartPose.offsetAndRotation(5.6775F, -4.2962F, 4.6319F, 0.8791F, 0.7694F, -0.2109F)
         );
         PartDefinition vine_r2 = left_wing.addOrReplaceChild(
            "vine_r2",
            CubeListBuilder.create()
               .texOffs(60, 88)
               .addBox(-1.0F, -2.0F, -5.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.2F))
               .texOffs(0, 105)
               .addBox(-1.0F, -2.0F, -5.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.4329F, -2.0F, 2.0474F, 0.3991F, 0.7694F, -0.2109F)
         );
         PartDefinition cube_r1 = left_wing.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(118, 88)
               .addBox(-1.0F, -0.5F, 6.5F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.01F))
               .texOffs(119, 113)
               .addBox(-1.5F, -1.0F, -1.5F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.01F)),
            PartPose.offsetAndRotation(10.2581F, -19.7797F, 12.6024F, 0.2682F, 0.7694F, -0.2109F)
         );
         PartDefinition right_wing = body.addOrReplaceChild(
            "right_wing", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.0F, 3.25F, 0.0F, -0.2182F, 0.0F)
         );
         PartDefinition vine_r3 = right_wing.addOrReplaceChild(
            "vine_r3",
            CubeListBuilder.create()
               .texOffs(117, 27)
               .mirror()
               .addBox(-2.5F, -3.0F, 12.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.2F))
               .mirror(false)
               .texOffs(43, 19)
               .mirror()
               .addBox(-1.5F, -2.0F, -1.5F, 3.0F, 3.0F, 16.0F, new CubeDeformation(0.21F))
               .mirror(false)
               .texOffs(0, 0)
               .addBox(0.0F, -3.0F, -8.5F, 0.0F, 22.0F, 42.0F, new CubeDeformation(0.0F))
               .texOffs(0, 120)
               .addBox(-2.5F, -3.0F, 12.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.01F))
               .texOffs(0, 65)
               .addBox(-1.5F, -2.0F, -1.5F, 3.0F, 3.0F, 16.0F, new CubeDeformation(0.01F)),
            PartPose.offsetAndRotation(-5.6775F, -4.2962F, 4.6319F, 0.8791F, -0.7694F, 0.2109F)
         );
         PartDefinition vine_r4 = right_wing.addOrReplaceChild(
            "vine_r4",
            CubeListBuilder.create()
               .texOffs(60, 88)
               .mirror()
               .addBox(-3.0F, -2.0F, -5.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.2F))
               .mirror(false)
               .texOffs(89, 88)
               .addBox(-3.0F, -2.0F, -5.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.4329F, -2.0F, 2.0474F, 0.3991F, -0.7694F, 0.2109F)
         );
         PartDefinition cube_r2 = right_wing.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(111, 15)
               .addBox(-1.0F, -0.5F, 6.5F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.01F))
               .texOffs(69, 118)
               .addBox(-1.5F, -1.0F, -1.5F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.01F)),
            PartPose.offsetAndRotation(-10.2581F, -19.7797F, 12.6024F, 0.2682F, -0.7694F, 0.2109F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(115, 38).addBox(-2.4F, 0.25F, -2.5F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.25F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(106, 103).addBox(-2.6F, 0.25F, -2.5F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.25F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 256, 256);
      }

      @Override
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
         ModelPartHelper.runPreservingTransforms(() -> {
            this.animateParts();
            super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         }, this.rightWing, this.leftWing);
      }

      private void animateParts() {
         this.leftWing.xRot = Mth.map((float)Math.sin(System.currentTimeMillis() / 1000.0), -1.0F, 1.0F, 0.0F, 0.17453294F);
         this.leftWing.yRot = Mth.map((float)Math.sin(System.currentTimeMillis() / 500.0), -1.0F, 1.0F, 0.0F, (float) (Math.PI / 6));
         this.rightWing.xRot = this.leftWing.xRot;
         this.rightWing.yRot = -this.leftWing.yRot;
      }
   }
}
