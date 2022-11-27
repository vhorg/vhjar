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

public class WitherArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? WitherArmorLayers.LeggingsLayer::createBodyLayer : WitherArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? WitherArmorLayers.LeggingsLayer::new : WitherArmorLayers.MainLayer::new;
   }

   @OnlyIn(Dist.CLIENT)
   public static class LeggingsLayer extends ArmorLayers.LeggingsLayer {
      public LeggingsLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(17, 17).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(17, 17).mirror().addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(33, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition skull_r1 = left_arm.addOrReplaceChild(
            "skull_r1",
            CubeListBuilder.create().texOffs(25, 17).addBox(-2.0F, -4.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.8F)),
            PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.3491F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(33, 0).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition skull_r2 = right_arm.addOrReplaceChild(
            "skull_r2",
            CubeListBuilder.create().texOffs(25, 17).mirror().addBox(-4.0F, -4.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.8F)).mirror(false),
            PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.3491F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 17)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
               .texOffs(38, 30)
               .addBox(-5.0F, 1.0F, -3.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.02F))
               .texOffs(38, 30)
               .addBox(-5.0F, 5.0F, -3.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.02F))
               .texOffs(38, 30)
               .addBox(3.0F, 5.0F, -3.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.02F))
               .texOffs(38, 30)
               .addBox(3.0F, 1.0F, -3.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.02F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition external_r1 = body.addOrReplaceChild(
            "external_r1",
            CubeListBuilder.create()
               .texOffs(38, 30)
               .addBox(-5.0F, -1.0F, -3.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.02F))
               .texOffs(38, 30)
               .addBox(3.0F, -1.0F, -3.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.02F)),
            PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, -0.2618F, 0.0F, 0.0F)
         );
         PartDefinition heart2_r1 = body.addOrReplaceChild(
            "heart2_r1",
            CubeListBuilder.create().texOffs(38, 38).addBox(-1.0F, -1.0F, -3.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.4F)),
            PartPose.offsetAndRotation(1.0F, 5.0F, 0.0F, 0.0F, 0.0F, 0.48F)
         );
         PartDefinition heart_r1 = body.addOrReplaceChild(
            "heart_r1",
            CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.2F)),
            PartPose.offsetAndRotation(-1.0F, 6.0F, -2.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 34).addBox(-1.9F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.51F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 34).mirror().addBox(-2.1F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.51F)).mirror(false),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
