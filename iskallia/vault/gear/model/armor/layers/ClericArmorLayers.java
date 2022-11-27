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

public class ClericArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ClericArmorLayers.LeggingsLayer::createBodyLayer : ClericArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ClericArmorLayers.LeggingsLayer::new : ClericArmorLayers.MainLayer::new;
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
               .texOffs(19, 0)
               .addBox(-2.0F, 9.0F, -4.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 0)
               .addBox(1.0F, 9.0F, -4.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
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
               .texOffs(0, 28)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 28)
               .addBox(-1.0F, -6.0F, -6.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(62, 42)
               .addBox(-2.0F, -12.0F, -6.0F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(32, 34)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(32, 59)
               .addBox(-2.0F, 5.0F, -4.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(-3.0F, 5.0F, -3.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(1.0F, 5.0F, -3.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 0)
               .addBox(-3.0F, 5.0F, 3.0F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(61, 64).addBox(-2.0F, -2.0F, -1.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 11.0F, 3.5F, -0.5236F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(23, 50).addBox(-4.0F, -2.0F, -3.5F, 8.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 3.0F, 4.5F, 0.6109F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 44).addBox(-5.0F, -4.5F, -2.0F, 10.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 5.5F, -2.0F, 0.48F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(60, 14)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(49, 43)
               .addBox(-5.0F, 5.0F, -3.5F, 3.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r4 = right_arm.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(30, 22)
               .addBox(-3.5556F, -2.6F, -3.25F, 9.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(-2.5556F, -1.6F, -4.25F, 10.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.4444F, -1.5F, 0.0F, 0.0F, 0.0F, -0.5236F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(16, 59)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(54, 0)
               .addBox(2.0F, 5.0F, -3.5F, 3.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r5 = left_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(30, 8)
               .addBox(-5.5833F, -2.5F, -3.25F, 9.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-7.5833F, -1.5F, -4.25F, 10.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(7.3333F, -1.5F, 0.0F, 0.0F, 0.0F, 0.5236F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(45, 57)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(56, 34)
               .addBox(-2.0F, 6.0F, -4.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(0, 55)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(44, 0)
               .addBox(-1.8F, 6.0F, -4.0F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
