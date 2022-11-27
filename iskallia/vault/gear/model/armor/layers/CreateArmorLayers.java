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

public class CreateArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? CreateArmorLayers.LeggingsLayer::createBodyLayer : CreateArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? CreateArmorLayers.LeggingsLayer::new : CreateArmorLayers.MainLayer::new;
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
               .texOffs(20, 0)
               .addBox(4.5F, 10.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(4.5F, 11.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 16)
               .addBox(4.5F, 7.0F, -2.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(4.5F, 9.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(4.0F, 9.0F, 0.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
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
               .texOffs(56, 39)
               .addBox(-5.0F, -6.0F, -6.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 19)
               .addBox(-6.0F, -7.0F, -5.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 34)
               .addBox(1.0F, -6.0F, -6.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(1.5F, -5.5F, -7.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 29)
               .addBox(-2.0F, -10.0F, -5.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 20)
               .addBox(-1.0F, -6.0F, -5.75F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(32, 29)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(14, 55)
               .addBox(-3.0F, 4.0F, -4.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(58, 2)
               .addBox(-2.0F, 8.0F, -4.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(58, 0)
               .addBox(-2.0F, 3.0F, -4.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 39)
               .addBox(-0.5F, 5.5F, -4.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 28)
               .addBox(-0.5F, 4.0F, -4.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-4.0F, 5.0F, 3.0F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 17)
               .addBox(-3.0F, 9.0F, 3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 5)
               .addBox(-3.0F, 4.0F, 3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(50, 13)
               .addBox(-2.0F, 10.0F, 3.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(37, 18)
               .addBox(-2.0F, 3.0F, 3.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 39)
               .addBox(-1.5F, 2.0F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 22)
               .addBox(-1.5F, 11.0F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 39)
               .addBox(0.5F, 2.0F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 22)
               .addBox(0.5F, 11.0F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 34)
               .addBox(4.0F, 5.5F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(3, 33)
               .addBox(4.0F, 7.25F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 32)
               .addBox(-5.0F, 7.25F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 32)
               .addBox(-5.0F, 5.5F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 1)
               .addBox(-0.5F, 6.5F, 3.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(24, 34)
               .addBox(0.55F, 9.05F, -2.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(3, 31)
               .addBox(4.65F, 4.75F, -2.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 32)
               .addBox(-3.6F, 5.0F, -2.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 34)
               .addBox(0.65F, 0.75F, -2.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0F, 2.5F, 5.25F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 28).addBox(0.5F, -1.25F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 7.5F, -3.75F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(48, 45)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(24, 29)
               .addBox(-6.0F, -3.0F, 1.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(37, 6)
               .addBox(-6.0F, -8.0F, 1.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 0)
               .addBox(-7.0F, -7.0F, 1.0F, 7.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(50, 24)
               .addBox(-5.0F, -7.0F, -3.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(50, 26)
               .addBox(-5.0F, -2.0F, -3.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 55)
               .addBox(-6.0F, -6.0F, -3.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(46, 17)
               .addBox(-7.0F, -5.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 45)
               .addBox(-4.5F, -8.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 41)
               .addBox(-2.75F, -9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 41)
               .addBox(-5.25F, -9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 41)
               .addBox(-8.0F, -6.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 39)
               .addBox(-8.0F, -4.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 41)
               .addBox(-2.5F, -8.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 45)
               .addBox(-7.0F, -3.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r3 = right_arm.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(24, 20)
               .addBox(-4.5F, 1.25F, -4.0F, 9.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 28)
               .addBox(-3.5F, -1.75F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, -1.5F, 0.0F, 0.0F, 0.0F, -0.4363F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(32, 45).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r4 = left_arm.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(24, 8)
               .addBox(-4.25F, 0.0F, -4.0F, 9.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(-4.25F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.5782F, -1.1195F, 0.0F, 0.0F, 0.0F, 0.4363F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(16, 39)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 4)
               .addBox(-4.0F, 11.0F, -2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(50, 17)
               .addBox(-5.0F, 9.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(3.2F, 11.0F, -2.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(50, 6)
               .addBox(3.2F, 9.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 39)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
