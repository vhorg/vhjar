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

public class WingedWarriorArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS
         ? WingedWarriorArmorLayers.LeggingsLayer::createBodyLayer
         : WingedWarriorArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? WingedWarriorArmorLayers.LeggingsLayer::new : WingedWarriorArmorLayers.MainLayer::new;
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
            CubeListBuilder.create()
               .texOffs(0, 16)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
               .texOffs(0, 32)
               .addBox(-1.75F, 2.0F, -3.0F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 32)
               .addBox(-1.75F, 2.0F, 2.0F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r1 = right_leg.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(24, 7).addBox(-4.0F, -3.5F, 0.0F, 8.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.1F, 3.5F, 2.0F, 0.0F, 0.7854F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(8, 32)
               .addBox(-1.3F, 2.0F, -3.0F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 14)
               .addBox(-1.3F, 2.0F, 2.0F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 16)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r2 = left_leg.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(24, 0).addBox(-4.0F, -3.5F, 0.0F, 8.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.1F, 3.5F, 2.0F, 0.0F, -0.7854F, 0.0F)
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
               .texOffs(40, 47)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(37, 6)
               .addBox(-5.5F, -7.0F, -5.5F, 11.0F, 2.0F, 11.0F, new CubeDeformation(0.0F))
               .texOffs(40, 19)
               .addBox(-1.0F, -8.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(74, 28)
               .addBox(0.0F, -5.0F, -3.5F, 0.0F, 10.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(0, 79)
               .addBox(-11.25F, -5.0F, -3.5F, 0.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.75F, -8.0F, 1.0F, -0.7854F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(40, 63)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-6.0F, 0.0F, -6.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -16.5F, -10.0F, 0.0F, 33.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-9.0F, -1.0F, 15.0F, 0.8713F, -0.5476F, -0.0241F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 33).addBox(0.0F, -16.5F, -10.0F, 0.0F, 33.0F, 20.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(9.0F, 0.75F, 15.0F, 0.6995F, 0.5476F, -0.0241F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(72, 54).addBox(-3.0F, -3.0F, -1.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 7.3863F, 3.0782F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(80, 70).addBox(-3.0F, -3.25F, 0.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 8.0F, -4.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(40, 79)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(36, 0)
               .addBox(-5.0F, 5.0F, -2.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r6 = right_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(64, 63)
               .addBox(-3.5F, -4.0F, -3.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(40, 33)
               .addBox(-4.5F, -3.0F, -4.0F, 9.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, -1.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(74, 19)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 0)
               .addBox(3.0F, 5.0F, -2.0F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r7 = left_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create()
               .texOffs(64, 47)
               .addBox(-3.5F, -4.0F, -3.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(40, 19)
               .addBox(-4.5F, -3.0F, -4.0F, 9.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.5F, -1.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(64, 70)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(40, 37)
               .addBox(-1.25F, 11.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r8 = right_leg.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(80, 78).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.2F, 10.0F, -3.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(70, 0)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(40, 33)
               .addBox(-1.0F, 11.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r9 = left_leg.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(84, 45).addBox(-2.0F, -3.0F, -1.0F, 4.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.0F, -3.0F, 0.3927F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
