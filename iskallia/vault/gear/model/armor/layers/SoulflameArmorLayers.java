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

public class SoulflameArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SoulflameArmorLayers.LeggingsLayer::createBodyLayer : SoulflameArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SoulflameArmorLayers.LeggingsLayer::new : SoulflameArmorLayers.MainLayer::new;
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
            CubeListBuilder.create()
               .texOffs(28, 36)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(74, 21)
               .addBox(-0.5F, -15.0241F, -5.1306F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition crown_r1 = head.addOrReplaceChild(
            "crown_r1",
            CubeListBuilder.create().texOffs(0, 73).addBox(-3.7013F, -3.5273F, -3.2142F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0808F, -10.2968F, 4.6772F, -3.1416F, -0.7854F, 3.1416F)
         );
         PartDefinition crown_r2 = head.addOrReplaceChild(
            "crown_r2",
            CubeListBuilder.create().texOffs(72, 50).addBox(-4.2013F, 0.4854F, -3.1813F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0808F, -10.2968F, 4.6772F, 2.618F, -0.7854F, -3.1416F)
         );
         PartDefinition crown_r3 = head.addOrReplaceChild(
            "crown_r3",
            CubeListBuilder.create().texOffs(70, 64).addBox(-4.2013F, 0.0608F, -4.6965F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0808F, -10.2968F, 4.6772F, -2.8362F, -0.7854F, 3.1416F)
         );
         PartDefinition crown_r4 = head.addOrReplaceChild(
            "crown_r4",
            CubeListBuilder.create().texOffs(64, 72).addBox(-0.4192F, -4.7273F, -0.3078F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0808F, -10.2968F, 4.6772F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition crown_r5 = head.addOrReplaceChild(
            "crown_r5",
            CubeListBuilder.create().texOffs(46, 71).addBox(-0.9192F, -1.7661F, -1.2977F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0808F, -10.2968F, 4.6772F, 2.618F, 0.0F, -3.1416F)
         );
         PartDefinition crown_r6 = head.addOrReplaceChild(
            "crown_r6",
            CubeListBuilder.create().texOffs(60, 6).addBox(-0.9192F, 0.01F, -1.6861F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0808F, -10.2968F, 4.6772F, -2.8362F, 0.0F, 3.1416F)
         );
         PartDefinition crown_r7 = head.addOrReplaceChild(
            "crown_r7",
            CubeListBuilder.create().texOffs(72, 58).addBox(2.7271F, -3.7273F, -2.9384F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0808F, -10.2968F, 4.6772F, -3.1416F, 0.7854F, 3.1416F)
         );
         PartDefinition crown_r8 = head.addOrReplaceChild(
            "crown_r8",
            CubeListBuilder.create().texOffs(14, 64).addBox(2.2271F, 0.4152F, -3.0758F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0808F, -10.2968F, 4.6772F, 2.618F, 0.7854F, -3.1416F)
         );
         PartDefinition crown_r9 = head.addOrReplaceChild(
            "crown_r9",
            CubeListBuilder.create().texOffs(70, 16).addBox(2.2271F, 0.0686F, -4.6718F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0808F, -10.2968F, 4.6772F, -2.8362F, 0.7854F, 3.1416F)
         );
         PartDefinition crown_r10 = head.addOrReplaceChild(
            "crown_r10",
            CubeListBuilder.create().texOffs(4, 73).addBox(-0.5F, -4.2273F, -0.0408F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.25F, -9.7968F, -4.5898F, 0.0F, -0.7854F, 0.0F)
         );
         PartDefinition crown_r11 = head.addOrReplaceChild(
            "crown_r11",
            CubeListBuilder.create().texOffs(52, 72).addBox(-1.0F, -1.4665F, -0.8165F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.25F, -9.7968F, -4.5898F, -0.5236F, -0.7854F, 0.0F)
         );
         PartDefinition crown_r12 = head.addOrReplaceChild(
            "crown_r12",
            CubeListBuilder.create().texOffs(70, 69).addBox(-1.0F, 0.463F, -1.7581F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.25F, -9.7968F, -4.5898F, 0.3054F, -0.7854F, 0.0F)
         );
         PartDefinition crown_r13 = head.addOrReplaceChild(
            "crown_r13",
            CubeListBuilder.create().texOffs(8, 73).addBox(-0.5F, -4.1273F, -0.1658F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, -9.6968F, -4.7148F, 0.0F, 0.7854F, 0.0F)
         );
         PartDefinition crown_r14 = head.addOrReplaceChild(
            "crown_r14",
            CubeListBuilder.create().texOffs(72, 54).addBox(-1.0F, -1.5585F, -0.8412F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, -9.6968F, -4.7148F, -0.5236F, 0.7854F, 0.0F)
         );
         PartDefinition crown_r15 = head.addOrReplaceChild(
            "crown_r15",
            CubeListBuilder.create().texOffs(40, 71).addBox(-1.0F, 0.4052F, -1.6088F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, -9.6968F, -4.7148F, 0.3054F, 0.7854F, 0.0F)
         );
         PartDefinition crown_r16 = head.addOrReplaceChild(
            "crown_r16",
            CubeListBuilder.create().texOffs(20, 64).addBox(-0.5F, -4.4889F, -0.125F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -10.5352F, -0.0056F, 0.0F, 1.5708F, 0.0F)
         );
         PartDefinition crown_r17 = head.addOrReplaceChild(
            "crown_r17",
            CubeListBuilder.create().texOffs(20, 44).addBox(-1.0F, -1.651F, -1.0202F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -10.5352F, -0.0056F, 0.0F, 1.5708F, 0.5236F)
         );
         PartDefinition crown_r18 = head.addOrReplaceChild(
            "crown_r18",
            CubeListBuilder.create().texOffs(30, 22).addBox(-1.0F, 0.2923F, -1.5835F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -10.5352F, -0.0056F, 0.0F, 1.5708F, -0.3054F)
         );
         PartDefinition crown_r19 = head.addOrReplaceChild(
            "crown_r19",
            CubeListBuilder.create().texOffs(52, 68).addBox(-0.5F, -4.4889F, -0.125F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, -10.5352F, -0.0056F, 0.0F, -1.5708F, 0.0F)
         );
         PartDefinition crown_r20 = head.addOrReplaceChild(
            "crown_r20",
            CubeListBuilder.create().texOffs(36, 52).addBox(-1.0F, -1.651F, -1.0202F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, -10.5352F, -0.0056F, 0.0F, -1.5708F, -0.5236F)
         );
         PartDefinition crown_r21 = head.addOrReplaceChild(
            "crown_r21",
            CubeListBuilder.create().texOffs(34, 4).addBox(-1.0F, 0.2923F, -1.5835F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, -10.5352F, -0.0056F, 0.0F, -1.5708F, 0.3054F)
         );
         PartDefinition crown_r22 = head.addOrReplaceChild(
            "crown_r22",
            CubeListBuilder.create().texOffs(58, 72).addBox(-1.0F, -2.5F, -1.75F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -9.435F, -4.798F, -0.5236F, 0.0F, 0.0F)
         );
         PartDefinition crown_r23 = head.addOrReplaceChild(
            "crown_r23",
            CubeListBuilder.create().texOffs(60, 42).addBox(-1.0F, -1.5F, -0.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -8.5F, -5.5F, 0.3054F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 44)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-6.0F, 0.0F, -5.0F, 12.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(0, 18)
               .addBox(-5.0F, -2.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(0, 70)
               .addBox(-11.8777F, 3.2718F, -5.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 60)
               .addBox(-12.8777F, 1.2718F, -5.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(56, 0)
               .addBox(11.9223F, 1.2718F, -5.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 48)
               .addBox(-11.6312F, -4.9738F, -5.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 4)
               .addBox(10.6188F, -4.9738F, -5.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(66, 13)
               .addBox(-11.8777F, 3.2718F, 4.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 30)
               .addBox(-11.6312F, -4.9738F, 4.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 22)
               .addBox(10.6188F, -4.9738F, 4.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 44)
               .addBox(-12.8777F, 1.2718F, 4.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(52, 52)
               .addBox(11.9223F, 1.2718F, 4.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(52, 36).addBox(-3.0F, -2.0F, -2.25F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.0F, 4.0F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(60, 0).addBox(-3.0F, -2.0F, 0.25F, 6.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 10.0F, -4.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(40, 68).addBox(-2.7676F, 1.2055F, -0.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(9.1899F, 2.1663F, 5.0F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(34, 0).addBox(-3.6084F, -2.9875F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(9.1899F, 2.1663F, 5.0F, -3.1416F, 0.0F, 2.6616F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(30, 18)
               .addBox(-7.5F, -2.5F, -0.5F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(66, 6)
               .addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(10, 60)
               .addBox(-7.5F, -2.5F, -10.5F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(26, 68)
               .addBox(-2.5F, -2.5F, -10.5F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, 3.5F, 4.5F, 0.0F, 0.0F, 0.48F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(66, 40)
               .addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(12, 68)
               .addBox(-2.5F, -2.5F, -10.5F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, 3.5F, 4.5F, 0.0F, 0.0F, -0.48F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(42, 16).addBox(-3.6084F, -2.9875F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(9.1899F, 2.1663F, -5.0F, -3.1416F, 0.0F, 2.6616F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(68, 47).addBox(-2.7676F, 1.2055F, -0.5F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(9.1899F, 2.1663F, -5.0F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(60, 22).addBox(-2.5F, -3.0F, -0.5F, 6.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.8512F, 2.0F, -5.5F, 0.4565F, -0.5194F, -0.2391F)
         );
         PartDefinition cube_r10 = body.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(56, 64).addBox(-3.5F, -3.0F, -0.5F, 6.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.8284F, 2.0F, -5.5F, 0.4565F, 0.5194F, 0.2391F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(56, 48).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r11 = right_arm.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(0, 18).addBox(-0.75F, 0.75F, -1.5F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-12.5485F, 4.4648F, 0.0F, 0.0F, 0.0F, -1.789F)
         );
         PartDefinition cube_r12 = right_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(68, 30).addBox(-0.75F, -3.5F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-14.1393F, -2.1473F, 0.0F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition cube_r13 = right_arm.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create().texOffs(0, 60).addBox(-3.5F, -1.0F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-11.7175F, -2.7678F, 0.0F, 0.0F, 0.0F, -1.1781F)
         );
         PartDefinition cube_r14 = right_arm.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(32, 22).addBox(-5.0F, -3.0F, -4.0F, 10.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, -1.0F, 0.0F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(40, 52).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r15 = left_arm.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create().texOffs(0, 0).addBox(-2.9229F, -2.5589F, -1.5F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.2117F, 1.6272F, 0.0F, -3.1416F, 0.0F, -1.3526F)
         );
         PartDefinition cube_r16 = left_arm.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create().texOffs(56, 12).addBox(-0.1778F, -4.4642F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.2117F, 1.6272F, 0.0F, 3.1416F, 0.0F, -1.9635F)
         );
         PartDefinition cube_r17 = left_arm.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create().texOffs(0, 30).addBox(0.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(16.4104F, -2.9127F, 0.0F, 3.1416F, 0.0F, -2.3562F)
         );
         PartDefinition cube_r18 = left_arm.addOrReplaceChild(
            "cube_r18",
            CubeListBuilder.create().texOffs(0, 30).addBox(-5.0F, -3.0F, -4.0F, 10.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(24, 52).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(44, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
