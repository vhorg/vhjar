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

public class SpikedWarriorArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS
         ? SpikedWarriorArmorLayers.LeggingsLayer::createBodyLayer
         : SpikedWarriorArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SpikedWarriorArmorLayers.LeggingsLayer::new : SpikedWarriorArmorLayers.MainLayer::new;
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
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r1 = right_leg.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(24, 12)
               .addBox(-0.5F, -3.2881F, -1.0697F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 6)
               .addBox(-0.5F, -1.2881F, -1.0697F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.25F, 5.1041F, -4.402F, 0.3927F, -0.3927F, 3.1416F)
         );
         PartDefinition cube_r2 = right_leg.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(14, 32)
               .addBox(-0.5F, -1.2881F, -1.0697F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(32, 20)
               .addBox(-0.5F, -3.2881F, -1.0697F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.25F, 5.1041F, 4.348F, -2.7489F, -0.3927F, 0.0F)
         );
         PartDefinition cube_r3 = right_leg.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -0.1041F, 0.402F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.25F, 5.1041F, 4.348F, -3.1416F, -0.3927F, 0.0F)
         );
         PartDefinition cube_r4 = right_leg.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -0.1041F, 0.402F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.25F, 5.1041F, -4.402F, 0.0F, -0.3927F, 3.1416F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r5 = left_leg.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(32, 13)
               .addBox(-0.5F, -1.2881F, -1.0697F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(28, 12)
               .addBox(-0.5F, -3.2881F, -1.0697F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.2F, 5.1041F, 4.348F, -2.7489F, 0.3927F, 0.0F)
         );
         PartDefinition cube_r6 = left_leg.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -0.1041F, 0.402F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.2F, 5.1041F, 4.348F, 3.1416F, 0.3927F, 0.0F)
         );
         PartDefinition cube_r7 = left_leg.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create()
               .texOffs(8, 32)
               .addBox(-0.5F, -1.2881F, -1.0697F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(20, 32)
               .addBox(-0.5F, -3.2881F, -1.0697F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.2F, 5.1041F, -4.402F, 0.3927F, 0.3927F, 3.1416F)
         );
         PartDefinition cube_r8 = left_leg.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(24, 6).addBox(-1.0F, -0.1041F, 0.402F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.2F, 5.1041F, -4.402F, 0.0F, 0.3927F, -3.1416F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
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
               .texOffs(0, 30)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 17)
               .addBox(-5.5F, -7.0F, -5.5F, 11.0F, 2.0F, 11.0F, new CubeDeformation(0.0F))
               .texOffs(44, 24)
               .addBox(-1.0F, -8.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 46)
               .addBox(-5.5F, -16.7907F, -7.8024F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(16, 62)
               .addBox(3.5F, -16.7907F, -7.8024F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(52, 82).addBox(-1.5F, -0.5087F, -1.9257F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.9889F, -10.7881F, -0.6361F, 0.3927F, 0.7854F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(70, 18).addBox(-1.0F, -4.0026F, -1.3687F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.9889F, -10.7881F, -0.6361F, 0.0F, 0.7854F, 0.0F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(52, 24).addBox(-1.0F, -3.7907F, -0.6774F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.5F, -11.0F, -1.125F, 0.0F, -0.7854F, 0.0F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(82, 34).addBox(-1.5F, 0.8755F, -1.7508F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.5F, -12.0F, -1.125F, 0.3927F, -0.7854F, 0.0F)
         );
         PartDefinition cube_r5 = head.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(40, 74)
               .addBox(-1.5F, -0.75F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(28, 74)
               .addBox(-10.5F, -0.75F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, -12.25F, -6.5F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(74, 0)
               .addBox(-6.0F, -3.9692F, -13.0093F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(82, 0)
               .addBox(4.0F, -3.9692F, -13.0093F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 46)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-6.0F, 0.0F, -6.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(72, 57).addBox(-3.0F, -3.0F, -1.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 7.3863F, 3.0782F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(48, 7).addBox(-3.0F, -3.25F, 0.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 8.0F, -4.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(76, 76)
               .addBox(3.5F, -4.7907F, -2.6774F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 78)
               .addBox(-6.5F, -4.7907F, -2.6774F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 2.0F, -8.125F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create()
               .texOffs(66, 24)
               .addBox(3.0F, -1.1245F, -2.7508F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(66, 35)
               .addBox(-7.0F, -1.1245F, -2.7508F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 2.0F, -8.125F, 1.1781F, 0.0F, 0.0F)
         );
         PartDefinition cube_r10 = body.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(64, 84).addBox(-1.0F, -4.0026F, -1.3687F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0111F, -0.7881F, 7.3639F, -2.6117F, -0.7119F, -3.1144F)
         );
         PartDefinition cube_r11 = body.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(28, 84).addBox(-1.5F, -0.5087F, -1.9257F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0111F, -0.7881F, 7.3639F, -2.219F, -0.7119F, -3.1144F)
         );
         PartDefinition cube_r12 = body.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(84, 65).addBox(-1.0F, -4.0026F, -1.3687F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.9889F, -0.7881F, 7.3639F, -2.6117F, 0.7119F, 3.1144F)
         );
         PartDefinition cube_r13 = body.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create().texOffs(82, 42).addBox(-1.5F, -0.5087F, -1.9257F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.9889F, -0.7881F, 7.3639F, -2.219F, 0.7119F, 3.1144F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(24, 58)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(72, 65)
               .addBox(-5.0F, 5.0F, -2.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r14 = right_arm.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(40, 84).addBox(-1.0F, -4.786F, -3.8958F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-10.0F, -5.1832F, -0.1134F, 0.0F, 1.5708F, 0.0F)
         );
         PartDefinition cube_r15 = right_arm.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create().texOffs(82, 26).addBox(-1.5F, -3.2551F, -2.8151F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-10.0F, -5.1832F, -0.1134F, 0.0F, 1.5708F, -0.7854F)
         );
         PartDefinition cube_r16 = right_arm.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create().texOffs(70, 7).addBox(-2.0F, 0.2415F, -3.4657F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-10.0F, -5.1832F, -0.1134F, 0.0F, 1.5708F, -1.1781F)
         );
         PartDefinition cube_r17 = right_arm.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create()
               .texOffs(44, 17)
               .addBox(-3.5F, -4.0F, -3.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(32, 30)
               .addBox(-4.5F, -3.0F, -4.0F, 9.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, -1.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(40, 58)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(16, 74)
               .addBox(3.0F, 5.0F, -2.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r18 = left_arm.addOrReplaceChild(
            "cube_r18",
            CubeListBuilder.create()
               .texOffs(48, 0)
               .addBox(-3.5F, -4.0F, -3.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(32, 44)
               .addBox(-4.5F, -3.0F, -4.0F, 9.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.5F, -1.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r19 = left_arm.addOrReplaceChild(
            "cube_r19",
            CubeListBuilder.create().texOffs(82, 50).addBox(-1.0F, -4.786F, -3.8958F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.0F, -5.1832F, -0.1134F, 0.0F, -1.5708F, 0.0F)
         );
         PartDefinition cube_r20 = left_arm.addOrReplaceChild(
            "cube_r20",
            CubeListBuilder.create().texOffs(82, 18).addBox(-1.5F, -3.2551F, -2.8151F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.0F, -5.1832F, -0.1134F, 0.0F, -1.5708F, 0.7854F)
         );
         PartDefinition cube_r21 = left_arm.addOrReplaceChild(
            "cube_r21",
            CubeListBuilder.create().texOffs(66, 46).addBox(-2.0F, 0.2415F, -3.4657F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.0F, -5.1832F, -0.1134F, 0.0F, -1.5708F, 1.1781F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(56, 58)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(24, 53)
               .addBox(-1.25F, 11.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r22 = right_leg.addOrReplaceChild(
            "cube_r22",
            CubeListBuilder.create().texOffs(64, 76).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.2F, 10.0F, -3.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(0, 62)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(16, 69)
               .addBox(-1.0F, 11.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r23 = left_leg.addOrReplaceChild(
            "cube_r23",
            CubeListBuilder.create().texOffs(52, 74).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.0F, -3.0F, 0.3927F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
