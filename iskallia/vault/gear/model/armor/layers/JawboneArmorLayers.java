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

public class JawboneArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? JawboneArmorLayers.LeggingsLayer::createBodyLayer : JawboneArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? JawboneArmorLayers.LeggingsLayer::new : JawboneArmorLayers.MainLayer::new;
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
               .texOffs(22, 17)
               .addBox(-2.0F, 10.0F, -5.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 16).addBox(3.5F, -3.5F, -4.0F, 7.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, 12.5F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(22, 8).addBox(-3.5F, -0.5F, -4.0F, 7.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, 12.5F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
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
               .texOffs(0, 15)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(48, 26)
               .addBox(-5.0F, -4.0F, -7.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 49)
               .addBox(-2.0F, -3.0F, -7.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(46, 10)
               .addBox(4.0F, -4.0F, -7.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(7, 0)
               .addBox(1.0F, -3.0F, -7.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -2.0F, -5.5F, 12.0F, 4.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -2.0F, -1.5F, 0.48F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(56, 35)
               .addBox(-1.0F, -10.5F, 4.5F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-5.0F, -10.5F, 4.5F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, 6.5F, 7.5F, 0.8727F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(32, 33)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(24, 15)
               .addBox(-4.0F, 1.0F, -4.0F, 8.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 33)
               .addBox(-2.0F, 8.0F, -4.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 45)
               .addBox(-2.0F, 8.0F, 3.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(12, 41)
               .addBox(-1.0F, 11.0F, 3.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(35, 0)
               .addBox(-4.0F, 1.0F, 3.0F, 8.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(0, 57)
               .addBox(-1.0F, -3.5F, -1.5F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(57, 0)
               .addBox(3.0F, -3.5F, -1.5F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, 6.5F, 7.5F, 0.8727F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(32, 49)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(42, 19)
               .addBox(-5.0F, -2.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 15)
               .addBox(-5.0F, -2.0F, -3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r4 = right_arm.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 31).addBox(-0.25F, -3.5F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, 13.5F, 0.0F, 0.0F, 0.0F, -0.48F)
         );
         PartDefinition cube_r5 = right_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(56, 26).addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0F, 8.5F, 0.0F, 0.0F, 0.0F, 0.3054F)
         );
         PartDefinition cube_r6 = right_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(0, 31).addBox(-4.5F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(28, 41)
               .addBox(4.0F, -2.0F, -3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 41)
               .addBox(4.0F, -2.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(48, 10)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r7 = left_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(48, 49).addBox(-1.725F, -4.3672F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.13F, 10.4988F, 0.0F, 3.1416F, 0.0F, 2.8362F)
         );
         PartDefinition cube_r8 = left_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(0, 15).addBox(-1.3076F, -0.667F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.13F, 10.4988F, 0.0F, -3.1416F, 0.0F, -2.6616F)
         );
         PartDefinition cube_r9 = left_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(24, 23).addBox(-3.5F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(16, 41)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(20, 57)
               .addBox(-2.0F, 8.0F, -4.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(10, 57)
               .addBox(-1.8F, 8.0F, -4.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 41)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
