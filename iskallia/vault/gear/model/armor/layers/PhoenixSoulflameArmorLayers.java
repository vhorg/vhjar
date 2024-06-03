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

public class PhoenixSoulflameArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS
         ? PhoenixSoulflameArmorLayers.LeggingsLayer::createBodyLayer
         : PhoenixSoulflameArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? PhoenixSoulflameArmorLayers.LeggingsLayer::new : PhoenixSoulflameArmorLayers.MainLayer::new;
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
               .texOffs(28, 36)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 79)
               .addBox(-5.5F, -9.0F, -6.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(12, 75)
               .addBox(3.5F, -9.0F, -6.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(56, 65).addBox(-6.0F, -4.0F, 2.0F, 9.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-8.0F, -8.0F, -4.0F, 0.0F, 0.7854F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(68, 0).addBox(-3.0F, -4.0F, 2.0F, 9.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(8.0F, -8.0F, -4.0F, 0.0F, -0.7854F, 0.0F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(22, 81).addBox(-2.2929F, -2.5F, -4.1213F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0167F, -8.5F, -3.537F, 0.4215F, 0.3614F, 0.1572F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(34, 81).addBox(-1.7071F, -2.5F, -4.1213F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0167F, -8.5F, -3.537F, 0.4215F, -0.3614F, -0.1572F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 44)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-6.0F, 0.0F, -5.0F, 12.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(0, 18)
               .addBox(-5.0F, -2.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(72, 52)
               .addBox(-12.4351F, -1.3087F, -5.25F, 8.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(72, 29)
               .addBox(4.5649F, -1.3087F, -5.25F, 8.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(44, 13)
               .addBox(4.5649F, -1.3087F, 5.25F, 8.0F, 9.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(72, 8)
               .addBox(-12.4351F, -1.3087F, 5.25F, 8.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(72, 71).addBox(-3.0F, -2.0F, -2.25F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.0F, 4.0F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(0, 73).addBox(-3.0F, -2.0F, 0.25F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.0F, -4.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create()
               .texOffs(76, 38)
               .addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(76, 45)
               .addBox(-2.5F, -2.5F, -10.5F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, 3.5F, 4.5F, 0.0F, 0.0F, -0.48F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(76, 17)
               .addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(70, 77)
               .addBox(-2.5F, -2.5F, -10.5F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, 3.5F, 4.5F, 0.0F, 0.0F, 0.48F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(56, 73).addBox(-2.5F, -3.0F, -0.5F, 6.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.8512F, 2.0F, -5.5F, 0.4565F, -0.5194F, -0.2391F)
         );
         PartDefinition cube_r10 = body.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(74, 61).addBox(-3.5F, -3.0F, -0.5F, 6.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.8284F, 2.0F, -5.5F, 0.4565F, 0.5194F, 0.2391F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(40, 65)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 60)
               .addBox(-15.4351F, -7.3087F, 1.75F, 12.0F, 13.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(48, 52)
               .addBox(-15.4351F, -7.3087F, -2.25F, 12.0F, 13.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r11 = right_arm.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(32, 22).addBox(-5.0F, -3.0F, -4.0F, 10.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(44, 0)
               .addBox(3.3149F, -7.3087F, -2.25F, 12.0F, 13.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(24, 52)
               .addBox(3.3149F, -7.3087F, 1.75F, 12.0F, 13.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(24, 65)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r12 = left_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(0, 30).addBox(-5.0F, -3.0F, -4.0F, 10.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(60, 36).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(60, 13).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
