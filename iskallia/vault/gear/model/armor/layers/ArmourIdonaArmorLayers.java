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

public class ArmourIdonaArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ArmourIdonaArmorLayers.LeggingsLayer::createBodyLayer : ArmourIdonaArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ArmourIdonaArmorLayers.LeggingsLayer::new : ArmourIdonaArmorLayers.MainLayer::new;
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
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
               .texOffs(24, 0)
               .addBox(-3.0F, 8.0F, -4.0F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 6)
               .addBox(-1.5F, 12.0F, -4.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
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
               .texOffs(40, 24)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(40, 8)
               .addBox(-4.0F, -18.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(40, 40)
               .addBox(-1.0F, -14.0F, -7.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 60)
               .addBox(-1.0F, -12.0F, 5.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(59, 40)
               .addBox(-7.0F, -12.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(90, 22)
               .addBox(-6.0F, -11.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(8, 89)
               .addBox(4.0F, -11.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(88, 85)
               .addBox(4.0F, -11.0F, 4.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 89)
               .addBox(-6.0F, -11.0F, 4.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(50, 55)
               .addBox(5.0F, -12.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(59, 55)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(72, 29)
               .addBox(-2.0F, 6.0F, 3.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(68, 36)
               .addBox(-4.0F, 1.0F, 3.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(82, 13).addBox(-3.5F, -2.0F, -1.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.5F, 1.0F, -4.5F, 0.5299F, -0.7119F, -0.3655F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(83, 56).addBox(-2.5F, -2.0F, -1.0F, 6.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, 1.0F, -4.5F, 0.5299F, 0.7119F, 0.3655F)
         );
         PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(3.6716F, 4.7721F, 6.0F));
         PartDefinition cube_r3 = left_wing.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(79, 46)
               .addBox(2.75F, -6.25F, 4.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 30)
               .addBox(-15.5F, -10.25F, 7.0F, 20.0F, 30.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(34, 70)
               .addBox(4.5F, -4.25F, 6.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(13.0F, -9.0F, 0.0F, 0.5201F, -0.1812F, -1.3055F)
         );
         PartDefinition cube_r4 = left_wing.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(40, 4).addBox(-15.5F, -6.5F, 6.0F, 20.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(13.0F, -9.0F, 0.0F, 0.4215F, -0.3614F, -0.9426F)
         );
         PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(-3.6716F, 4.7721F, 6.0F));
         PartDefinition cube_r5 = right_wing.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(40, 0).addBox(-15.0F, -16.75F, 7.0F, 20.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-15.8284F, 4.9779F, 0.0F, 0.4215F, 0.3614F, 0.9426F)
         );
         PartDefinition cube_r6 = right_wing.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(79, 3)
               .addBox(-4.75F, -14.0F, -3.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-1.75F, -17.0F, -0.25F, 20.0F, 30.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(26, 70)
               .addBox(-3.75F, -11.0F, -1.0F, 2.0F, 24.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-17.3562F, -10.0698F, 10.6675F, 0.5201F, 0.1812F, 1.3055F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(74, 71).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r7 = right_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create()
               .texOffs(68, 87)
               .addBox(-1.9268F, -0.6768F, -3.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(78, 87)
               .addBox(-1.9268F, -0.6768F, 1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-9.9704F, -4.0772F, 0.0F, -3.1416F, 0.0F, 2.3562F)
         );
         PartDefinition cube_r8 = right_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(90, 78)
               .addBox(-1.4874F, -4.091F, -2.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(22, 82)
               .addBox(-1.4874F, -7.091F, 1.5F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-9.9704F, -4.0772F, 0.0F, -3.1416F, 0.0F, 1.5708F)
         );
         PartDefinition cube_r9 = right_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create()
               .texOffs(64, 16)
               .addBox(-4.5455F, -0.8473F, -4.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(31, 55)
               .addBox(0.4545F, -1.8473F, -4.5F, 5.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.398F, -1.5908F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r10 = right_arm.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(16, 84).addBox(-1.7374F, -8.591F, -0.5F, 2.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.7204F, -7.5772F, 0.0F, -3.1416F, 0.0F, 1.9635F)
         );
         PartDefinition cube_r11 = right_arm.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(84, 29).addBox(-2.4571F, -0.5F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.7204F, -7.5772F, 0.0F, -3.1416F, 0.0F, 2.7489F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(0, 73).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r12 = left_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(90, 72).addBox(-1.4874F, -3.341F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.2796F, -4.0772F, 2.0F, 0.0F, 0.0F, 1.5708F)
         );
         PartDefinition cube_r13 = left_arm.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create().texOffs(42, 86).addBox(-2.4571F, -0.1464F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.2796F, -4.0772F, 2.0F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition cube_r14 = left_arm.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(86, 65).addBox(-2.4571F, -0.1464F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.2796F, -4.0772F, -2.0F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition cube_r15 = left_arm.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create().texOffs(22, 73).addBox(-1.4874F, -6.341F, -0.5F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.2796F, -4.0772F, -2.0F, 0.0F, 0.0F, 1.5708F)
         );
         PartDefinition cube_r16 = left_arm.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create()
               .texOffs(40, 40)
               .addBox(-3.5852F, -4.3003F, -4.5F, 5.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
               .texOffs(0, 60)
               .addBox(-8.5852F, -3.3003F, -4.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.4413F, -0.7187F, 0.0F, -3.1416F, 0.0F, -2.7489F)
         );
         PartDefinition cube_r17 = left_arm.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create().texOffs(16, 73).addBox(-1.7374F, -8.591F, -0.5F, 2.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.7796F, -7.5772F, 0.0F, 0.0F, 0.0F, 1.1781F)
         );
         PartDefinition cube_r18 = left_arm.addOrReplaceChild(
            "cube_r18",
            CubeListBuilder.create().texOffs(68, 46).addBox(-2.4571F, -0.5F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.7796F, -7.5772F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(58, 71)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(40, 24)
               .addBox(-1.0F, 8.0F, 3.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r19 = right_leg.addOrReplaceChild(
            "cube_r19",
            CubeListBuilder.create().texOffs(64, 8).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.0F, -3.0F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(42, 70)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(40, 8)
               .addBox(-0.8F, 8.0F, 3.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r20 = left_leg.addOrReplaceChild(
            "cube_r20",
            CubeListBuilder.create().texOffs(18, 60).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.2F, 10.0F, -3.0F, -0.3927F, 0.0F, 0.0F)
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
            this.leftWing.xRot = Mth.map((float)Math.sin(System.currentTimeMillis() / 1000.0), -1.0F, 1.0F, 0.0F, 0.17453294F);
            this.leftWing.yRot = Mth.map((float)Math.sin(System.currentTimeMillis() / 500.0), -1.0F, 1.0F, 0.0F, (float) (Math.PI / 6));
            this.rightWing.xRot = this.leftWing.xRot;
            this.rightWing.yRot = -this.leftWing.yRot;
            super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         }, this.rightWing, this.leftWing);
      }
   }
}
