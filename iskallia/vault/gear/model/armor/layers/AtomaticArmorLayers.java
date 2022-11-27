package iskallia.vault.gear.model.armor.layers;

import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import java.util.function.Supplier;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AtomaticArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? AtomaticArmorLayers.LeggingsLayer::createBodyLayer : AtomaticArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? AtomaticArmorLayers.LeggingsLayer::new : AtomaticArmorLayers.MainLayer::new;
   }

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
               .texOffs(0, 9)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
               .texOffs(34, 35)
               .addBox(-2.0F, 9.0F, -4.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(34, 17)
               .addBox(-2.0F, 9.0F, 2.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-5.0F, 10.0F, -3.0F, 10.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(36, 24)
               .addBox(-1.5F, -2.5F, -0.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 37)
               .addBox(-1.5F, -2.5F, -6.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, 11.5F, 3.0F, 0.0F, 0.0F, 0.829F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(26, 0)
               .addBox(-1.5F, -2.5F, -0.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 37)
               .addBox(-1.5F, -2.5F, -6.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.5F, 11.5F, 3.0F, 0.0F, 0.0F, -0.829F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(24, 9).addBox(-0.5F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, 11.5F, 0.0F, 0.0F, 0.0F, 0.2618F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(31, 4).addBox(-0.5F, -2.5F, -2.5F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, 11.5F, 0.0F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(20, 21).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }

   public static class MainLayer extends ArmorLayers.MainLayer {
      public MainLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
               .texOffs(28, 22)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(47, 87)
               .addBox(-4.0F, -7.0F, -6.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(66, 69)
               .addBox(-6.0F, -4.0F, -4.0F, 1.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(76, 14)
               .addBox(-4.0F, -8.0F, 5.0F, 8.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(78, 77)
               .addBox(-6.0F, -8.0F, -2.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(77, 0)
               .addBox(5.0F, -8.0F, -2.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(16, 67)
               .addBox(5.0F, -4.0F, -4.0F, 1.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(86, 28)
               .addBox(3.0F, -7.0F, -6.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 5)
               .addBox(-3.0F, -7.0F, -7.0F, 6.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(40, 38)
               .addBox(-2.0F, -8.0F, -6.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(74, 31)
               .addBox(-1.5F, -12.0F, -4.5F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(85, 0)
               .addBox(-1.5F, -11.0F, 1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(40, 8)
               .addBox(-1.0F, -10.0F, -6.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(0, 47)
               .addBox(2.0F, -9.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(41, 83)
               .addBox(2.0F, -6.5F, -0.5F, 2.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, -13.0F, -5.0F, 0.0F, 0.0F, 0.6545F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(37, 51)
               .addBox(-3.0F, -9.75F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 84)
               .addBox(-4.0F, -6.75F, -0.5F, 2.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, -13.0F, -5.0F, 0.0F, 0.0F, -0.6545F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(50, 61).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -4.5F, -5.5F, -0.3054F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(54, 44).addBox(-0.4697F, -2.0F, -1.1339F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.1564F, -1.0F, -5.5F, 0.1886F, -0.3864F, -0.0718F)
         );
         PartDefinition cube_r5 = head.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(72, 0).addBox(-3.5303F, -2.0F, -1.1339F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.1564F, -1.0F, -5.5F, 0.1886F, 0.3864F, 0.0718F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 47)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-6.0F, 1.0F, -4.0F, 12.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(76, 69)
               .addBox(-4.0F, 1.0F, 4.0F, 8.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(66, 82)
               .addBox(-2.0F, 1.0F, 5.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(29, 16)
               .addBox(-12.1667F, -12.25F, 3.25F, 19.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(22, 84)
               .addBox(-0.9167F, -8.25F, 3.25F, 3.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 63)
               .addBox(-4.6667F, -8.25F, 3.25F, 3.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 79)
               .addBox(-8.4167F, -8.25F, 3.25F, 3.0F, 14.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(50, 70)
               .addBox(-12.1667F, -8.25F, 3.25F, 3.0F, 17.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 40)
               .addBox(-7.1667F, -15.25F, 3.25F, 9.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-13.0833F, 8.0F, 5.5F, 0.1756F, 0.218F, 0.6642F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create()
               .texOffs(78, 87)
               .addBox(-6.5417F, -3.0F, 2.25F, 3.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(33, 83)
               .addBox(-2.5417F, -3.0F, 2.25F, 3.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 79)
               .addBox(1.2083F, -3.0F, 2.25F, 3.0F, 14.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(58, 77)
               .addBox(4.9583F, -3.0F, 2.25F, 3.0F, 17.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 43)
               .addBox(-6.0417F, -10.0F, 2.25F, 9.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 0)
               .addBox(-11.0417F, -7.0F, 2.25F, 19.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(12.8367F, 0.2338F, 5.5F, 0.0F, -0.2182F, -0.6545F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.0F, 6.0F, -0.6981F, 0.0F, 0.0F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(74, 62).addBox(-3.0F, -3.5F, -1.5F, 6.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 5.9997F, -5.7916F, -1.5708F, 0.0F, 0.0F)
         );
         PartDefinition cube_r10 = body.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(62, 5).addBox(-2.0F, -1.0F, -2.75F, 4.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 7.9997F, -3.2916F, -1.1781F, 0.0F, 0.0F)
         );
         PartDefinition cube_r11 = body.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(0, 16).addBox(-5.5F, -3.0F, -5.5F, 11.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 5.0F, -0.5F, -1.0036F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(65, 40).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r12 = right_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create()
               .texOffs(24, 52)
               .addBox(-0.625F, -3.5F, -3.5F, 3.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(83, 48)
               .addBox(-2.625F, -1.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(20, 30)
               .addBox(-3.625F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(74, 49)
               .addBox(-1.625F, -2.5F, -3.5F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.875F, 5.25F, 0.0F, 0.0F, 0.0F, -0.2182F)
         );
         PartDefinition cube_r13 = right_arm.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create()
               .texOffs(36, 59)
               .addBox(-11.0F, -2.5F, -4.0F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(80, 24)
               .addBox(-9.0F, -4.5F, -3.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(81, 44)
               .addBox(-9.0F, -4.5F, 1.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(52, 50)
               .addBox(-6.0F, -0.5F, -4.5F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
               .texOffs(20, 38)
               .addBox(-8.0F, -2.5F, -4.0F, 6.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.48F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(64, 21).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r14 = left_arm.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create()
               .texOffs(16, 80)
               .addBox(-2.15F, -3.25F, 1.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(77, 40)
               .addBox(-2.15F, -3.25F, -3.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 30)
               .addBox(-1.15F, -1.25F, -4.0F, 6.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(51, 29)
               .addBox(0.85F, 0.75F, -4.5F, 2.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
               .texOffs(40, 5)
               .addBox(-4.15F, -1.25F, -4.0F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.15F, -4.0F, 0.0F, -3.1416F, 0.0F, 2.6616F)
         );
         PartDefinition cube_r15 = left_arm.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create()
               .texOffs(26, 67)
               .addBox(-2.375F, -1.5F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 30)
               .addBox(-3.375F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(41, 44)
               .addBox(-0.375F, -3.5F, -3.5F, 3.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(34, 70)
               .addBox(-1.375F, -2.5F, -3.5F, 1.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.875F, 5.25F, 0.0F, -3.1416F, 0.0F, -2.9671F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 63).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r16 = right_leg.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create().texOffs(24, 51).addBox(-1.0F, -4.0F, -0.5F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 8.0F, -3.25F, -0.2182F, 0.0F, 0.0F)
         );
         PartDefinition cube_r17 = right_leg.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create().texOffs(54, 5).addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.5F, -3.5F, 0.3054F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(58, 61).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r18 = left_leg.addOrReplaceChild(
            "cube_r18",
            CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -4.0F, -0.5F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.2F, 8.0F, -3.25F, -0.2182F, 0.0F, 0.0F)
         );
         PartDefinition cube_r19 = left_leg.addOrReplaceChild(
            "cube_r19",
            CubeListBuilder.create().texOffs(52, 21).addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.2F, 10.5F, -3.5F, 0.3054F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
