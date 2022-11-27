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

public class CakeArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? CakeArmorLayers.LeggingsLayer::createBodyLayer : CakeArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? CakeArmorLayers.LeggingsLayer::new : CakeArmorLayers.MainLayer::new;
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
               .texOffs(0, 17)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 0)
               .addBox(-6.0F, -10.0F, -6.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(0, 17)
               .addBox(-1.0F, -15.0F, -1.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(6, 0)
               .addBox(-0.5F, -16.0F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(66, 0)
               .addBox(-1.5F, -21.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(0, 6)
               .addBox(-6.0F, -5.0F, 1.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(30, 17)
               .addBox(-6.0F, -5.0F, -3.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(6, 6)
               .addBox(-6.0F, -5.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(6, 0)
               .addBox(5.0F, -5.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(5.0F, -5.0F, 1.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(50, 35)
               .addBox(5.0F, -5.0F, -3.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(42, 0)
               .addBox(-4.0F, -5.0F, -6.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 37)
               .addBox(-1.0F, -5.0F, -6.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 7)
               .addBox(-4.0F, -5.0F, 5.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(37, 22)
               .addBox(1.0F, -5.0F, 5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(28, 37).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(48, 49)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 33)
               .addBox(-5.0F, -4.0F, -4.0F, 6.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(12, 45)
               .addBox(-5.0F, 0.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 45)
               .addBox(-5.0F, 0.0F, 2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(52, 29)
               .addBox(-5.0F, 0.0F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 0)
               .addBox(-5.0F, 0.0F, 0.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(50, 5)
               .addBox(-4.0F, 0.0F, -4.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 45)
               .addBox(-1.0F, 0.0F, -4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 29)
               .addBox(-1.0F, 0.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(50, 0)
               .addBox(-4.0F, 0.0F, 3.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 35)
               .addBox(-5.0F, 0.0F, -4.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(16, 49)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 6)
               .addBox(4.0F, 0.0F, -4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 45)
               .addBox(4.0F, 0.0F, 2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 17)
               .addBox(4.0F, 0.0F, -3.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 33)
               .addBox(4.0F, 0.0F, 0.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(16, 45)
               .addBox(4.0F, 0.0F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 5)
               .addBox(1.0F, 0.0F, -4.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(38, 17)
               .addBox(1.0F, 0.0F, 3.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(4.0F, 0.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 25)
               .addBox(-1.0F, -4.0F, -4.0F, 6.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(44, 13).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
