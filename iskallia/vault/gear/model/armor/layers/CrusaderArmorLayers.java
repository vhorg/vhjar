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

public class CrusaderArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? CrusaderArmorLayers.LeggingsLayer::createBodyLayer : CrusaderArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? CrusaderArmorLayers.LeggingsLayer::new : CrusaderArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(0, 25)
               .addBox(-1.5F, -13.0F, 5.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 50)
               .addBox(-1.5F, -16.0F, 5.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(47, 18)
               .addBox(-1.5F, -18.0F, 2.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-2.5F, -16.0F, 0.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(1.5F, -16.0F, 0.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(47, 9)
               .addBox(-1.5F, -17.0F, -1.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(-0.5F, -14.0F, 0.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(46, 0)
               .addBox(-9.0438F, -9.0F, -4.2832F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(49, 49)
               .addBox(-9.0438F, -3.0F, -4.2832F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5161F, 0.0F, 0.1884F, 0.0F, -0.6545F, 0.0F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(46, 0)
               .addBox(1.1637F, -9.0F, -1.4569F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(49, 49)
               .addBox(1.1637F, -3.0F, -1.4569F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, 0.0F, -3.0F, 0.0F, 0.6545F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(28, 25)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(32, 41)
               .addBox(-4.0F, 2.0F, 3.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(-5.5F, 11.0F, -3.5F, 11.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(18, 0).addBox(-7.8378F, 0.4276F, -3.0F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, 13.0F, 0.0F, 0.0F, 0.0F, -1.8326F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(18, 0).addBox(-8.0F, -1.3F, -3.0F, 8.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0F, 13.0F, 0.0F, 0.0F, 0.0F, -1.2217F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(32, 41).addBox(-4.0F, -4.0F, -0.5F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 6.0F, -3.5F, 0.3054F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(16, 39)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 25)
               .addBox(-5.0F, -4.0F, -4.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(32, 0)
               .addBox(-5.0F, 2.0F, -4.0F, 3.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r7 = right_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(29, 9).addBox(0.0F, -7.5F, -3.5F, 2.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0F, 8.5F, 0.0F, 0.0F, 0.0F, -0.7418F)
         );
         PartDefinition cube_r8 = right_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(29, 9).addBox(-1.0F, -6.5F, -3.5F, 2.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0F, 4.5F, 0.0F, 0.0F, 0.0F, -0.6545F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(0, 25)
               .addBox(-1.0F, -4.0F, -4.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(32, 0)
               .addBox(2.0F, 2.0F, -4.0F, 3.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(16, 39)
               .mirror()
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .mirror(false),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r9 = left_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(29, 9).addBox(-1.0F, -6.5F, -3.5F, 2.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0F, 4.5F, 0.0F, 0.0F, 0.0F, 0.6109F)
         );
         PartDefinition cube_r10 = left_arm.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(29, 9).addBox(-2.0F, -7.5F, -3.5F, 2.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0F, 8.5F, 0.0F, -0.0174F, -0.0013F, 0.7727F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 39).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 39).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
