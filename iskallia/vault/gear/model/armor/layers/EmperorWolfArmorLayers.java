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

public class EmperorWolfArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? EmperorWolfArmorLayers.LeggingsLayer::createBodyLayer : EmperorWolfArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? EmperorWolfArmorLayers.LeggingsLayer::new : EmperorWolfArmorLayers.MainLayer::new;
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
               .texOffs(22, 26)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(30, 22)
               .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 34)
               .addBox(2.0F, -14.0F, -3.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 0)
               .addBox(-5.0F, -14.0F, -3.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(0, 18)
               .addBox(1.0F, 11.5F, -2.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 4)
               .addBox(-1.0F, 11.5F, -2.25F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(23, 18)
               .addBox(-2.0F, 11.5F, -2.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(48, 73)
               .addBox(-2.5F, 9.0F, -5.0F, 5.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(-4.0F, -4.0F, -8.0F, 8.0F, 13.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(32, 54)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(30, 42)
               .addBox(-6.0F, -4.0F, 3.0F, 12.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-6.5F, -4.5F, 2.75F, 13.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(62, 2)
               .addBox(-5.5F, -2.5F, -6.25F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(58, 56)
               .addBox(1.5F, -2.5F, -6.25F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(54, 34)
               .addBox(-4.0F, 3.0F, 3.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(70, 42)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 34)
               .addBox(-6.0F, -3.5F, -4.0F, 7.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(72, 69)
               .addBox(-6.0F, 5.5F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(38, 22)
               .addBox(-5.0F, 11.5F, 0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(23, 14)
               .addBox(-5.0F, 11.5F, -1.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(70, 19)
               .addBox(3.0F, 5.5F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(3.0F, 11.5F, -1.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(3.0F, 11.5F, 0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 70)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(30, 6)
               .addBox(-1.0F, -3.5F, -4.0F, 7.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(16, 62)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(46, 22)
               .addBox(-4.0F, 6.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(0, 62)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 50)
               .addBox(-4.05F, 6.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
