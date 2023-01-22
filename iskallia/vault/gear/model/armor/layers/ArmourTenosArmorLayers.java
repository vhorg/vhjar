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

public class ArmourTenosArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ArmourTenosArmorLayers.LeggingsLayer::createBodyLayer : ArmourTenosArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ArmourTenosArmorLayers.LeggingsLayer::new : ArmourTenosArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(28, 32).addBox(-6.0F, -5.75F, 2.5F, 8.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, 17.0F, 2.5F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(0, 16)
               .addBox(-0.75F, -4.25F, -0.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 34)
               .addBox(-0.75F, 3.75F, -3.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(40, 0)
               .addBox(-0.75F, 1.75F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(8, 16)
               .addBox(-0.75F, -0.25F, -1.75F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(40, 14)
               .addBox(-0.75F, -2.25F, -1.25F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.7571F, 16.114F, 3.2369F, 0.3104F, 0.7531F, 0.3496F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(14, 16)
               .addBox(-2.5F, -5.0F, -0.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 42)
               .addBox(-2.5F, -3.0F, -1.25F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(8, 34)
               .addBox(-2.5F, -1.0F, -1.75F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(40, 7)
               .addBox(-2.5F, 1.0F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(14, 34)
               .addBox(-2.5F, 3.0F, -3.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.3478F, 16.2346F, 5.0F, 0.3054F, -0.7854F, -0.3927F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 16).addBox(1.5F, -6.0F, -3.0F, 1.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, 17.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(14, 16).addBox(-2.5F, -6.0F, -3.0F, 1.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, 17.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(28, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MainLayer extends ArmorLayers.MainLayer {
      protected ModelPart leftWing;
      protected ModelPart rightWing;
      protected ModelPart leftOrb;
      protected ModelPart rightOrb;

      public MainLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
         ModelPart body = root.getChild("body");
         this.leftWing = body.getChild("left_wing");
         this.rightWing = body.getChild("right_wing");
         this.leftOrb = body.getChild("left_orb");
         this.rightOrb = body.getChild("right_orb");
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create().texOffs(56, 6).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -7.3012F, 3.1326F, 0.0F, 15.0F, 13.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -11.1974F, -6.5967F, 0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(56, 28)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(56, 56)
               .addBox(-5.0F, -1.0F, -6.0F, 10.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(14, 0).addBox(-4.0F, -5.0F, 0.25F, 8.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 8.0F, -4.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(2.8934F, 3.533F, 5.6858F));
         PartDefinition cube_r3 = left_wing.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(96, 20)
               .addBox(4.5554F, 10.8696F, -6.3332F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 29)
               .addBox(5.5554F, -6.1304F, -8.3332F, 0.0F, 29.0F, 28.0F, new CubeDeformation(0.0F))
               .texOffs(32, 86)
               .addBox(4.5554F, -5.1304F, 5.6668F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(56, 44)
               .addBox(4.5554F, -7.1304F, 3.6668F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(12, 94)
               .addBox(4.5554F, -7.1304F, -9.3332F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(88, 10)
               .addBox(4.5554F, 8.8696F, -9.3332F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.6092F, -7.6003F, 11.7533F, -0.5923F, 0.9873F, 0.0672F)
         );
         PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(-2.8934F, 3.533F, 5.6858F));
         PartDefinition cube_r4 = right_wing.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(0, 96)
               .addBox(-0.9514F, 9.2626F, -5.5225F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(48, 87)
               .addBox(-0.9514F, 7.2626F, -8.5225F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(92, 34)
               .addBox(-0.9514F, -8.7374F, -8.5225F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-0.9514F, -8.7374F, 4.4775F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(16, 86)
               .addBox(-0.9514F, -6.7374F, 6.4775F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(0.0486F, -7.7374F, -7.5225F, 0.0F, 29.0F, 28.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-10.2132F, -8.0F, 8.0F, -0.5715F, -0.8585F, 0.0315F)
         );
         PartDefinition left_orb = body.addOrReplaceChild(
            "left_orb",
            CubeListBuilder.create().texOffs(80, 0).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(10.5F, -8.5F, 0.0F)
         );
         PartDefinition right_orb = body.addOrReplaceChild(
            "right_orb",
            CubeListBuilder.create().texOffs(56, 80).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-9.5F, -8.5F, -0.25F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(80, 38)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(26, 14)
               .addBox(-9.0F, -4.0F, -5.0F, 9.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(64, 22)
               .addBox(-6.0F, -4.0F, -6.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(68, 93)
               .addBox(-6.0F, -6.0F, -8.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(80, 93)
               .addBox(-12.0F, -8.0F, 3.0F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(58, 90)
               .addBox(-12.0F, -8.0F, 0.25F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(70, 44)
               .addBox(-12.0F, -8.0F, -2.5F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(90, 84)
               .addBox(-12.0F, -8.0F, -5.0F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r5 = right_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(84, 54).addBox(0.25F, -6.5F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, 4.5F, 0.0F, 0.0F, 0.0F, -0.3491F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(26, 0)
               .addBox(0.0F, -4.0F, -5.0F, 9.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(64, 0)
               .addBox(2.0F, -4.0F, -6.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(88, 76)
               .addBox(2.0F, -6.0F, -8.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(9.0F, -8.0F, 3.0F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(26, 14)
               .addBox(9.0F, -8.0F, 0.25F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(54, 0)
               .addBox(9.0F, -8.0F, -2.5F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(56, 44)
               .addBox(9.0F, -8.0F, -5.0F, 3.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(80, 22)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r6 = left_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(76, 80).addBox(-3.0F, -5.5F, -2.0F, 3.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, 4.5F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(72, 64).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r7 = right_leg.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(34, 94).addBox(-2.0F, -4.0F, -0.5F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 9.0F, -3.5F, 0.1745F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(56, 64).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r8 = left_leg.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(24, 94).addBox(2.0F, -4.0F, -0.5F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.8F, 9.0F, -3.5F, 0.1745F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
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
         }, this.rightWing, this.leftWing, this.leftOrb, this.rightOrb);
      }

      private void animateParts() {
         this.leftOrb.y = this.leftOrb.y + (float)Math.sin(System.currentTimeMillis() / 500.0) * (float) Math.PI / 5.0F;
         this.leftOrb.yRot = (float)(10.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (Math.PI / 180.0));
         this.leftOrb.xRot = (float)(20.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (Math.PI / 180.0));
         this.leftOrb.zRot = (float)(30.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (Math.PI / 180.0));
         this.rightOrb.y = this.rightOrb.y + (float)Math.sin(System.currentTimeMillis() / 500.0 + 500.0) * (float) Math.PI / 5.0F;
         this.rightOrb.yRot = (float)(-10.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (Math.PI / 180.0));
         this.rightOrb.xRot = (float)(-20.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (Math.PI / 180.0));
         this.rightOrb.zRot = (float)(-30.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (Math.PI / 180.0));
         this.leftWing.xRot = Mth.map((float)Math.sin(System.currentTimeMillis() / 1000.0), -1.0F, 1.0F, 0.0F, 0.17453294F);
         this.leftWing.yRot = Mth.map((float)Math.sin(System.currentTimeMillis() / 500.0), -1.0F, 1.0F, 0.0F, (float) (Math.PI / 6));
         this.rightWing.xRot = this.leftWing.xRot;
         this.rightWing.yRot = -this.leftWing.yRot;
      }
   }
}
