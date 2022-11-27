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

public class SquireArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SquireArmorLayers.LeggingsLayer::createBodyLayer : SquireArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SquireArmorLayers.LeggingsLayer::new : SquireArmorLayers.MainLayer::new;
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
               .texOffs(0, 15)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 0)
               .addBox(-1.0F, -11.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 4)
               .addBox(-0.5F, -15.0F, -1.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(35, 6).addBox(-2.0F, -0.5F, -4.5F, 5.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.8321F, -9.2935F, -0.35F, 0.0F, 0.0F, 0.2618F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 44).addBox(-2.5F, -2.0F, -4.85F, 5.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.8675F, -7.7152F, 0.0F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(30, 36)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-6.0F, 1.0F, -5.0F, 12.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(34, 0)
               .addBox(-4.0F, 6.0F, -4.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 17)
               .addBox(-4.0F, 6.0F, 3.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(54, 9)
               .addBox(-3.0F, 9.0F, -3.5F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(63, 18)
               .addBox(-3.0F, 9.0F, 2.5F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(40, 56)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 31)
               .addBox(-6.0F, -5.0F, -4.0F, 7.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(47, 45)
               .addBox(-5.0F, -1.0F, -3.5F, 5.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r3 = right_arm.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(63, 9).addBox(-1.7F, -0.5F, -3.9F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.2495F, -5.2887F, 0.0F, 0.0F, 0.0F, -0.4363F)
         );
         PartDefinition cube_r4 = right_arm.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(9, 64).addBox(-1.5F, -0.5F, -3.9F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.5F, -5.5F, 0.0F, 0.0F, 0.0F, 0.4363F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(0, 55)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(24, 23)
               .addBox(-1.0F, -5.0F, -4.0F, 7.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(46, 17)
               .addBox(0.0F, -1.0F, -3.5F, 5.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r5 = left_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(56, 56).addBox(1.5F, -1.5F, -3.9F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.6F, -5.85F, 0.0F, -3.1416F, 0.0F, 2.7053F)
         );
         PartDefinition cube_r6 = left_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(54, 0).addBox(-3.7F, -1.5F, -3.9F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.9727F, -5.2161F, 0.0F, 3.1416F, 0.0F, -2.7053F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(54, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(24, 52).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
