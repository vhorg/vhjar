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

public class PaladinArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? PaladinArmorLayers.LeggingsLayer::createBodyLayer : PaladinArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? PaladinArmorLayers.LeggingsLayer::new : PaladinArmorLayers.MainLayer::new;
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
               .texOffs(0, 8)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
               .texOffs(24, 8)
               .addBox(-1.0F, 10.0F, -4.0F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 3)
               .addBox(-2.0F, 11.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 0)
               .addBox(1.0F, 11.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(34, 7)
               .addBox(-3.0F, 9.0F, -4.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 15)
               .addBox(-4.0F, 10.0F, -4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 24)
               .addBox(-6.25F, 10.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 0)
               .addBox(-5.25F, 9.0F, 1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 31)
               .addBox(-5.25F, 9.0F, -2.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 8)
               .addBox(-6.25F, 10.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(5.25F, 10.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 8)
               .addBox(5.25F, 10.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 24)
               .addBox(4.25F, 9.0F, -2.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 8)
               .addBox(4.25F, 9.0F, 1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 24)
               .addBox(3.0F, 10.0F, -4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 15)
               .addBox(2.0F, 9.0F, -4.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-5.0F, 11.0F, -3.0F, 10.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(20, 20).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
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
               .texOffs(0, 0)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(8, 63)
               .addBox(-1.0F, -6.0F, -6.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(62, 49)
               .addBox(-2.0F, -12.0F, -6.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(52, 61)
               .addBox(-7.0F, -8.0F, -3.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(44, 61)
               .addBox(-8.0F, -5.0F, -3.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-9.0F, 2.0F, -3.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(20, 16)
               .addBox(-8.0F, 4.0F, -3.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(12, 67)
               .addBox(6.0F, -8.0F, -3.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 63)
               .addBox(6.0F, -5.0F, -3.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(40, 27)
               .addBox(6.0F, 2.0F, -3.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(44, 11)
               .addBox(5.0F, 4.0F, -3.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -11.0F, 0.0F, 0.4363F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 16)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(60, 0)
               .addBox(-2.0F, 5.0F, -4.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(46, 15)
               .addBox(-3.0F, 5.0F, -3.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(1.0F, 5.0F, -3.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 32)
               .addBox(-3.0F, 5.0F, 3.0F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(16, 47).addBox(-2.0F, -2.0F, -1.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 11.0F, 3.5F, -0.5236F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, -2.0F, -3.5F, 8.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 3.0F, 4.5F, 0.6109F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(32, 0).addBox(-5.0F, -4.5F, -2.0F, 10.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 5.5F, -2.0F, 0.48F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(16, 52)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(26, 38)
               .addBox(-5.0F, 5.0F, -3.5F, 3.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r5 = right_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(58, 44)
               .addBox(2.4444F, 0.4F, 3.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(68, 13)
               .addBox(-3.5556F, 2.4F, 3.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 31)
               .addBox(-5.5556F, 2.4F, 3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(52, 66)
               .addBox(-1.5556F, 1.4F, 3.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 43)
               .addBox(-5.5556F, 2.4F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(68, 16)
               .addBox(-3.5556F, 2.4F, -4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(62, 66)
               .addBox(-1.5556F, 1.4F, -4.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 60)
               .addBox(2.4444F, 0.4F, -4.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(58, 57)
               .addBox(-1.5556F, -1.6F, -2.0F, 7.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(18, 27)
               .addBox(-0.5556F, -0.6F, -3.0F, 8.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.4444F, -1.5F, 0.0F, 0.0F, 0.0F, -0.5236F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(52, 11)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(39, 31)
               .addBox(2.0F, 5.0F, -3.5F, 3.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r6 = left_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(52, 27)
               .addBox(-5.5833F, -1.5F, -2.0F, 7.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(24, 16)
               .addBox(-7.5833F, -0.5F, -3.0F, 8.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(32, 11)
               .addBox(-7.5833F, 0.5F, -4.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(21, 38)
               .addBox(-7.5833F, 0.5F, 3.0F, 5.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(64, 8)
               .addBox(-2.5833F, 1.5F, 3.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 65)
               .addBox(-2.5833F, 1.5F, -4.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 4)
               .addBox(1.4167F, 2.5F, -4.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 6)
               .addBox(3.4167F, 2.5F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(50, 27)
               .addBox(1.4167F, 2.5F, 3.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 20)
               .addBox(3.4167F, 2.5F, 3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(7.3333F, -1.5F, 0.0F, 0.0F, 0.0F, 0.5236F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(0, 47)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(59, 36)
               .addBox(-2.0F, 6.0F, -4.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(46, 45)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(32, 52)
               .addBox(-1.8F, 6.0F, -4.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
