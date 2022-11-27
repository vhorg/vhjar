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

public class FluxArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? FluxArmorLayers.LeggingsLayer::createBodyLayer : FluxArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? FluxArmorLayers.LeggingsLayer::new : FluxArmorLayers.MainLayer::new;
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
               .texOffs(0, 0)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 24)
               .addBox(-6.0F, -6.0F, -6.0F, 12.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(-6.0F, -4.0F, -6.0F, 12.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(68, 69)
               .addBox(5.0F, -7.0F, 1.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(65, 9)
               .addBox(-6.0F, -7.0F, 1.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 24)
               .addBox(5.75F, -6.0F, 2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(4, 50)
               .addBox(5.0F, -11.0F, 2.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-5.0F, -7.0F, 5.0F, 10.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(-1.0F, -11.0F, -5.75F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 32)
               .addBox(-7.0F, -5.0F, 2.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(31, 24)
               .addBox(-7.0F, -5.0F, 4.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(3, 45)
               .addBox(-6.0F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 6)
               .addBox(-6.0F, -1.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(23, 41)
               .addBox(-1.0F, -5.75F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(64, 36)
               .addBox(2.0F, -5.75F, -0.5F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.0F, -11.0F, -5.0F, 0.0F, 0.0F, 0.6545F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(23, 43)
               .addBox(-2.0F, -5.75F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(64, 61)
               .addBox(-4.0F, -5.75F, -0.5F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.0F, -11.0F, -5.0F, 0.0F, 0.0F, -0.6545F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(32, 6).addBox(-0.5F, -3.5F, 1.5F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.75F, -9.25F, 3.25F, -0.4363F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(32, 34)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(46, 0)
               .addBox(-3.0F, 2.0F, 3.0F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(38, 18)
               .addBox(-3.0F, 1.0F, -4.0F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(52, 18)
               .addBox(-2.0F, 6.0F, -3.5F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(52, 34)
               .addBox(1.75F, 2.25F, 4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(3, 37)
               .addBox(-2.75F, 2.25F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 35)
               .addBox(-1.25F, 2.25F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(25, 32)
               .addBox(-1.25F, 5.25F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(35, 13)
               .addBox(0.25F, 2.25F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 29)
               .addBox(1.0F, 2.25F, -4.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(31, 18)
               .addBox(-2.0F, 2.25F, -4.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 4)
               .addBox(3.75F, 2.25F, 3.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(26, 50)
               .addBox(-4.75F, 2.25F, 3.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(34, 29)
               .addBox(0.25F, 5.25F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(25, 36)
               .addBox(1.75F, 2.25F, 6.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 50)
               .addBox(0.25F, 2.25F, 5.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 41)
               .addBox(-1.25F, 2.25F, 5.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 6)
               .addBox(-4.75F, 2.25F, 5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(24, 6)
               .addBox(-1.0F, 0.5F, 0.3333F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(23, 45)
               .addBox(1.0F, 0.5F, -1.6667F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(32, 14)
               .addBox(-1.0F, 0.5F, -0.6667F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.25F, 5.75F, 4.6667F, 0.0F, 0.0F, 0.8727F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(25, 34)
               .addBox(-2.25F, 0.5F, 1.3333F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-2.25F, 0.5F, -1.6667F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(40, 50)
               .addBox(-0.25F, 0.5F, -0.6667F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.25F, 5.75F, 4.6667F, 0.0F, 0.0F, -0.8727F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(48, 61)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(57, 0)
               .addBox(-5.0F, 0.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(56, 52)
               .addBox(-5.0F, 2.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(56, 27)
               .addBox(-5.0F, 4.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(56, 18)
               .addBox(-5.0F, 6.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r6 = right_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(0, 41).addBox(-3.5F, -0.5F, -3.5F, 8.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, -3.0F, 0.0F, 0.0F, 0.0F, 0.1309F)
         );
         PartDefinition cube_r7 = right_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(31, 8).addBox(-4.5F, -3.0F, -4.0F, 9.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, -3.0F, 0.0F, 0.0F, 0.0F, 0.4363F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(32, 60)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(40, 51)
               .addBox(1.0F, 0.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(24, 50)
               .addBox(1.0F, 2.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 50)
               .addBox(1.0F, 4.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(48, 42)
               .addBox(1.0F, 6.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r8 = left_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(0, 32).addBox(-4.5F, -0.5F, -3.5F, 9.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, -3.0F, 0.0F, 0.0F, 0.0F, -0.1309F)
         );
         PartDefinition cube_r9 = left_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(30, 24).addBox(-4.5F, -3.0F, -4.0F, 9.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, -3.0F, 0.0F, 0.0F, 0.0F, -0.4363F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 59).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r10 = right_leg.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(72, 18).addBox(-2.0F, -3.5F, -0.5F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 9.5F, -3.5F, 0.0873F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 59).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r11 = left_leg.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(16, 50).addBox(-2.0F, -3.5F, -0.5F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.2F, 9.5F, -3.5F, 0.0873F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
