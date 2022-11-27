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

public class FlamingoArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? FlamingoArmorLayers.LeggingsLayer::createBodyLayer : FlamingoArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? FlamingoArmorLayers.LeggingsLayer::new : FlamingoArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(26, 6).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(1.0F, -5.75F, -0.25F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-7.0F, 1.25F, -0.25F, 10.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, -3.25F, -5.0F, -0.3491F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(32, 44)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 35)
               .addBox(-8.0F, 8.0F, -6.0F, 16.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 28)
               .addBox(-8.0F, 8.0F, 2.0F, 16.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(32, 60)
               .addBox(4.0F, 8.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(58, 12)
               .addBox(-8.0F, 8.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 14).addBox(-2.0F, -1.5F, 0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 6.5F, 5.5F, -0.6109F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(0, 18)
               .addBox(-1.0F, -3.0F, -8.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(34, 22)
               .addBox(-1.0F, -5.0F, -8.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(50, 0)
               .addBox(-2.0F, -5.0F, -4.0F, 4.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 5.0F, -4.0F, 0.48F, -0.0436F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(56, 44)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 14)
               .addBox(-6.0F, 2.0F, -5.0F, 7.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-1.0F, 2.0F, -5.0F, 7.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(16, 55)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r4 = right_leg.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 42).addBox(-4.5F, -0.5F, -5.5F, 5.0F, 2.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 11.5F, -4.5F, 0.0F, 0.48F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(50, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r5 = left_leg.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(29, 31).addBox(-0.25F, -0.5F, -5.75F, 5.0F, 2.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.2F, 11.5F, -4.5F, 0.0F, -0.5236F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
