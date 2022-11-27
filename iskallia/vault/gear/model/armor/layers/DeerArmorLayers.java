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

public class DeerArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? DeerArmorLayers.LeggingsLayer::createBodyLayer : DeerArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? DeerArmorLayers.LeggingsLayer::new : DeerArmorLayers.MainLayer::new;
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
               .texOffs(28, 28)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(36, 16)
               .addBox(-2.0F, -9.0F, -9.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(31, 6)
               .addBox(-4.0F, -10.0F, -5.0F, 8.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-3.0F, -17.0F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(6, 31)
               .addBox(-3.0F, -20.0F, 1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(29, 16)
               .addBox(-8.0F, -18.0F, 1.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(29, 18)
               .addBox(-6.0F, -15.0F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(29, 18)
               .addBox(3.0F, -15.0F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(29, 16)
               .addBox(3.0F, -18.0F, 1.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(1.0F, -13.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(-2.0F, -13.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(6, 31)
               .addBox(2.0F, -20.0F, 1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(6, 31)
               .addBox(8.0F, -21.0F, 1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 0)
               .addBox(6.0F, -24.0F, 1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 20)
               .addBox(5.0F, -23.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 20)
               .addBox(-6.0F, -23.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 0)
               .addBox(-7.0F, -24.0F, 1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(6, 31)
               .addBox(-9.0F, -21.0F, 1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(2.0F, -17.0F, 0.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 15).addBox(-0.2F, -2.0F, 1.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0F, -11.0F, -3.5F, 0.0F, 0.0F, 0.6981F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 15).addBox(-2.0F, -2.1F, 1.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0F, -11.0F, -3.5F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(28, 44)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-6.0F, -2.0F, -4.0F, 12.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 15)
               .addBox(-5.5F, -1.0F, -3.5F, 11.0F, 9.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(52, 52)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 36)
               .addBox(-5.0F, 4.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(-5.0F, 5.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(-5.0F, 5.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(-5.0F, 1.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(-5.0F, -1.0F, -3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 31)
               .addBox(-5.0F, 1.0F, -5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 31)
               .addBox(-5.0F, 1.0F, 3.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(-5.0F, -2.0F, -5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(-5.0F, -2.0F, 4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(-5.0F, -1.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(-5.0F, 1.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(52, 52)
               .mirror()
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .mirror(false)
               .texOffs(0, 31)
               .addBox(4.0F, 1.0F, -5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(4.0F, -2.0F, -5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(4.0F, 1.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(4.0F, -1.0F, -3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(4.0F, 5.0F, -2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(4.0F, 5.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(4.0F, 1.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 31)
               .addBox(4.0F, 1.0F, 3.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(4.0F, -1.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 31)
               .addBox(4.0F, -2.0F, 4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 36)
               .addBox(-3.0F, 4.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 45).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
