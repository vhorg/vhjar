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

public class PlatemailArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? PlatemailArmorLayers.LeggingsLayer::createBodyLayer : PlatemailArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? PlatemailArmorLayers.LeggingsLayer::new : PlatemailArmorLayers.MainLayer::new;
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
               .texOffs(19, 1)
               .addBox(-3.25F, -2.0F, -3.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-3.25F, 4.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(1.2F, 4.0F, -2.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(1.2F, -2.0F, -3.0F, 2.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .mirror()
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
               .mirror(false),
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
               .texOffs(0, 0)
               .addBox(-1.0F, -10.75F, -5.75F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 29)
               .addBox(-1.0F, -10.75F, -4.75F, 2.0F, 1.0F, 11.0F, new CubeDeformation(0.0F))
               .texOffs(20, 31)
               .addBox(-0.4F, -11.75F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 31)
               .addBox(-0.4F, -11.75F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 31)
               .addBox(-0.4F, -11.75F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(15, 26)
               .addBox(-5.9F, -12.25F, -3.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(15, 26)
               .addBox(5.1F, -12.25F, -3.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(21, 14)
               .addBox(-1.0F, -9.75F, 5.25F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(29, 3).addBox(-2.0F, -1.75F, -0.5F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, -8.0F, -5.5F, 0.0F, 0.0F, 1.2654F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(29, 3).addBox(-1.75F, -2.0F, -0.5F, 1.0F, 1.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, -8.0F, -5.5F, 0.0F, 0.0F, 0.3491F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 14).addBox(-0.5F, -4.25F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.6F, -8.0F, 2.0F, 0.0F, 0.0F, 0.5236F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 14).addBox(-0.5F, -4.25F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.4F, -8.0F, 2.0F, 0.0F, 0.0F, -0.5672F)
         );
         PartDefinition helmet2 = head.addOrReplaceChild(
            "helmet2",
            CubeListBuilder.create()
               .texOffs(28, 15)
               .addBox(-5.5F, -9.75F, 4.5F, 11.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(48, 25)
               .addBox(-4.5F, -9.75F, -5.5F, 9.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 0)
               .addBox(-4.5F, -4.75F, -5.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 0)
               .addBox(-5.5F, -1.75F, -4.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 4)
               .addBox(-5.5F, -1.75F, -3.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 4)
               .addBox(4.5F, -1.75F, -3.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 0)
               .addBox(4.5F, -1.75F, -4.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 34)
               .addBox(-5.5F, -1.75F, -5.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 34)
               .addBox(1.5F, -1.75F, -5.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 0)
               .addBox(3.5F, -4.75F, -5.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition Helmet6_r1 = helmet2.addOrReplaceChild(
            "Helmet6_r1",
            CubeListBuilder.create().texOffs(43, 43).addBox(-4.5F, -4.5F, -9.75F, 9.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F)
         );
         PartDefinition Helmet4_r1 = helmet2.addOrReplaceChild(
            "Helmet4_r1",
            CubeListBuilder.create()
               .texOffs(42, 0)
               .addBox(-4.5F, -9.75F, -5.5F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 0)
               .addBox(-4.5F, -9.75F, 4.5F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(24, 24)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-6.0F, -2.0F, -4.0F, 12.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 28)
               .mirror()
               .addBox(1.0F, -1.0F, -5.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(0, 28)
               .addBox(-5.0F, -1.0F, -5.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(15, 32)
               .addBox(-1.0F, -1.0F, -5.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 32)
               .addBox(-6.0F, 4.0F, -4.0F, 4.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(40, 32)
               .mirror()
               .addBox(2.0F, 4.0F, -4.0F, 4.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(20, 35)
               .addBox(-2.0F, 4.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 35)
               .addBox(-2.0F, 4.0F, 3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(19, 26)
               .addBox(-1.0F, 4.0F, -4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(19, 26)
               .addBox(-1.0F, 4.0F, 3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 35)
               .addBox(1.0F, 4.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 35)
               .addBox(1.0F, 4.0F, 3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition Innerchestplate = body.addOrReplaceChild(
            "Innerchestplate",
            CubeListBuilder.create()
               .texOffs(0, 26)
               .addBox(-3.0F, 11.75F, -3.25F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 52)
               .addBox(-4.0F, 5.0F, -3.5F, 8.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 52)
               .addBox(-4.0F, 5.0F, 2.5F, 8.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 26)
               .addBox(-3.0F, 11.75F, 2.25F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(0, 41)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 14)
               .mirror()
               .addBox(-7.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(19, 40)
               .mirror()
               .addBox(-7.0F, 3.0F, -3.5F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(46, 18)
               .mirror()
               .addBox(-6.0F, -5.0F, -3.0F, 4.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(0, 41)
               .mirror()
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .mirror(false)
               .texOffs(46, 18)
               .addBox(2.0F, -5.0F, -3.0F, 4.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(0.0F, -4.25F, -3.5F, 7.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(19, 40)
               .addBox(1.75F, 3.0F, -3.5F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(52, 11).addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(52, 11).mirror().addBox(-2.0F, 9.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
