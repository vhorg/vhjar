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

public class StressFlowerArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return StressFlowerArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return StressFlowerArmorLayers.MainLayer::new;
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
               .texOffs(42, 16)
               .addBox(-5.0F, -6.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 40)
               .addBox(-6.0F, -4.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 31)
               .addBox(3.0F, -5.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(39, 39)
               .addBox(5.0F, -5.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(38, 35)
               .addBox(5.0F, -7.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 18)
               .addBox(5.0F, -4.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 29)
               .addBox(-1.0F, -7.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 27)
               .addBox(-3.0F, -4.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 33)
               .addBox(-6.0F, -6.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(39, 41)
               .addBox(0.0F, -7.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 40)
               .addBox(4.0F, -6.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 37)
               .addBox(-3.0F, -5.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 2).addBox(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.0F, -2.0F, -2.5F, 0.0F, 0.0F, 0.5236F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 30).addBox(0.0F, -1.5F, -1.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.0F, -3.5F, -1.0F, 0.0F, -0.3054F, 0.0F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.0F, -5.0F, -2.5F, 0.0F, 0.0F, -0.3491F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 14).addBox(0.0F, -1.5F, -1.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.0F, -3.5F, -4.0F, 0.0F, 0.2618F, 0.0F)
         );
         PartDefinition cube_r5 = head.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(20, 15).addBox(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.0F, -4.0F, 2.5F, 0.0F, 0.0F, 0.4363F)
         );
         PartDefinition cube_r6 = head.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(16, 30).addBox(0.0F, -1.5F, -1.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.0F, -5.5F, 4.0F, 0.0F, -0.2618F, 0.0F)
         );
         PartDefinition cube_r7 = head.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(20, 13).addBox(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.0F, -7.0F, 2.5F, 0.0F, 0.0F, 2.8798F)
         );
         PartDefinition cube_r8 = head.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(12, 30).addBox(0.0F, -1.5F, -1.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.0F, -5.5F, 1.0F, 0.0F, 0.2182F, 0.0F)
         );
         PartDefinition cube_r9 = head.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(24, 0).addBox(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -5.0F, 0.5F, 0.0F, 0.0F, -0.2182F)
         );
         PartDefinition cube_r10 = head.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(24, 2).addBox(0.0F, -1.0F, -1.5F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -8.0F, 0.5F, 0.0F, 0.0F, 0.2618F)
         );
         PartDefinition cube_r11 = head.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(28, 30).addBox(0.0F, -1.5F, -1.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -6.5F, 2.0F, 0.0F, 0.1745F, 0.0F)
         );
         PartDefinition cube_r12 = head.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(32, 30).addBox(0.0F, -1.5F, -1.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -6.5F, -1.0F, 0.0F, -0.4363F, 0.0F)
         );
         PartDefinition cube_r13 = head.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create().texOffs(0, 6).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -3.5F, 2.5F, 0.0F, -0.5672F, 0.0F)
         );
         PartDefinition cube_r14 = head.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(4, 1).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -4.5F, 3.5F, 0.0F, 0.0F, 0.4363F)
         );
         PartDefinition cube_r15 = head.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create().texOffs(2, 1).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -3.5F, 4.5F, 0.0F, 0.2182F, 0.0F)
         );
         PartDefinition cube_r16 = head.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create().texOffs(0, 1).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -2.5F, 3.5F, 0.0F, 0.0F, -0.3054F)
         );
         PartDefinition cube_r17 = head.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create().texOffs(6, 0).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -3.5F, -3.5F, 0.0F, 0.0F, -0.3491F)
         );
         PartDefinition cube_r18 = head.addOrReplaceChild(
            "cube_r18",
            CubeListBuilder.create().texOffs(6, 1).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -4.5F, -2.5F, 0.0F, 0.4363F, 0.0F)
         );
         PartDefinition cube_r19 = head.addOrReplaceChild(
            "cube_r19",
            CubeListBuilder.create().texOffs(2, 6).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -4.5F, -4.5F, 0.0F, -0.3927F, 0.0F)
         );
         PartDefinition cube_r20 = head.addOrReplaceChild(
            "cube_r20",
            CubeListBuilder.create().texOffs(6, 2).addBox(0.0F, -0.5F, -0.5F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0F, -5.5F, -3.5F, 0.0F, 0.0F, 0.3491F)
         );
         PartDefinition cube_r21 = head.addOrReplaceChild(
            "cube_r21",
            CubeListBuilder.create().texOffs(32, 37).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, -6.5F, -6.0F, 0.0F, 0.3054F, 0.0F)
         );
         PartDefinition cube_r22 = head.addOrReplaceChild(
            "cube_r22",
            CubeListBuilder.create().texOffs(24, 0).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, -5.0F, -6.0F, -0.1309F, 0.0F, 0.0F)
         );
         PartDefinition cube_r23 = head.addOrReplaceChild(
            "cube_r23",
            CubeListBuilder.create().texOffs(36, 32).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.0F, -6.5F, -6.0F, 0.0F, -0.2618F, 0.0F)
         );
         PartDefinition cube_r24 = head.addOrReplaceChild(
            "cube_r24",
            CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, -8.0F, -6.0F, 0.2618F, 0.0F, 0.0F)
         );
         PartDefinition cube_r25 = head.addOrReplaceChild(
            "cube_r25",
            CubeListBuilder.create().texOffs(6, 6).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, -5.5F, -6.0F, 0.0F, 0.3927F, 0.0F)
         );
         PartDefinition cube_r26 = head.addOrReplaceChild(
            "cube_r26",
            CubeListBuilder.create().texOffs(6, 5).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, -6.5F, -6.0F, 0.4363F, 0.0F, 0.0F)
         );
         PartDefinition cube_r27 = head.addOrReplaceChild(
            "cube_r27",
            CubeListBuilder.create().texOffs(6, 4).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.5F, -5.5F, -6.0F, 0.0F, -0.3491F, 0.0F)
         );
         PartDefinition cube_r28 = head.addOrReplaceChild(
            "cube_r28",
            CubeListBuilder.create().texOffs(6, 0).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, -4.5F, -6.0F, -0.3491F, 0.0F, 0.0F)
         );
         PartDefinition cube_r29 = head.addOrReplaceChild(
            "cube_r29",
            CubeListBuilder.create().texOffs(2, 19).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, -4.5F, -6.0F, 0.0F, -0.5236F, 0.0F)
         );
         PartDefinition cube_r30 = head.addOrReplaceChild(
            "cube_r30",
            CubeListBuilder.create().texOffs(0, 19).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, -5.5F, -6.0F, 0.4363F, 0.0F, 0.0F)
         );
         PartDefinition cube_r31 = head.addOrReplaceChild(
            "cube_r31",
            CubeListBuilder.create().texOffs(6, 7).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, -3.5F, -6.0F, -0.48F, 0.0F, 0.0F)
         );
         PartDefinition cube_r32 = head.addOrReplaceChild(
            "cube_r32",
            CubeListBuilder.create().texOffs(4, 7).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.5F, -4.5F, -6.0F, 0.0F, 0.2618F, 0.0F)
         );
         PartDefinition cube_r33 = head.addOrReplaceChild(
            "cube_r33",
            CubeListBuilder.create().texOffs(26, 7).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, -5.5F, -6.0F, 0.0F, -0.48F, 0.0F)
         );
         PartDefinition cube_r34 = head.addOrReplaceChild(
            "cube_r34",
            CubeListBuilder.create().texOffs(26, 2).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, -4.5F, -6.0F, -0.4363F, 0.0F, 0.0F)
         );
         PartDefinition cube_r35 = head.addOrReplaceChild(
            "cube_r35",
            CubeListBuilder.create().texOffs(24, 7).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, -5.5F, -6.0F, 0.0F, 0.48F, 0.0F)
         );
         PartDefinition cube_r36 = head.addOrReplaceChild(
            "cube_r36",
            CubeListBuilder.create().texOffs(24, 2).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, -6.5F, -6.0F, 0.5236F, 0.0F, 0.0F)
         );
         PartDefinition cube_r37 = head.addOrReplaceChild(
            "cube_r37",
            CubeListBuilder.create().texOffs(26, 19).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, -7.5F, 6.0F, -0.2182F, 0.0F, 0.0F)
         );
         PartDefinition cube_r38 = head.addOrReplaceChild(
            "cube_r38",
            CubeListBuilder.create().texOffs(26, 18).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.5F, -6.5F, 6.0F, 0.0F, 0.1745F, 0.0F)
         );
         PartDefinition cube_r39 = head.addOrReplaceChild(
            "cube_r39",
            CubeListBuilder.create().texOffs(26, 17).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, -5.5F, 6.0F, 0.2618F, 0.0F, 0.0F)
         );
         PartDefinition cube_r40 = head.addOrReplaceChild(
            "cube_r40",
            CubeListBuilder.create().texOffs(26, 16).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, -6.5F, 6.0F, 0.0F, -0.2182F, 0.0F)
         );
         PartDefinition cube_r41 = head.addOrReplaceChild(
            "cube_r41",
            CubeListBuilder.create().texOffs(30, 2).addBox(-1.5F, -1.0F, 0.5F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, -2.0F, 5.5F, 0.2618F, 0.0F, 0.0F)
         );
         PartDefinition cube_r42 = head.addOrReplaceChild(
            "cube_r42",
            CubeListBuilder.create().texOffs(40, 18).addBox(-1.0F, -1.5F, 0.5F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.0F, -3.5F, 5.5F, 0.0F, -0.2618F, 0.0F)
         );
         PartDefinition cube_r43 = head.addOrReplaceChild(
            "cube_r43",
            CubeListBuilder.create().texOffs(36, 37).addBox(-1.0F, -1.5F, 0.5F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0F, -3.5F, 5.5F, 0.0F, 0.1745F, 0.0F)
         );
         PartDefinition cube_r44 = head.addOrReplaceChild(
            "cube_r44",
            CubeListBuilder.create().texOffs(30, 0).addBox(-1.5F, -1.0F, 0.5F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, -5.0F, 5.5F, -0.2182F, 0.0F, 0.0F)
         );
         PartDefinition cube_r45 = head.addOrReplaceChild(
            "cube_r45",
            CubeListBuilder.create().texOffs(40, 21).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, -4.5F, 6.0F, 0.0F, 0.2618F, 0.0F)
         );
         PartDefinition cube_r46 = head.addOrReplaceChild(
            "cube_r46",
            CubeListBuilder.create().texOffs(40, 24).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, -4.5F, 6.0F, 0.0F, -0.3054F, 0.0F)
         );
         PartDefinition cube_r47 = head.addOrReplaceChild(
            "cube_r47",
            CubeListBuilder.create().texOffs(32, 35).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.5F, -3.0F, 6.0F, 0.2182F, 0.0F, 0.0F)
         );
         PartDefinition cube_r48 = head.addOrReplaceChild(
            "cube_r48",
            CubeListBuilder.create().texOffs(36, 16).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.5F, -6.0F, 6.0F, -0.2618F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(16, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(24, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
