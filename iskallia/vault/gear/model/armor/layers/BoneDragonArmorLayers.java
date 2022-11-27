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

public class BoneDragonArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? BoneDragonArmorLayers.LeggingsLayer::createBodyLayer : BoneDragonArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? BoneDragonArmorLayers.LeggingsLayer::new : BoneDragonArmorLayers.MainLayer::new;
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
               .texOffs(0, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
               .texOffs(28, 13)
               .addBox(-2.0F, 8.0F, -3.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 0)
               .addBox(-2.0F, 8.0F, -4.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 16)
               .addBox(-1.0F, 11.0F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(24, 0).addBox(-0.5F, -3.5F, -3.0F, 1.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, 14.5F, 0.0F, 0.0F, 0.0F, -0.2182F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(26, 26).addBox(-0.5F, -3.5F, -3.0F, 1.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, 14.5F, 0.0F, 0.0F, 0.0F, 0.1745F)
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
               .texOffs(0, 37)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(70, 19)
               .addBox(-5.0F, -3.0F, -6.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(46, 0)
               .addBox(-5.0F, -9.0F, -6.0F, 10.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-5.0F, -10.0F, -5.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(36, 0)
               .addBox(-5.0F, -6.0F, -6.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 30)
               .addBox(4.0F, -6.0F, -6.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(63, 79)
               .addBox(5.0F, -8.0F, -5.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 57)
               .addBox(-1.0F, -6.0F, -6.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 6)
               .addBox(-2.0F, -1.0F, -6.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(22, 57)
               .addBox(1.0F, -1.0F, -6.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 58)
               .addBox(-4.0F, -1.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(58, 33)
               .addBox(3.0F, -1.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(32, 39)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(42, 86)
               .addBox(-1.0F, 1.0F, -4.0F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(46, 4)
               .addBox(-2.0F, 1.0F, 3.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(30, 0)
               .addBox(-1.0F, 6.0F, 3.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(52, 35)
               .addBox(-2.5F, -0.5F, -1.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(18, 53)
               .addBox(-2.5F, -0.5F, -8.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(62, 70)
               .addBox(0.5F, -0.5F, -8.5F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, 9.5F, 4.5F, 0.0F, 0.0F, 0.4363F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(52, 22)
               .addBox(-2.5F, -0.5F, -1.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 55)
               .addBox(-2.5F, -0.5F, -8.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(70, 10)
               .addBox(0.5F, -0.5F, -8.5F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, 6.5F, 4.5F, 0.0F, 0.0F, 0.3054F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(0, 18)
               .addBox(-3.25F, -1.5F, -1.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 24)
               .addBox(-3.25F, -1.5F, -8.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(62, 44)
               .addBox(0.75F, -1.5F, -8.5F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.25F, 3.5F, 4.5F, 0.0F, 0.0F, -0.1571F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(0, 7)
               .addBox(-0.75F, -1.5F, -1.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 14)
               .addBox(-0.75F, -1.5F, -8.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(70, 0)
               .addBox(-1.75F, -1.5F, -8.5F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.25F, 3.5F, 4.5F, 0.0F, 0.0F, 0.1309F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(24, 43)
               .addBox(-0.5F, -0.5F, -1.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(18, 55)
               .addBox(-0.5F, -0.5F, -8.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 71)
               .addBox(-1.5F, -0.5F, -8.5F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, 9.5F, 4.5F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(0, 31)
               .addBox(-0.5F, -0.5F, -1.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(38, 55)
               .addBox(-0.5F, -0.5F, -8.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(72, 23)
               .addBox(-1.5F, -0.5F, -8.5F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, 6.5F, 4.5F, 0.0F, 0.0F, -0.3054F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(70, 54).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r7 = right_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create()
               .texOffs(52, 31)
               .addBox(-0.6738F, -9.7517F, -3.476F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 53)
               .addBox(-0.6738F, -9.7517F, 2.524F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(58, 20)
               .addBox(-3.7191F, 6.1096F, -1.476F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(58, 31)
               .addBox(-3.7191F, 6.1096F, 0.524F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 11)
               .addBox(-2.7191F, 6.1096F, -2.976F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(34, 57)
               .addBox(-1.2191F, 6.1096F, -3.476F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(58, 18)
               .addBox(-1.2191F, 6.1096F, 2.524F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(18, 57)
               .addBox(-2.7191F, 6.1096F, 2.024F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 58)
               .addBox(6.2809F, 1.1096F, -4.976F, 2.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(42, 71)
               .addBox(-2.7191F, -0.8904F, -2.976F, 3.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(56, 31)
               .addBox(-3.7191F, 1.1096F, -3.976F, 4.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(46, 45)
               .addBox(0.2809F, 1.1096F, -4.976F, 3.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(0, 24)
               .addBox(0.2809F, -1.8904F, -4.976F, 8.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(26, 27)
               .addBox(0.2809F, 4.1096F, -4.976F, 8.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(0, 53)
               .addBox(3.2809F, 1.1096F, -3.976F, 5.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.5309F, -4.3596F, -0.024F, 0.0F, 0.0F, -0.3491F)
         );
         PartDefinition cube_r8 = right_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(26, 28)
               .addBox(6.1281F, -7.7251F, -3.576F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 29)
               .addBox(6.1281F, -7.7251F, 2.424F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.5309F, -4.3596F, -0.024F, 0.0F, 0.0F, -1.0908F)
         );
         PartDefinition cube_r9 = right_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(0, 27).addBox(1.8449F, -10.7943F, -0.576F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.5309F, -4.3596F, -0.024F, 0.0F, 0.0F, -1.789F)
         );
         PartDefinition cube_r10 = right_arm.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(52, 18).addBox(-5.9052F, -9.1209F, -0.476F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.5309F, -4.3596F, -0.024F, 0.0F, 0.0F, -1.0472F)
         );
         PartDefinition cube_r11 = right_arm.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create()
               .texOffs(20, 84)
               .addBox(-1.93F, -8.3301F, -0.976F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(80, 0)
               .addBox(-2.93F, -4.3301F, -1.576F, 5.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.5309F, -4.3596F, -0.024F, 0.0F, 0.0F, -1.6581F)
         );
         PartDefinition cube_r12 = right_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create()
               .texOffs(80, 10)
               .addBox(1.7172F, -1.8462F, 1.424F, 5.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(80, 32)
               .addBox(1.7172F, -1.8462F, -4.576F, 5.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(86, 52)
               .addBox(2.7172F, -5.8462F, -3.976F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(75, 86)
               .addBox(2.7172F, -5.8462F, 2.024F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.5309F, -4.3596F, -0.024F, 0.0F, 0.0F, -0.9599F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(16, 68).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r13 = left_arm.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create()
               .texOffs(52, 6)
               .addBox(-8.6735F, 1.1096F, -4.008F, 5.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(54, 18)
               .addBox(-0.6735F, 1.1096F, -4.008F, 4.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(16, 55)
               .addBox(-8.6735F, 1.1096F, -5.008F, 2.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(26, 14)
               .addBox(-8.6735F, 4.1096F, -5.008F, 8.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(30, 1)
               .addBox(-3.6735F, 1.1096F, -5.008F, 3.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(0, 11)
               .addBox(-8.6735F, -1.8904F, -5.008F, 8.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(72, 44)
               .addBox(-0.6735F, -0.8904F, -3.008F, 3.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(30, 57)
               .addBox(2.3265F, 6.1096F, -1.508F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 4)
               .addBox(2.3265F, 6.1096F, 0.492F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 53)
               .addBox(-0.1735F, 6.1096F, 2.492F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(52, 39)
               .addBox(1.5265F, 6.1096F, 1.992F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 11)
               .addBox(1.2265F, 6.1096F, -3.008F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 4)
               .addBox(-0.4735F, 6.1096F, -3.508F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(7.9235F, -4.1096F, 0.008F, 0.0F, 0.0F, 0.3491F)
         );
         PartDefinition cube_r14 = left_arm.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create()
               .texOffs(26, 26)
               .addBox(5.6135F, -8.1967F, 2.408F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 19)
               .addBox(5.6135F, -8.1967F, -3.592F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(7.9235F, -4.1096F, 0.008F, -3.1416F, 0.0F, -2.0508F)
         );
         PartDefinition cube_r15 = left_arm.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create()
               .texOffs(0, 41)
               .addBox(-1.3718F, -9.7517F, 2.508F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 39)
               .addBox(-1.3718F, -9.7517F, -3.492F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(7.9235F, -4.1096F, 0.008F, -3.1416F, 0.0F, -2.7925F)
         );
         PartDefinition cube_r16 = left_arm.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create()
               .texOffs(10, 84)
               .addBox(2.2196F, -6.1947F, 2.008F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(47, 79)
               .addBox(1.2196F, -2.1947F, 1.408F, 5.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 81)
               .addBox(2.2196F, -6.1947F, -3.992F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(77, 78)
               .addBox(1.2196F, -2.1947F, -4.592F, 5.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(7.9235F, -4.1096F, 0.008F, 3.1416F, 0.0F, -2.1817F)
         );
         PartDefinition cube_r17 = left_arm.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create().texOffs(26, 17).addBox(1.8423F, -10.8145F, -0.492F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(7.9235F, -4.1096F, 0.008F, 3.1416F, 0.0F, -1.3526F)
         );
         PartDefinition cube_r18 = left_arm.addOrReplaceChild(
            "cube_r18",
            CubeListBuilder.create().texOffs(24, 39).addBox(-5.9208F, -9.134F, -0.392F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(7.9235F, -4.1096F, 0.008F, -3.1416F, 0.0F, -2.0944F)
         );
         PartDefinition cube_r19 = left_arm.addOrReplaceChild(
            "cube_r19",
            CubeListBuilder.create()
               .texOffs(32, 80)
               .addBox(-1.9353F, -8.3498F, -0.892F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(72, 70)
               .addBox(-2.9353F, -4.3498F, -1.492F, 5.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(7.9235F, -4.1096F, 0.008F, -3.1416F, 0.0F, -1.4835F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(0, 65)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(26, 30)
               .addBox(-1.0F, 3.0F, -4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 11)
               .addBox(-2.0F, 6.0F, -4.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-1.8F, 6.0F, -4.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 37)
               .addBox(-0.8F, 3.0F, -4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(54, 58)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
