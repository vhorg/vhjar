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

public class BardArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? BardArmorLayers.LeggingsLayer::createBodyLayer : BardArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? BardArmorLayers.LeggingsLayer::new : BardArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
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
         return LayerDefinition.create(meshdefinition, 32, 32);
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
            CubeListBuilder.create().texOffs(0, 27).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(10, 0)
               .addBox(-4.1622F, -7.1054F, -1.654F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(6, 0)
               .addBox(-4.1622F, -9.1054F, 3.346F, 0.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 26)
               .addBox(-4.1622F, -9.1054F, 2.346F, 0.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 0)
               .addBox(-4.1622F, -9.1054F, 1.346F, 0.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(48, 14)
               .addBox(-4.1622F, -9.1054F, 0.346F, 0.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(46, 14)
               .addBox(-4.1622F, -8.1054F, -0.654F, 0.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.8378F, -11.4864F, 0.5385F, -0.5672F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(2, 5)
               .addBox(-4.1622F, -1.7636F, -1.0385F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 59)
               .addBox(-4.1622F, -0.7636F, -3.0385F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(41, 42)
               .addBox(-4.1622F, 0.2364F, -7.0385F, 1.0F, 2.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(40, 0)
               .addBox(-1.1622F, -2.7636F, -5.0385F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 13)
               .addBox(-2.1622F, -1.7636F, -6.0385F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(60, 34)
               .addBox(-1.1622F, 2.2364F, -11.0385F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(53, 32)
               .addBox(-2.1622F, 2.2364F, -10.0385F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(53, 30)
               .addBox(-2.1622F, 2.2364F, 5.9615F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(53, 28)
               .addBox(-3.1622F, 2.2364F, -9.0385F, 12.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 13)
               .addBox(-4.1622F, 2.2364F, 4.9615F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 9)
               .addBox(-4.1622F, 2.2364F, -8.0385F, 14.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-4.1622F, 2.2364F, -7.0385F, 14.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.8378F, -11.4864F, 0.5385F, 0.1745F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(28, 15).addBox(3.8444F, 8.6166F, -7.0385F, 3.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.8378F, -11.4864F, 0.5385F, 0.1129F, 0.1334F, -0.8651F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 43)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 17)
               .addBox(-2.0F, 1.0F, -3.6F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 28)
               .addBox(-3.0F, 3.0F, -4.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 10)
               .addBox(-2.0F, 7.0F, -4.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(16, 59)
               .addBox(-5.0F, -3.25F, 0.6F, 2.0F, 15.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(8, 7)
               .addBox(-7.0F, -2.25F, -0.4F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 7)
               .addBox(-7.0F, -4.25F, -0.4F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 7)
               .addBox(-2.0F, -4.25F, -0.4F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 7)
               .addBox(-2.0F, -2.25F, -0.4F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 13)
               .addBox(-6.0F, -4.25F, -0.4F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-5.0F, -3.25F, -1.4F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 56)
               .addBox(-7.0F, 4.75F, -1.4F, 6.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -2.75F, 4.4F, 0.0F, 0.0F, -0.6981F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(40, 56)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(25, 41)
               .addBox(-4.5F, 2.0F, -3.5F, 7.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(56, 37)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(32, 28)
               .addBox(-2.5F, 2.0F, -3.5F, 7.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(56, 11).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(24, 54).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
