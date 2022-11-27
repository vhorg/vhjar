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

public class AngelArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? AngelArmorLayers.LeggingsLayer::createBodyLayer : AngelArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? AngelArmorLayers.LeggingsLayer::new : AngelArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(32, 22).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(15, 35).addBox(1.0F, -4.0F, -3.0F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.2618F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(27, 35).addBox(-1.0F, -4.0F, -3.0F, 0.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -8.0F, 0.0F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(30, 19)
               .addBox(-3.0F, -0.5F, 3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(15, 49)
               .addBox(-3.0F, -0.5F, -4.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(72, 73)
               .addBox(3.0F, -0.5F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(74, 40)
               .addBox(-4.0F, -0.5F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -12.5F, 0.0F, 0.1745F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 0).addBox(-0.5429F, -5.0F, -1.3107F, 5.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0184F, -4.0F, -5.5F, 0.1886F, -0.3864F, -0.0718F)
         );
         PartDefinition cube_r5 = head.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(32, 74).addBox(-4.4571F, -5.0F, -1.3107F, 5.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0184F, -4.0F, -5.5F, 0.1886F, 0.3864F, 0.0718F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(22, 58)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-6.0F, -2.0F, -7.0F, 12.0F, 6.0F, 13.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(29, 38).addBox(-1.0F, -4.5F, -5.5F, 2.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(15.1809F, -4.3373F, 3.9788F, -0.6485F, -0.3189F, 0.2333F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(0, 41).addBox(-1.0F, -4.5F, -5.5F, 2.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-15.1809F, -4.3373F, 3.9788F, -0.6485F, 0.3189F, -0.2333F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(44, 38)
               .addBox(-18.0F, -4.5F, -1.0F, 13.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(56, 13)
               .addBox(-3.0F, -4.5F, -1.0F, 13.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0F, -1.7769F, 7.6355F, -0.6109F, 0.0F, 0.0F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(71, 49).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 7.0F, 4.0F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r10 = body.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(69, 0).addBox(-3.0F, -3.0F, -0.5F, 7.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.35F, 5.0F, -5.5F, 0.6139F, -0.5198F, -0.3368F)
         );
         PartDefinition cube_r11 = body.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(16, 74).addBox(-4.0F, -3.0F, -0.5F, 7.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.9913F, 5.0F, -5.5F, 0.6139F, 0.5198F, 0.3368F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(72, 24).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r12 = right_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create()
               .texOffs(0, 19)
               .addBox(-6.0F, 4.5F, -5.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(47, 50)
               .addBox(-4.0F, -0.5F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -2.5F, 0.0F, 0.0F, 0.0F, -0.7418F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(62, 63).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r13 = left_arm.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create()
               .texOffs(0, 30)
               .addBox(-4.0F, 4.5F, -5.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(37, 0)
               .addBox(-4.0F, -0.5F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, -2.5F, 0.0F, 0.0F, 0.0F, 0.7418F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(46, 63).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r14 = right_leg.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(0, 16).addBox(0.75F, -3.0F, -1.5F, 0.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.05F, 8.0F, 0.5F, 0.0F, 0.0F, 0.2618F)
         );
         PartDefinition cube_r15 = right_leg.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create().texOffs(0, 27).addBox(-0.75F, -3.0F, -1.5F, 0.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.2F, 8.0F, 0.5F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 61).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r16 = left_leg.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create().texOffs(30, 18).addBox(-0.75F, -3.0F, -1.5F, 0.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0F, 8.0F, 0.5F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition cube_r17 = left_leg.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create().texOffs(0, 38).addBox(1.0F, -3.0F, -1.5F, 0.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0F, 8.0F, 0.5F, 0.0F, 0.0F, 0.2618F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
