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

public class RoboticArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? RoboticArmorLayers.LeggingsLayer::createBodyLayer : RoboticArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? RoboticArmorLayers.LeggingsLayer::new : RoboticArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(48, 51).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 78)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-7.5F, -10.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 23)
               .addBox(-9.5F, -11.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-8.0F, 2.0F, -8.0F, 16.0F, 7.0F, 16.0F, new CubeDeformation(0.0F))
               .texOffs(48, 0)
               .addBox(-6.0F, 9.0F, -5.0F, 12.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(60, 67)
               .addBox(-7.0F, 1.0F, -4.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(6, 23)
               .addBox(-1.0F, 13.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(42, 30)
               .addBox(-2.0F, 16.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(42, 27)
               .addBox(-6.0F, 16.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 23)
               .addBox(-7.0F, 13.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(80, 73)
               .addBox(-6.0F, 9.0F, -4.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(32, 67)
               .addBox(1.0F, 1.0F, -4.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(80, 51)
               .addBox(1.0F, 9.0F, -4.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(6, 29)
               .addBox(6.0F, 13.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 29)
               .addBox(0.0F, 13.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 46)
               .addBox(1.0F, 16.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(42, 33)
               .addBox(5.0F, 16.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(0, 63)
               .addBox(-4.0F, 7.0F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(80, 13)
               .addBox(-4.0F, 11.0F, -6.0F, 8.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(36, 51)
               .addBox(-3.8F, 11.0F, -6.0F, 8.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(56, 15)
               .addBox(-3.8F, 7.0F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
