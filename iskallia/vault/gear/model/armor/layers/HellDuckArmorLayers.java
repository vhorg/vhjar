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

public class HellDuckArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? HellDuckArmorLayers.LeggingsLayer::createBodyLayer : HellDuckArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? HellDuckArmorLayers.LeggingsLayer::new : HellDuckArmorLayers.MainLayer::new;
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
               .addBox(-2.0F, 9.0F, -3.5F, 4.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(30, 28).addBox(1.0F, -1.0F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0F, 11.0F, 0.0F, 0.0F, 0.0F, 0.9163F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(1.0F, -3.0F, 2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(1.0F, -3.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-2.0F, -1.0F, -3.0F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0F, 11.0F, 0.0F, 0.0F, 0.0F, 0.48F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(34, 8).addBox(-2.0F, 0.5F, -2.5F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.3172F, 13.2905F, 0.0F, 0.0F, 0.0F, -0.9163F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(15, 16)
               .addBox(-2.0F, -3.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(3, 18)
               .addBox(-2.0F, -3.0F, 2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 28)
               .addBox(-2.0F, -1.0F, -3.0F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0F, 11.0F, 0.0F, 0.0F, 0.0F, -0.48F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(22, 12).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(5.0F, -5.0F, -2.5F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(36, 0)
               .addBox(-7.0F, 5.0F, -2.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(56, 0)
               .addBox(5.0F, 5.0F, -2.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(52, 42)
               .addBox(5.0F, 0.0F, -2.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(-7.0F, -5.0F, -2.5F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(52, 50)
               .addBox(-8.0F, 0.0F, -2.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -10.0F, -0.75F, 0.7418F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(24, 5)
               .addBox(-2.0F, -1.5F, 0.8333F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(-2.0F, 0.5F, -1.1667F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(44, 27)
               .addBox(-3.0F, -0.5F, -2.1667F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -2.5F, -6.8333F, 0.3491F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 28)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(40, 18)
               .addBox(-5.0F, 1.0F, -5.0F, 10.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(52, 33).addBox(-3.0F, -4.0F, -0.5F, 6.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 8.0F, -3.5F, 0.2182F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(44, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r4 = right_arm.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(24, 8)
               .addBox(-2.5F, 1.5F, -4.0F, 6.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(20, 20)
               .addBox(-2.5F, -2.5F, -4.0F, 6.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.2618F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(0, 44).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r5 = left_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(0, 16)
               .addBox(-4.0F, -2.5F, -4.0F, 6.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(24, 32)
               .addBox(-4.0F, 1.5F, -4.0F, 6.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(36, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r6 = right_leg.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(16, 58).addBox(-3.1642F, -3.4142F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.8F, 8.0F, -3.5F, 0.2194F, 0.2143F, -0.7617F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(20, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r7 = left_leg.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(26, 58).addBox(-0.5858F, -0.5858F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, 8.0F, -3.5F, 0.2194F, 0.2143F, -0.7617F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
