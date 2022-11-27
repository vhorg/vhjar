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

public class FurnaceArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? FurnaceArmorLayers.LeggingsLayer::createBodyLayer : FurnaceArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? FurnaceArmorLayers.LeggingsLayer::new : FurnaceArmorLayers.MainLayer::new;
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
               .texOffs(26, 6)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 14)
               .addBox(5.0F, -5.0F, -5.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-6.0F, -5.0F, -5.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(26, 0).addBox(-9.0438F, -3.3F, -4.2832F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5161F, 0.0F, 0.1884F, 0.0F, -0.6545F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(57, 48).addBox(-1.3533F, -2.05F, -1.8886F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.1275F, -5.95F, -6.3931F, -0.5807F, -0.5704F, 0.3405F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(40, 0).addBox(1.1637F, -3.3F, -1.4569F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, 0.0F, -3.0F, 0.0F, 0.6545F, 0.0F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(72, 38).addBox(-4.7675F, -1.95F, -1.7312F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.1275F, -5.95F, -6.3931F, -0.5807F, 0.5704F, -0.3405F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 28)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(58, 16)
               .addBox(-4.0F, 1.0F, 3.0F, 8.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(62, 30)
               .addBox(-3.0F, 2.0F, 5.0F, 6.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(27, 22)
               .addBox(-5.5F, 11.0F, -3.5F, 11.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(0, 28)
               .addBox(4.0F, -0.5F, -2.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 28)
               .addBox(4.0F, -3.5F, -2.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 30)
               .addBox(-5.0F, -2.0F, -2.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 31)
               .addBox(-4.0F, -4.0F, -2.5F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(48, 38)
               .addBox(-5.0F, -5.0F, -1.5F, 10.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 6.2822F, -2.9637F, 0.1745F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(60, 62).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r6 = right_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(9, 68)
               .addBox(-7.0F, -1.0F, -3.5F, 2.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(0, 44)
               .addBox(-3.0F, 1.0F, -3.5F, 2.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(46, 51)
               .addBox(-5.0F, -1.0F, -3.5F, 2.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F)
         );
         PartDefinition cube_r7 = right_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create()
               .texOffs(16, 40)
               .addBox(-7.0F, 2.0F, -4.0F, 5.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(-8.0F, -4.0F, -4.0F, 9.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6545F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(32, 62).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r8 = left_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(24, 31)
               .addBox(0.25F, -0.75F, -4.0F, 5.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-2.75F, -6.75F, -4.0F, 9.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.75F, 0.75F, 0.0F, 0.0F, 0.0F, -0.6545F)
         );
         PartDefinition cube_r9 = left_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create()
               .texOffs(35, 42)
               .addBox(-3.0F, -2.5F, -3.5F, 2.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(64, 48)
               .addBox(1.0F, -3.5F, -3.5F, 2.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(18, 51)
               .addBox(-1.0F, -3.5F, -3.5F, 2.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0F, 1.5F, 0.0F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 59).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(58, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
