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

public class DevilguiseArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return DevilguiseArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return DevilguiseArmorLayers.MainLayer::new;
   }

   @OnlyIn(Dist.CLIENT)
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
               .texOffs(0, 22)
               .addBox(-2.75F, -2.0F, -9.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 8)
               .addBox(-1.25F, -2.5F, -9.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(0.25F, -2.5F, -9.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(9, 0)
               .addBox(1.75F, -2.0F, -9.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(45, 51)
               .addBox(1.25F, -0.25F, -6.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(34, 51)
               .addBox(-2.25F, -0.25F, -6.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(34, 47).addBox(-1.0F, -1.0F, -3.5F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 4.75F, -11.75F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(49, 40).addBox(-1.0F, -1.0F, -3.5F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 1.25F, -6.5F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(45, 49).addBox(-1.0F, -1.0F, -1.5F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -8.6839F, -12.7979F, 1.2654F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(31, 56).addBox(-1.0F, -3.5F, -1.5F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -6.75F, -10.5F, 0.9599F, 0.0F, 0.0F)
         );
         PartDefinition Helmet_r1 = head.addOrReplaceChild(
            "Helmet_r1",
            CubeListBuilder.create().texOffs(49, 0).addBox(-2.0F, -4.7556F, -4.7814F, 4.0F, 2.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offsetAndRotation(0.0F, -2.6913F, -3.0069F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition Helmet_r2 = head.addOrReplaceChild(
            "Helmet_r2",
            CubeListBuilder.create().texOffs(30, 66).addBox(-1.0F, 1.2444F, -4.7814F, 3.0F, 1.0F, 2.0F, new CubeDeformation(1.0F)),
            PartPose.offsetAndRotation(-0.5F, -2.6913F, -3.0069F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition Helmet_r3 = head.addOrReplaceChild(
            "Helmet_r3",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.3087F, -1.9931F, 8.0F, 8.0F, 14.0F, new CubeDeformation(1.0F)),
            PartPose.offsetAndRotation(0.0F, -2.6913F, -3.0069F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 22)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(53, 58)
               .addBox(4.0F, 4.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(56, 6)
               .addBox(-6.0F, 4.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(60, 18)
               .addBox(-6.0F, 4.0F, 2.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(60, 40)
               .addBox(1.0F, 4.0F, 2.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(18, 64)
               .addBox(-6.0F, 4.0F, -4.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(64, 22)
               .addBox(1.0F, 4.0F, -4.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 54)
               .addBox(-1.5F, -2.0F, 3.0F, 3.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(52, 28)
               .addBox(-0.5F, -1.0F, 7.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(45, 47)
               .addBox(-0.5F, -0.5F, 5.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(30, 0)
               .addBox(-0.5F, 4.5F, 5.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(8, 8)
               .addBox(-0.5F, 9.5F, 5.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(49, 0)
               .addBox(-0.5F, 9.0F, 7.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 47)
               .addBox(-0.5F, 4.0F, 7.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 38)
               .addBox(-0.5F, 6.5F, 5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 31)
               .addBox(-0.5F, 1.5F, 5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 47)
               .addBox(-2.0F, 15.5F, 15.0F, 4.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-1.0F, 16.0F, 20.0F, 2.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(20, 57)
               .addBox(-1.0F, 13.0F, 25.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(16, 38)
               .addBox(-1.0F, 10.0F, 23.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(10, 57).addBox(-1.5F, -1.5F, -1.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 18.0178F, 11.0178F, 1.5708F, 0.0F, 0.0F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(56, 25).addBox(-1.0F, -2.0F, -1.0F, 3.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, 13.5F, 5.25F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create()
               .texOffs(64, 4)
               .addBox(-3.25F, -1.0F, -4.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(56, 36)
               .addBox(-3.25F, -1.0F, 2.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(41, 58)
               .addBox(-0.25F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.25F, 9.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(63, 62)
               .addBox(-1.75F, -1.0F, -4.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(30, 9)
               .addBox(-1.75F, -1.0F, 2.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 8)
               .addBox(-1.75F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.25F, 9.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create()
               .texOffs(39, 64)
               .addBox(-1.75F, -1.0F, -4.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(61, 0)
               .addBox(-1.75F, -1.0F, 2.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(56, 49)
               .addBox(-1.75F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.25F, 1.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r10 = body.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create()
               .texOffs(61, 56)
               .addBox(-3.25F, -1.0F, 2.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(60, 12)
               .addBox(-0.25F, -1.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(64, 49)
               .addBox(-3.25F, -1.0F, -4.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.25F, 1.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(44, 9)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(30, 0)
               .addBox(-6.0F, -4.0F, -3.5F, 6.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(30, 4)
               .addBox(-8.0F, -4.0F, 2.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(62, 70)
               .addBox(-9.0F, -4.0F, -3.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 68)
               .addBox(-10.0F, -4.0F, -1.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 71)
               .addBox(-5.0F, -5.0F, -1.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 71)
               .addBox(-5.0F, -5.0F, 0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 71)
               .addBox(-5.0F, -5.0F, 2.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(70, 70)
               .addBox(-5.0F, -5.0F, -3.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(70, 36)
               .addBox(-9.0F, -4.0F, 0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r11 = right_arm.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create()
               .texOffs(66, 34)
               .addBox(-2.5F, -0.5F, -1.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 9)
               .addBox(-0.5F, -0.5F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, -2.5F, -4.5F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r12 = right_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create()
               .texOffs(66, 30)
               .addBox(-0.25F, -1.75F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(68, 53)
               .addBox(-3.25F, -0.75F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-11.75F, -1.25F, 1.0F, 0.0F, 0.0F, -1.1781F)
         );
         PartDefinition cube_r13 = right_arm.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create()
               .texOffs(63, 66)
               .addBox(-0.25F, -1.75F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(55, 68)
               .addBox(-3.25F, -0.75F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-13.0F, -1.5F, -1.0F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition cube_r14 = right_arm.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create()
               .texOffs(47, 68)
               .addBox(-3.25F, -0.75F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(66, 26)
               .addBox(-0.25F, -1.75F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-11.25F, -2.0F, 3.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r15 = right_arm.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create()
               .texOffs(28, 69)
               .addBox(-3.25F, -0.75F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(67, 44)
               .addBox(-0.25F, -1.75F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-12.25F, -2.0F, -3.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(68, 8)
               .addBox(6.0F, -4.0F, -1.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(68, 11)
               .addBox(6.0F, -4.0F, -3.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 22)
               .addBox(0.0F, -4.0F, -3.5F, 6.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(16, 45)
               .addBox(2.0F, -5.0F, -3.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 54)
               .addBox(2.0F, -5.0F, -1.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 57)
               .addBox(2.0F, -5.0F, 0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(68, 14)
               .addBox(2.0F, -5.0F, 2.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 68)
               .addBox(6.0F, -4.0F, 0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 26)
               .addBox(6.0F, -4.0F, 2.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 31)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r16 = left_arm.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create()
               .texOffs(39, 68)
               .addBox(0.25F, -0.75F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(29, 47)
               .addBox(-3.75F, -1.75F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(12.25F, -2.25F, 1.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r17 = left_arm.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create()
               .texOffs(53, 64)
               .addBox(-3.75F, -1.75F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(49, 58)
               .addBox(0.25F, -0.75F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(12.5F, -1.25F, -1.0F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition cube_r18 = left_arm.addOrReplaceChild(
            "cube_r18",
            CubeListBuilder.create()
               .texOffs(43, 25)
               .addBox(-3.75F, -1.75F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 31)
               .addBox(0.25F, -0.75F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.0F, -0.75F, 3.0F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition cube_r19 = left_arm.addOrReplaceChild(
            "cube_r19",
            CubeListBuilder.create()
               .texOffs(12, 38)
               .addBox(-1.5F, -0.5F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(61, 60)
               .addBox(-1.5F, -0.5F, -1.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.5F, -2.5F, -4.5F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r20 = left_arm.addOrReplaceChild(
            "cube_r20",
            CubeListBuilder.create()
               .texOffs(10, 65)
               .addBox(0.25F, -0.75F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 22)
               .addBox(-3.75F, -1.75F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(12.25F, -2.0F, -3.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(24, 31).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
