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

public class ScarecrowArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ScarecrowArmorLayers.LeggingsLayer::createBodyLayer : ScarecrowArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ScarecrowArmorLayers.LeggingsLayer::new : ScarecrowArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 32);
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
               .texOffs(0, 32)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 0)
               .addBox(-7.0F, -7.0F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
               .texOffs(0, 15)
               .addBox(-5.0F, -16.0F, -5.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(30, 19)
               .addBox(1.0F, -2.0F, -4.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(50, 5)
               .addBox(1.5F, 0.0F, -2.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.5F, 0.661F, -8.6148F, 0.5661F, -0.5338F, -0.3127F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(30, 22).addBox(1.0F, 3.5F, 0.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -16.5F, 0.0F, 0.5672F, 0.0F, -0.3927F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(32, 33)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(30, 15)
               .addBox(-11.0F, 0.0F, 3.0F, 22.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 48)
               .addBox(-1.0F, 2.0F, 3.0F, 2.0F, 23.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 32)
               .addBox(-1.0F, 2.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 15).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, 3.0F, -3.5F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 20).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, 3.0F, -3.5F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(56, 33).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r5 = right_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(42, 0).addBox(-0.5F, -2.5F, -3.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, 8.5F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r6 = right_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(56, 55).addBox(-3.0F, -2.5F, -0.5F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.0F, 8.5F, -3.5F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r7 = right_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(58, 0).addBox(-3.0F, -2.5F, -0.5F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.0F, 8.5F, 3.5F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(40, 49).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r8 = left_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(56, 49).addBox(-3.0F, -2.5F, -0.5F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.0F, 8.5F, -3.5F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r9 = left_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(60, 19).addBox(-3.0F, -2.5F, -0.5F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.0F, 8.5F, 3.5F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r10 = left_arm.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -2.5F, -3.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, 8.5F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(24, 49).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(8, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}