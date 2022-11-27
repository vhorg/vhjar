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

public class DankArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? DankArmorLayers.LeggingsLayer::createBodyLayer : DankArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? DankArmorLayers.LeggingsLayer::new : DankArmorLayers.MainLayer::new;
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
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
               .texOffs(0, 16)
               .addBox(-2.0F, 12.0F, 2.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(-2.0F, 10.0F, 2.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(1.0F, 11.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-2.0F, 11.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
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
            CubeListBuilder.create()
               .texOffs(66, 62)
               .addBox(-5.0F, -9.0F, -5.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(16, 66)
               .addBox(3.0F, -9.0F, -5.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(8, 66)
               .addBox(3.0F, -9.0F, 3.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(48, 64)
               .addBox(-5.0F, -9.0F, 3.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(10, 46)
               .addBox(-5.0F, -9.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(40, 44)
               .addBox(3.0F, -9.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(30, 42)
               .addBox(3.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(42, 26)
               .addBox(-5.0F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(64, 58)
               .addBox(-3.0F, -1.0F, 3.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(64, 46)
               .addBox(-3.0F, -9.0F, 3.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(64, 42)
               .addBox(-3.0F, -9.0F, -5.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(32, 64)
               .addBox(-3.0F, -1.0F, -5.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(60, 36)
               .addBox(-3.0F, 10.0F, -5.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(58, 30)
               .addBox(-3.0F, 2.0F, -5.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(56, 12)
               .addBox(-3.0F, 2.0F, 3.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(56, 4)
               .addBox(-3.0F, 10.0F, 3.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(42, 18)
               .addBox(-5.0F, 10.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(20, 40)
               .addBox(3.0F, 10.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(40, 8)
               .addBox(-5.0F, 2.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(40, 0)
               .addBox(3.0F, 2.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(24, 62)
               .addBox(-5.0F, 2.0F, 3.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(58, 60)
               .addBox(3.0F, 2.0F, 3.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 60)
               .addBox(3.0F, 2.0F, -5.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(58, 48)
               .addBox(-5.0F, 2.0F, -5.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(50, 40)
               .addBox(-8.0F, 3.0F, -5.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(50, 8)
               .addBox(-8.0F, -5.0F, -5.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(50, 0)
               .addBox(-8.0F, -5.0F, 3.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(46, 34)
               .addBox(-8.0F, 3.0F, 3.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(32, 24)
               .addBox(-10.0F, 3.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(32, 16)
               .addBox(-2.0F, 3.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(16, 30)
               .addBox(-10.0F, -5.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(34, 50)
               .addBox(-10.0F, -5.0F, 3.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(22, 22)
               .addBox(-2.0F, -5.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(26, 50)
               .addBox(-2.0F, -5.0F, 3.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 48)
               .addBox(-2.0F, -5.0F, -5.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(16, 0)
               .addBox(-10.0F, -5.0F, -5.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(52, 26)
               .addBox(2.0F, 3.0F, -5.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(52, 20)
               .addBox(2.0F, -5.0F, -5.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(52, 16)
               .addBox(2.0F, -5.0F, 3.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(50, 44)
               .addBox(2.0F, 3.0F, 3.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 40)
               .addBox(0.0F, 3.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(10, 38)
               .addBox(8.0F, 3.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(36, 34)
               .addBox(0.0F, -5.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(16, 54)
               .addBox(0.0F, -5.0F, 3.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(26, 32)
               .addBox(8.0F, -5.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(8, 54)
               .addBox(8.0F, -5.0F, 3.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(50, 52)
               .addBox(8.0F, -5.0F, -5.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(42, 52)
               .addBox(0.0F, -5.0F, -5.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 24).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(12, 12).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
