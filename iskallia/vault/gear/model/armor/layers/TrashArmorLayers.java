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

public class TrashArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? TrashArmorLayers.LeggingsLayer::createBodyLayer : TrashArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? TrashArmorLayers.LeggingsLayer::new : TrashArmorLayers.MainLayer::new;
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
               .texOffs(12, 16)
               .addBox(-3.0F, 10.0F, 2.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(19, 19)
               .addBox(-1.0F, 13.0F, 2.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-2.5F, 13.0F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
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
               .texOffs(0, 15)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(36, 49)
               .addBox(-4.0F, -14.0F, -6.0F, 8.0F, 15.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(18, 48)
               .addBox(-4.0F, -14.0F, 5.0F, 8.0F, 15.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 15)
               .addBox(-5.0F, -14.0F, 4.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 5)
               .addBox(4.0F, -14.0F, 4.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 5)
               .addBox(4.0F, -14.0F, -5.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 5)
               .addBox(-5.0F, -14.0F, -5.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-4.0F, -15.0F, -7.0F, 8.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
               .texOffs(24, 17)
               .addBox(-4.0F, -14.0F, -7.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 15)
               .addBox(-4.0F, -14.0F, 6.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 36)
               .addBox(-5.0F, -15.0F, -6.0F, 1.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(30, 0)
               .addBox(4.0F, -15.0F, -6.0F, 1.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-0.5F, -17.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(4, 15)
               .addBox(-0.5F, -16.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 11)
               .addBox(-0.5F, -16.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(50, 29)
               .addBox(-6.0F, -15.0F, -5.0F, 1.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(4, 11)
               .addBox(-6.0F, -14.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(10, 3)
               .addBox(-5.0F, -14.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(6, 2)
               .addBox(4.0F, -14.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(4.0F, -14.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(9, 1)
               .addBox(-6.0F, -14.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(6, 0)
               .addBox(5.0F, -14.0F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 2)
               .addBox(5.0F, -14.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 11)
               .addBox(-5.0F, -14.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 0)
               .addBox(5.0F, -15.0F, -5.0F, 1.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(62, 40)
               .addBox(-7.0F, -15.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(62, 30)
               .addBox(6.0F, -15.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(62, 21)
               .addBox(6.0F, -14.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(56, 0)
               .addBox(-7.0F, -14.0F, -4.0F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(0, 48)
               .addBox(-4.0F, -14.0F, -6.0F, 8.0F, 15.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(48, 13)
               .addBox(-4.0F, -14.0F, 5.0F, 8.0F, 15.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(24, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(32, 65)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 31)
               .addBox(-5.0F, -5.0F, -4.0F, 4.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(16, 64).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(0, 64)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(24, 23)
               .addBox(-4.0F, 5.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(24, 23)
               .addBox(-3.8F, 5.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(54, 49)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
