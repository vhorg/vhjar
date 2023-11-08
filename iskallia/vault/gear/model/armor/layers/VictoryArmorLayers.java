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

public class VictoryArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? VictoryArmorLayers.LeggingsLayer::createBodyLayer : VictoryArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? VictoryArmorLayers.LeggingsLayer::new : VictoryArmorLayers.MainLayer::new;
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
               .texOffs(12, 32)
               .addBox(-1.0F, 11.0F, -4.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(28, 12)
               .addBox(0.25F, 10.0F, -3.25F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(30, 30)
               .addBox(-3.25F, 10.5F, -3.5F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(12, 16)
               .addBox(-2.0F, 9.0F, -3.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 32)
               .addBox(-3.0F, 12.0F, -3.0F, 6.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-3.0F, 12.0F, 2.75F, 6.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)),
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
      public MainLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create().texOffs(0, 19).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -3.5F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -8.5F, -2.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(0, 49)
               .addBox(-0.75F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(36, 74)
               .addBox(-13.25F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -9.5F, 3.0F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -3.5F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.0F, -8.5F, -2.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(44, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(24, 19)
               .addBox(-3.0F, 4.0F, -4.5F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-6.0F, -1.0F, -4.0F, 12.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(62, 31).addBox(-3.0F, -5.0F, -1.0F, 6.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.2779F, 4.0F, -4.0F, 0.4674F, -0.3542F, -0.1733F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(66, 14).addBox(-3.0F, -5.0F, -1.0F, 6.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, 4.0F, -4.0F, 0.4674F, 0.3542F, 0.1733F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(0, 65)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 55)
               .addBox(-5.0F, 4.0F, -4.0F, 4.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(46, 35)
               .addBox(-5.0F, 7.0F, -4.0F, 4.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r6 = right_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(46, 48).addBox(-4.9217F, -2.7314F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0783F, -1.2686F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r7 = right_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(0, 35).addBox(-2.2349F, -2.1941F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0783F, -1.2686F, 0.0F, 0.0F, 0.0F, -0.9599F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(20, 63)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(46, 16)
               .addBox(1.0F, 5.0F, -4.0F, 4.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 35)
               .addBox(4.0F, 8.0F, 1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 19)
               .addBox(4.0F, 8.0F, -3.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r8 = left_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(22, 43)
               .addBox(-3.0F, -5.0F, -4.5F, 2.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(24, 27)
               .addBox(-1.0F, -4.0F, -3.5F, 7.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0F, -2.0F, -0.5F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(58, 60).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r9 = right_leg.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(68, 0).addBox(-4.0F, -3.0F, -1.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.8F, 10.0F, -3.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r10 = right_leg.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(38, 19).addBox(-4.0F, 0.0F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.8F, 10.0F, 2.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(42, 60).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r11 = left_leg.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(68, 46).addBox(0.0F, -3.0F, -1.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, 10.0F, -3.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r12 = left_leg.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(36, 46).addBox(0.0F, 0.0F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, 10.0F, 2.0F, 0.3927F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
