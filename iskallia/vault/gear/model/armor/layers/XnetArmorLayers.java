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

public class XnetArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? XnetArmorLayers.LeggingsLayer::createBodyLayer : XnetArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? XnetArmorLayers.LeggingsLayer::new : XnetArmorLayers.MainLayer::new;
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
            CubeListBuilder.create()
               .texOffs(16, 16)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
               .texOffs(4, 32)
               .addBox(-1.0F, 1.0F, 3.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 16)
               .addBox(-1.0F, 1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(-1.0F, 7.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 1)
               .addBox(-3.0F, 3.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(28, 28)
               .addBox(-3.0F, 5.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(28, 15)
               .addBox(-3.0F, 7.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(24, 0)
               .addBox(1.95F, 7.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(24, 5)
               .addBox(1.95F, 5.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(24, 10)
               .addBox(1.95F, 3.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
               .texOffs(0, 0)
               .addBox(0.2F, 7.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 32)
               .addBox(0.2F, 1.0F, 3.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 2)
               .addBox(0.2F, 1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
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
               .texOffs(0, 0)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(50, 37)
               .addBox(-6.0F, -6.0F, -2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(46, 40)
               .addBox(5.0F, -6.0F, -2.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-6.0F, -6.0F, -6.0F, 12.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(24, 26)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(44, 24)
               .addBox(-4.0F, 1.0F, 3.0F, 8.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(28, 21)
               .addBox(-3.0F, 4.0F, 3.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(48, 8)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 16)
               .addBox(-4.5F, 5.0F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(0, 30)
               .addBox(-4.5F, -2.0F, -3.5F, 4.0F, 6.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(24, 8)
               .addBox(-5.5F, 7.25F, -4.0F, 4.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(21, 21)
               .addBox(-7.0F, 8.0F, 2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(48, 30)
               .addBox(-7.0F, 0.0F, 2.0F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 6)
               .addBox(-6.0F, 0.0F, 2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r1 = right_arm.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, 8.2069F, -3.3363F, -1.3526F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = right_arm.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(15, 30).addBox(-0.5F, -4.0F, -2.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, 14.0F, -4.0F, -0.6545F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = right_arm.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 30).addBox(-0.5F, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, 14.0F, 4.0F, 0.4363F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = right_arm.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, -2.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, 14.0F, 1.0F, 0.2598F, 0.0173F, -0.0254F)
         );
         PartDefinition cube_r5 = right_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(40, 8).addBox(-0.5F, -2.25F, -2.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, 14.0F, -1.0F, -0.3054F, 0.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(0, 43).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(34, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(18, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
