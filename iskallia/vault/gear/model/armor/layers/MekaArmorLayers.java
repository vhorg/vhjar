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

public class MekaArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? MekaArmorLayers.LeggingsLayer::createBodyLayer : MekaArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? MekaArmorLayers.LeggingsLayer::new : MekaArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
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
               .texOffs(0, 24)
               .addBox(-4.0F, -8.25F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(32, 67)
               .addBox(-2.0F, -7.25F, 5.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(12, 48)
               .addBox(-5.3311F, -4.8166F, 1.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 61)
               .addBox(-5.3311F, -4.8166F, -2.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 37)
               .addBox(-5.3311F, -5.3166F, -1.0F, 11.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 24)
               .addBox(4.9189F, 4.6834F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 40)
               .addBox(-5.8311F, -3.3166F, -5.0F, 1.0F, 9.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(0, 13)
               .addBox(-5.3311F, -4.3166F, -5.0F, 11.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(70, 9)
               .addBox(-5.3311F, 1.6834F, -6.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(52, 16)
               .addBox(-5.3311F, -3.3166F, -6.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 0)
               .addBox(-1.3311F, -3.3166F, -6.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(66, 0)
               .addBox(-1.3311F, -3.3166F, 5.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(74, 14)
               .addBox(-5.3311F, -3.3166F, 5.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(70, 51)
               .addBox(-5.3311F, 1.6834F, 5.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-5.0811F, 5.6834F, -6.0F, 11.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.1689F, -5.9334F, 0.5F, 0.0F, 1.5708F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(50, 22).addBox(5.152F, -2.1297F, -5.0F, 1.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.1689F, -5.9334F, 0.5F, -1.5708F, 1.405F, -1.5708F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(38, 53).addBox(5.7676F, 0.5341F, -5.0F, 1.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.1689F, -5.9334F, 0.5F, 1.5708F, 1.3788F, 1.5708F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(50, 53)
               .addBox(-4.0F, 1.0F, 3.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(68, 46)
               .addBox(-3.5F, 2.0F, -4.0F, 7.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 53)
               .addBox(-2.25F, 6.0F, -3.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(38, 13)
               .addBox(0.25F, 6.0F, -3.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(46, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(18, 40)
               .addBox(-1.0F, 9.0F, -4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(6, 0)
               .addBox(1.5F, 9.0F, -4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 28)
               .addBox(-3.5F, 9.0F, -4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 40).addBox(-2.0F, -2.5F, -1.0F, 3.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.3729F, 7.9218F, 4.0F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(60, 62)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(34, 16)
               .addBox(-6.0F, 4.0F, -4.0F, 5.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(70, 0)
               .addBox(-5.0F, 2.0F, -4.0F, 3.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(12, 40)
               .addBox(-6.25F, 8.25F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r5 = right_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(74, 32).addBox(-0.5F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.0F, 3.0F, 0.0F, 0.0F, 0.0F, 0.1745F)
         );
         PartDefinition cube_r6 = right_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(22, 48).addBox(-2.5F, -2.5F, -4.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, -2.5F, 0.0F, 0.0F, 0.0F, -0.1745F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(24, 24)
               .addBox(5.25F, 8.25F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(60, 37)
               .addBox(2.0F, 2.0F, -4.0F, 3.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(24, 32)
               .addBox(1.0F, 4.0F, -4.0F, 5.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(62, 16)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r7 = left_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(42, 40).addBox(-2.5F, -2.5F, -4.0F, 5.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.5F, -2.5F, 0.0F, 0.0F, 0.0F, 0.1745F)
         );
         PartDefinition cube_r8 = left_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(48, 67).addBox(-1.5F, -4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, 3.0F, 0.0F, 0.0F, 0.0F, -0.1745F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(16, 61)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(70, 3)
               .addBox(-1.0F, 4.0F, -4.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 32)
               .addBox(-1.0F, 4.0F, 3.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r9 = right_leg.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(32, 13).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.5F, 10.0F, 0.0F, 0.0F, 0.0F, 0.1309F)
         );
         PartDefinition cube_r10 = right_leg.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(34, 0).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, 10.0F, 0.0F, 0.0F, 0.0F, -0.1309F)
         );
         PartDefinition cube_r11 = right_leg.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(76, 63).addBox(-2.0F, -3.0F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.0F, 3.5F, -0.1309F, 0.0F, 0.0F)
         );
         PartDefinition cube_r12 = right_leg.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(76, 70).addBox(-2.0F, -3.0F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.0F, -3.5F, 0.1309F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(60, 40)
               .addBox(-0.8F, 4.0F, -4.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(22, 50)
               .addBox(-0.8F, 4.0F, 3.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 59)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r13 = left_leg.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create().texOffs(74, 56).addBox(-2.0F, -3.0F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.2F, 10.0F, 3.5F, -0.1309F, 0.0F, 0.0F)
         );
         PartDefinition cube_r14 = left_leg.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(0, 75).addBox(-2.0F, -3.0F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.2F, 10.0F, -3.5F, 0.1309F, 0.0F, 0.0F)
         );
         PartDefinition cube_r15 = left_leg.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.3F, 10.0F, 0.0F, 0.0F, 0.0F, -0.1309F)
         );
         PartDefinition cube_r16 = left_leg.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create().texOffs(0, 13).addBox(-0.5F, -3.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.45F, 10.0F, 0.0F, 0.0F, 0.0F, 0.1309F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
