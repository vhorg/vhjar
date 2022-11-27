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

public class SpikyPlatemailArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS
         ? SpikyPlatemailArmorLayers.LeggingsLayer::createBodyLayer
         : SpikyPlatemailArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SpikyPlatemailArmorLayers.LeggingsLayer::new : SpikyPlatemailArmorLayers.MainLayer::new;
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
            CubeListBuilder.create()
               .texOffs(0, 16)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
               .texOffs(0, 0)
               .addBox(-3.0F, 3.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(2.0F, 3.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
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
               .texOffs(0, 25)
               .addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(24, 50)
               .addBox(-6.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(24, 50)
               .addBox(5.0F, -4.0F, -5.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 19)
               .addBox(-6.0F, -1.0F, -6.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 19)
               .addBox(3.0F, -1.0F, -6.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-1.0F, -9.0F, -6.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 41)
               .addBox(-1.0F, -9.0F, -5.0F, 2.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(-1.0F, -9.0F, 5.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(30, 18)
               .addBox(-4.0F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 18)
               .addBox(-9.0F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -7.4944F, -4.5165F, 0.8727F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, 0.5F, -5.5F, 0.0F, -0.3927F, 0.0F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.5F, 0.5F, -5.5F, 0.0F, 0.3927F, 0.0F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(4, 25).addBox(-0.5F, -2.75F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.1F, -7.75F, 5.0F, 0.0F, 0.0F, 0.6545F)
         );
         PartDefinition cube_r5 = head.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(4, 25).addBox(-0.5F, -2.75F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.6F, -9.5F, 3.5F, 0.0F, 0.0F, 0.6545F)
         );
         PartDefinition cube_r6 = head.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(32, 32).addBox(-0.5F, -2.25F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.6F, -9.0F, -0.5F, 0.0F, 0.0F, 0.5236F)
         );
         PartDefinition cube_r7 = head.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(4, 25).addBox(-0.5F, -2.75F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.9F, -7.75F, 4.5F, 0.0F, 0.0F, -0.6109F)
         );
         PartDefinition cube_r8 = head.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(4, 25).addBox(-0.5F, -2.75F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.4F, -9.25F, 3.0F, 0.0F, 0.0F, -0.6109F)
         );
         PartDefinition cube_r9 = head.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(32, 32).addBox(-0.5F, -2.25F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.4F, -8.75F, -1.0F, 0.0F, 0.0F, -0.5236F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(40, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-6.0F, -2.0F, -4.0F, 12.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(-5.5F, 4.0F, -4.0F, 11.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(31, 18)
               .addBox(-5.25F, 7.0F, -3.5F, 10.0F, 3.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(0, 52)
               .addBox(-5.0F, -1.0F, -5.0F, 10.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(38, 44)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(32, 32)
               .addBox(-6.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(14, 41)
               .mirror()
               .addBox(-5.0F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r10 = right_arm.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(30, 16).addBox(-1.0F, 0.0F, 2.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0F, -5.0F, -4.0F, 0.0F, 0.0F, 0.829F)
         );
         PartDefinition cube_r11 = right_arm.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(24, 30).addBox(-3.0F, 0.0F, 2.0F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0F, -5.0F, -2.0F, 0.0F, 0.0F, 0.829F)
         );
         PartDefinition cube_r12 = right_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(30, 16).addBox(-1.5F, 0.0F, 2.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(13.0F, -4.0F, -4.0F, 0.0F, 0.0F, 2.3562F)
         );
         PartDefinition cube_r13 = right_arm.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create().texOffs(24, 30).addBox(-3.0F, 0.0F, 2.0F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(14.0F, -4.0F, -2.0F, 0.0F, 0.0F, 2.3562F)
         );
         PartDefinition cube_r14 = right_arm.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(24, 28).addBox(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(15.0F, -1.0F, 0.0F, 0.0F, 0.0F, 2.618F)
         );
         PartDefinition cube_r15 = right_arm.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create().texOffs(24, 28).addBox(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(15.0F, -4.0F, 0.0F, 0.0F, 0.0F, 2.618F)
         );
         PartDefinition cube_r16 = right_arm.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create()
               .texOffs(0, 25)
               .addBox(-0.125F, -4.5F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 25)
               .addBox(-19.125F, -4.5F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(14.125F, -4.5F, 3.0F, -0.6545F, 0.0F, 0.0F)
         );
         PartDefinition cube_r17 = right_arm.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create().texOffs(24, 28).addBox(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.4363F)
         );
         PartDefinition cube_r18 = right_arm.addOrReplaceChild(
            "cube_r18",
            CubeListBuilder.create().texOffs(24, 28).addBox(-4.0F, 0.0F, 2.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.4363F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(38, 44)
               .mirror()
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .mirror(false)
               .texOffs(14, 41)
               .addBox(-0.25F, 5.0F, -3.5F, 5.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(32, 32)
               .addBox(-1.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(53, 28)
               .addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(44, 5)
               .addBox(-3.25F, 8.0F, -3.75F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 5)
               .addBox(0.75F, 8.0F, -3.75F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(43, 5)
               .addBox(-2.25F, 7.0F, -3.75F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(43, 5)
               .addBox(1.75F, 7.0F, -3.75F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(53, 28).mirror().addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
