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

public class WitchArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? WitchArmorLayers.LeggingsLayer::createBodyLayer : WitchArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? WitchArmorLayers.LeggingsLayer::new : WitchArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(1, 87).addBox(-5.5F, 10.0F, -3.0F, 11.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition feather_r28_r1 = body.addOrReplaceChild(
            "feather_r28_r1",
            CubeListBuilder.create().texOffs(32, 31).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.83F, 13.9031F, -2.9458F, -0.1975F, 0.3687F, 1.3116F)
         );
         PartDefinition feather_r29_r1 = body.addOrReplaceChild(
            "feather_r29_r1",
            CubeListBuilder.create().texOffs(26, 33).mirror().addBox(-1.2358F, 0.0231F, -1.6961F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(6.5015F, 16.1736F, -0.25F, -0.0193F, 0.2173F, 1.3505F)
         );
         PartDefinition feather_r31_r1 = body.addOrReplaceChild(
            "feather_r31_r1",
            CubeListBuilder.create().texOffs(32, 31).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(6.9015F, 17.4735F, 0.0249F, 0.0F, 0.0F, 1.3963F)
         );
         PartDefinition feather_r30_r1 = body.addOrReplaceChild(
            "feather_r30_r1",
            CubeListBuilder.create().texOffs(26, 33).mirror().addBox(-1.2358F, 0.0231F, 0.6961F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(6.5015F, 16.1736F, 0.25F, 0.0193F, -0.2173F, 1.3505F)
         );
         PartDefinition feather_r29_r2 = body.addOrReplaceChild(
            "feather_r29_r2",
            CubeListBuilder.create().texOffs(32, 31).mirror().addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.83F, 13.9031F, 2.9458F, 0.1975F, -0.3687F, 1.3116F)
         );
         PartDefinition feather_r28_r2 = body.addOrReplaceChild(
            "feather_r28_r2",
            CubeListBuilder.create().texOffs(32, 31).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.83F, 13.9031F, 2.9458F, 0.1975F, 0.3687F, -1.3116F)
         );
         PartDefinition feather_r29_r3 = body.addOrReplaceChild(
            "feather_r29_r3",
            CubeListBuilder.create().texOffs(26, 33).addBox(-1.7642F, 0.0231F, 0.6961F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.5015F, 16.1736F, 0.25F, 0.0193F, 0.2173F, -1.3505F)
         );
         PartDefinition feather_r27_r1 = body.addOrReplaceChild(
            "feather_r27_r1",
            CubeListBuilder.create().texOffs(32, 31).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.83F, 13.9031F, -2.9458F, -0.1975F, -0.3687F, -1.3116F)
         );
         PartDefinition feather_r28_r3 = body.addOrReplaceChild(
            "feather_r28_r3",
            CubeListBuilder.create().texOffs(26, 33).addBox(-1.7642F, 0.0231F, -1.6961F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.5015F, 16.1736F, -0.25F, -0.0193F, -0.2173F, -1.3505F)
         );
         PartDefinition feather_r30_r2 = body.addOrReplaceChild(
            "feather_r30_r2",
            CubeListBuilder.create().texOffs(32, 31).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.9015F, 17.4735F, 0.0249F, 0.0F, 0.0F, -1.3963F)
         );
         PartDefinition body_r1 = body.addOrReplaceChild(
            "body_r1",
            CubeListBuilder.create().texOffs(0, 97).mirror().addBox(-0.6333F, 1.5247F, -2.933F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.8833F, 13.5099F, 0.0F, -1.2654F, 0.0F, -0.3054F)
         );
         PartDefinition body_r2 = body.addOrReplaceChild(
            "body_r2",
            CubeListBuilder.create().texOffs(0, 97).mirror().addBox(-0.3333F, 1.2947F, -0.8553F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.6833F, 13.5099F, 0.0F, -0.7854F, 0.0F, -0.3054F)
         );
         PartDefinition body_r3 = body.addOrReplaceChild(
            "body_r3",
            CubeListBuilder.create().texOffs(3, 95).mirror().addBox(-0.4323F, -1.1838F, -3.7838F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.5833F, 13.5099F, 0.0F, -0.7854F, 0.0F, -0.3054F)
         );
         PartDefinition body_r4 = body.addOrReplaceChild(
            "body_r4",
            CubeListBuilder.create().texOffs(3, 95).mirror().addBox(-0.4333F, -1.1838F, -2.2162F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.5833F, 13.5099F, 0.0F, 0.7854F, 0.0F, -0.3054F)
         );
         PartDefinition body_r5 = body.addOrReplaceChild(
            "body_r5",
            CubeListBuilder.create().texOffs(0, 97).mirror().addBox(-0.6333F, 1.5247F, -0.067F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.8833F, 13.5099F, 0.0F, 1.2654F, 0.0F, -0.3054F)
         );
         PartDefinition body_r6 = body.addOrReplaceChild(
            "body_r6",
            CubeListBuilder.create().texOffs(0, 97).mirror().addBox(-0.4333F, 1.2947F, -2.1447F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.6833F, 13.5099F, 0.0F, 0.7854F, 0.0F, -0.3054F)
         );
         PartDefinition body_r7 = body.addOrReplaceChild(
            "body_r7",
            CubeListBuilder.create().texOffs(0, 97).addBox(-0.6667F, 1.2947F, -0.8553F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.6833F, 13.5099F, 0.0F, -0.7854F, 0.0F, 0.3054F)
         );
         PartDefinition body_r8 = body.addOrReplaceChild(
            "body_r8",
            CubeListBuilder.create().texOffs(0, 97).addBox(-0.3667F, 1.5247F, -2.933F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.8833F, 13.5099F, 0.0F, -1.2654F, 0.0F, 0.3054F)
         );
         PartDefinition body_r9 = body.addOrReplaceChild(
            "body_r9",
            CubeListBuilder.create().texOffs(0, 97).addBox(-0.3667F, 1.5247F, -0.067F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.8833F, 13.5099F, 0.0F, 1.2654F, 0.0F, 0.3054F)
         );
         PartDefinition body_r10 = body.addOrReplaceChild(
            "body_r10",
            CubeListBuilder.create().texOffs(0, 97).addBox(-0.5667F, 1.2947F, -2.1447F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.6833F, 13.5099F, 0.0F, 0.7854F, 0.0F, 0.3054F)
         );
         PartDefinition body_r11 = body.addOrReplaceChild(
            "body_r11",
            CubeListBuilder.create().texOffs(3, 95).addBox(-0.5677F, -1.1838F, -3.7838F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5833F, 13.5099F, 0.0F, -0.7854F, 0.0F, 0.3054F)
         );
         PartDefinition body_r12 = body.addOrReplaceChild(
            "body_r12",
            CubeListBuilder.create().texOffs(3, 95).addBox(-0.5667F, -1.1838F, -2.2162F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5833F, 13.5099F, 0.0F, 0.7854F, 0.0F, 0.3054F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 111).addBox(-3.0F, -1.0F, -2.49F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_leg_r1 = right_leg.addOrReplaceChild(
            "right_leg_r1", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4897F, 3.8373F, 0.0226F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition right_leg_r2 = right_leg.addOrReplaceChild(
            "right_leg_r2", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4897F, 3.8373F, 0.0226F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition right_leg_r3 = right_leg.addOrReplaceChild(
            "right_leg_r3", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4897F, 3.8373F, 0.0226F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition right_leg_r4 = right_leg.addOrReplaceChild(
            "right_leg_r4", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4897F, 3.8373F, 0.0226F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition right_leg_r5 = right_leg.addOrReplaceChild(
            "right_leg_r5", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4897F, 4.0373F, 0.0226F, -0.3054F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r6_r1 = right_leg_r5.addOrReplaceChild(
            "right_leg_r6_r1",
            CubeListBuilder.create()
               .texOffs(0, 103)
               .addBox(-3.0F, -1.9312F, -3.2418F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 103)
               .addBox(-3.0F, -1.9312F, -3.2418F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0165F, -0.4052F, -0.1414F, 1.2654F, -1.2654F, -1.5708F)
         );
         PartDefinition right_leg_r6 = right_leg.addOrReplaceChild(
            "right_leg_r6", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4897F, 4.0373F, 0.0226F, 0.3054F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r7 = right_leg.addOrReplaceChild(
            "right_leg_r7",
            CubeListBuilder.create().texOffs(0, 103).addBox(-2.9835F, -3.8402F, -2.9976F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-0.4897F, 3.8373F, 0.0226F)
         );
         PartDefinition right_leg_r8 = right_leg.addOrReplaceChild(
            "right_leg_r8", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.6764F, 6.9405F, 1.1254F, -0.85F, -0.4211F, 1.6939F)
         );
         PartDefinition right_leg_r9 = right_leg.addOrReplaceChild(
            "right_leg_r9", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0764F, 6.7405F, 1.9254F, 2.4003F, 0.0576F, -0.6523F)
         );
         PartDefinition right_leg_r10 = right_leg.addOrReplaceChild(
            "right_leg_r10", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.1719F, 5.4737F, 0.227F, 2.8792F, 0.8588F, -0.9424F)
         );
         PartDefinition right_leg_r11 = right_leg.addOrReplaceChild(
            "right_leg_r11", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.9886F, 6.5494F, -1.8674F, 1.0093F, 0.7614F, 0.2047F)
         );
         PartDefinition right_leg_r12 = right_leg.addOrReplaceChild(
            "right_leg_r12", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.1886F, 4.8494F, 1.5326F, -0.01F, -1.1486F, 0.519F)
         );
         PartDefinition right_leg_r13 = right_leg.addOrReplaceChild(
            "right_leg_r13", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.9886F, 6.5494F, -1.8674F, -0.2169F, 0.0193F, 0.5191F)
         );
         PartDefinition right_leg_r14 = right_leg.addOrReplaceChild(
            "right_leg_r14", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.7897F, 3.8373F, 0.7726F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r15 = right_leg.addOrReplaceChild(
            "right_leg_r15", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4897F, 3.8373F, 0.0226F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r16 = right_leg.addOrReplaceChild(
            "right_leg_r16",
            CubeListBuilder.create().texOffs(0, 103).addBox(-2.4835F, 2.7598F, -2.9976F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-0.4897F, 5.0873F, 0.0226F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 111).mirror().addBox(-2.0F, -1.0F, -2.49F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg_r1 = left_leg.addOrReplaceChild(
            "left_leg_r1", CubeListBuilder.create(), PartPose.offsetAndRotation(0.7897F, 3.8373F, 0.7726F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r2 = left_leg.addOrReplaceChild(
            "left_leg_r2", CubeListBuilder.create(), PartPose.offsetAndRotation(3.9886F, 6.5494F, -1.8674F, -0.2169F, -0.0193F, -0.5191F)
         );
         PartDefinition left_leg_r3 = left_leg.addOrReplaceChild(
            "left_leg_r3", CubeListBuilder.create(), PartPose.offsetAndRotation(3.9886F, 6.5494F, -1.8674F, 1.0093F, -0.7614F, -0.2047F)
         );
         PartDefinition left_leg_r4 = left_leg.addOrReplaceChild(
            "left_leg_r4", CubeListBuilder.create(), PartPose.offsetAndRotation(5.1886F, 4.8494F, 1.5326F, -0.01F, 1.1486F, -0.519F)
         );
         PartDefinition left_leg_r5 = left_leg.addOrReplaceChild(
            "left_leg_r5", CubeListBuilder.create(), PartPose.offsetAndRotation(4.1719F, 5.4737F, 0.227F, 2.8792F, -0.8588F, 0.9424F)
         );
         PartDefinition left_leg_r6 = left_leg.addOrReplaceChild(
            "left_leg_r6", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0764F, 6.7405F, 1.9254F, 2.4003F, -0.0576F, 0.6523F)
         );
         PartDefinition left_leg_r7 = left_leg.addOrReplaceChild(
            "left_leg_r7", CubeListBuilder.create(), PartPose.offsetAndRotation(4.6764F, 6.9405F, 1.1254F, -0.85F, 0.4211F, -1.6939F)
         );
         PartDefinition left_leg_r8 = left_leg.addOrReplaceChild(
            "left_leg_r8", CubeListBuilder.create(), PartPose.offsetAndRotation(0.3F, 6.0346F, -1.333F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition left_leg_r9 = left_leg.addOrReplaceChild(
            "left_leg_r9", CubeListBuilder.create(), PartPose.offsetAndRotation(0.3F, 3.5346F, -1.333F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition left_leg_r10 = left_leg.addOrReplaceChild(
            "left_leg_r10", CubeListBuilder.create(), PartPose.offsetAndRotation(0.65F, 3.5346F, -1.433F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition left_leg_r11 = left_leg.addOrReplaceChild(
            "left_leg_r11", CubeListBuilder.create(), PartPose.offsetAndRotation(0.65F, 6.0346F, -1.433F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition left_leg_r12 = left_leg.addOrReplaceChild(
            "left_leg_r12", CubeListBuilder.create(), PartPose.offsetAndRotation(0.65F, 3.5346F, 1.433F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition left_leg_r13 = left_leg.addOrReplaceChild(
            "left_leg_r13", CubeListBuilder.create(), PartPose.offsetAndRotation(0.3F, 3.5346F, 1.333F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition left_leg_r14 = left_leg.addOrReplaceChild(
            "left_leg_r14", CubeListBuilder.create(), PartPose.offsetAndRotation(0.65F, 6.0346F, 1.433F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition left_leg_r15 = left_leg.addOrReplaceChild(
            "left_leg_r15", CubeListBuilder.create(), PartPose.offsetAndRotation(0.3F, 6.0346F, 1.333F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition left_leg_r16 = left_leg.addOrReplaceChild(
            "left_leg_r16", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.0268F, -0.2529F, -2.033F, -0.3054F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r17 = left_leg.addOrReplaceChild(
            "left_leg_r17", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.0268F, 1.7471F, -3.783F, 0.3054F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r18 = left_leg.addOrReplaceChild(
            "left_leg_r18",
            CubeListBuilder.create().texOffs(0, 103).mirror().addBox(-2.5F, 2.5F, 0.033F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(-0.0268F, -2.5029F, -3.033F)
         );
         PartDefinition left_leg_r19 = left_leg.addOrReplaceChild(
            "left_leg_r19", CubeListBuilder.create(), PartPose.offsetAndRotation(0.6732F, 7.0971F, 0.0F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r20 = left_leg.addOrReplaceChild(
            "left_leg_r20",
            CubeListBuilder.create().texOffs(0, 103).mirror().addBox(-2.0F, 2.5F, 0.033F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(-0.0268F, 5.3471F, -3.033F)
         );
         PartDefinition left_leg_r21 = left_leg.addOrReplaceChild("left_leg_r21", CubeListBuilder.create(), PartPose.offset(-0.0268F, 4.0971F, -3.033F));
         PartDefinition right_leg_r17 = left_leg.addOrReplaceChild(
            "right_leg_r17", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.2897F, 4.0373F, 0.0226F, -0.3054F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r6_r2 = right_leg_r17.addOrReplaceChild(
            "right_leg_r6_r2",
            CubeListBuilder.create()
               .texOffs(0, 103)
               .mirror()
               .addBox(-3.0F, -1.9312F, -3.2418F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(0, 103)
               .mirror()
               .addBox(-3.0F, -1.9312F, -3.2418F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offsetAndRotation(4.7629F, -0.4052F, -0.1414F, 1.2654F, 1.2654F, 1.5708F)
         );
         PartDefinition right_leg_r18 = left_leg.addOrReplaceChild(
            "right_leg_r18", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.2897F, 4.0373F, 0.0226F, 0.3054F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
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
            "head", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0436F, 0.0F, 0.0F)
         );
         PartDefinition head_r1 = head.addOrReplaceChild(
            "head_r1",
            CubeListBuilder.create().texOffs(0, 14).addBox(-2.75F, -1.95F, -5.25F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, -5.0F, 0.0F, -0.3048F, -0.0186F, -0.1276F)
         );
         PartDefinition head_r2 = head.addOrReplaceChild(
            "head_r2",
            CubeListBuilder.create().texOffs(0, 55).addBox(1.35F, 0.05F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.704F, -14.2025F, 5.7117F, -0.5968F, 0.6696F, 1.2934F)
         );
         PartDefinition head_r3 = head.addOrReplaceChild(
            "head_r3",
            CubeListBuilder.create().texOffs(0, 30).addBox(1.5F, 0.0F, -0.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.4009F, -12.7447F, 6.4586F, -0.6173F, 0.8249F, 1.5822F)
         );
         PartDefinition head_r4 = head.addOrReplaceChild(
            "head_r4",
            CubeListBuilder.create()
               .texOffs(75, 74)
               .addBox(6.5F, -31.75F, -11.25F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(43, 28)
               .addBox(-1.5F, -31.75F, -11.25F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(34, 67)
               .addBox(-3.5F, -31.75F, -11.25F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(70, 65)
               .addBox(-3.5F, -31.75F, -4.25F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-1.5F, -31.75F, -9.25F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.6618F, 25.413F, -5.3821F, -0.3048F, -0.0186F, -0.1276F)
         );
         PartDefinition head_r5 = head.addOrReplaceChild(
            "head_r5",
            CubeListBuilder.create().texOffs(30, 20).addBox(-3.175F, -0.9999F, -0.175F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.6707F, -7.2899F, -5.1F, -0.3521F, -0.5156F, 0.0458F)
         );
         PartDefinition head_r6 = head.addOrReplaceChild(
            "head_r6",
            CubeListBuilder.create().texOffs(16, 78).addBox(-1.575F, -0.9746F, 0.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0033F, -7.4583F, -5.8296F, -0.4114F, 0.7224F, -0.4143F)
         );
         PartDefinition head_r7 = head.addOrReplaceChild(
            "head_r7",
            CubeListBuilder.create().texOffs(34, 51).addBox(-0.425F, -1.0F, -1.25F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.2544F, -4.9491F, -1.0762F, -0.4441F, -0.7977F, 0.1948F)
         );
         PartDefinition head_r8 = head.addOrReplaceChild(
            "head_r8",
            CubeListBuilder.create().texOffs(54, 29).addBox(-0.575F, -0.9748F, -0.975F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.879F, -5.6909F, -3.5945F, -0.353F, 0.5198F, -0.3144F)
         );
         PartDefinition head_r9 = head.addOrReplaceChild(
            "head_r9",
            CubeListBuilder.create().texOffs(0, 25).addBox(-2.5F, 0.0F, -4.5F, 9.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.9901F, -8.6229F, 0.9681F, -0.3481F, 0.0316F, 0.0844F)
         );
         PartDefinition head_r10 = head.addOrReplaceChild(
            "head_r10",
            CubeListBuilder.create().texOffs(36, 0).addBox(-1.5F, -1.5F, -3.75F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.9868F, -9.5805F, 1.2908F, -0.4582F, 0.1451F, 0.2752F)
         );
         PartDefinition head_r11 = head.addOrReplaceChild(
            "head_r11",
            CubeListBuilder.create().texOffs(72, 19).addBox(3.0F, -2.55F, -2.25F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.5121F, -13.9451F, 5.148F, -0.4867F, 0.5183F, 0.7968F)
         );
         PartDefinition head_r12 = head.addOrReplaceChild(
            "head_r12",
            CubeListBuilder.create().texOffs(59, 28).addBox(0.0F, -1.5F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.3545F, -11.8213F, 2.9487F, -0.3675F, 0.2629F, 0.4639F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(40, 14)
               .addBox(-5.0F, -0.5F, -3.0F, 10.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(31, 32)
               .addBox(-4.5F, 7.501F, -2.501F, 9.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body_r1 = body.addOrReplaceChild(
            "body_r1",
            CubeListBuilder.create()
               .texOffs(72, 38)
               .addBox(-5.0F, -16.5F, 0.01F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(57, 72)
               .addBox(-5.0F, -16.5F, -2.99F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.9102F, 24.2703F, 0.0F, 0.0F, 0.0F, 0.1745F)
         );
         PartDefinition body_r2 = body.addOrReplaceChild(
            "body_r2",
            CubeListBuilder.create().texOffs(67, 74).addBox(-1.025F, -2.7F, -1.5F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.2869F, 14.7125F, -1.49F, -0.5236F, 0.0F, -0.1745F)
         );
         PartDefinition body_r3 = body.addOrReplaceChild(
            "body_r3",
            CubeListBuilder.create().texOffs(59, 38).addBox(-0.975F, -2.7F, -1.5F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.2869F, 14.7125F, -1.49F, -0.5236F, 0.0F, 0.1745F)
         );
         PartDefinition body_r4 = body.addOrReplaceChild(
            "body_r4",
            CubeListBuilder.create().texOffs(0, 14).addBox(3.0F, -16.5F, -2.99F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.9102F, 24.2703F, 0.0F, 0.0F, 0.0F, -0.1745F)
         );
         PartDefinition body_r5 = body.addOrReplaceChild(
            "body_r5",
            CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -4.5F, -1.5F, 2.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.7661F, 11.758F, 1.51F, 0.0F, 0.0F, -0.1745F)
         );
         PartDefinition body_r6 = body.addOrReplaceChild(
            "body_r6",
            CubeListBuilder.create().texOffs(0, 37).addBox(-4.975F, -10.5F, -0.75F, 10.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.01F, 11.2618F, 2.7227F, 0.1317F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(-5.0F, 1.0F, 0.0F, -0.0133F, -0.0125F, -0.0853F)
         );
         PartDefinition rightarm_r1 = right_arm.addOrReplaceChild(
            "rightarm_r1",
            CubeListBuilder.create().texOffs(0, 61).addBox(-2.2814F, 1.8917F, -2.9187F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.6951F, 5.1954F, 0.2218F, -0.014F, 0.0821F, 0.1306F)
         );
         PartDefinition rightarm_r2 = right_arm.addOrReplaceChild(
            "rightarm_r2",
            CubeListBuilder.create().texOffs(39, 51).addBox(-1.969F, -8.4881F, -2.5253F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.6951F, 5.9454F, 0.2218F, -0.0135F, -0.0064F, 0.0445F)
         );
         PartDefinition rightarm_r3 = right_arm.addOrReplaceChild(
            "rightarm_r3",
            CubeListBuilder.create().texOffs(30, 14).addBox(-1.75F, -0.5F, -2.25F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.1622F, 7.9253F, 3.2088F, -0.2886F, 0.8566F, -0.0735F)
         );
         PartDefinition rightarm_r4 = right_arm.addOrReplaceChild(
            "rightarm_r4",
            CubeListBuilder.create()
               .texOffs(36, 77)
               .addBox(-4.75F, 1.5F, 0.25F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(36, 0)
               .addBox(-4.25F, -1.5F, 0.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0983F, 3.2608F, -0.7944F, -0.0041F, 0.7798F, 0.0409F)
         );
         PartDefinition rightarm_r5 = right_arm.addOrReplaceChild(
            "rightarm_r5",
            CubeListBuilder.create().texOffs(36, 0).addBox(-1.25F, -1.25F, -0.75F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5781F, 0.1539F, 2.0367F, -0.0041F, -0.791F, 0.0467F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm", CubeListBuilder.create(), PartPose.offsetAndRotation(5.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0436F)
         );
         PartDefinition leftarm_r1 = left_arm.addOrReplaceChild(
            "leftarm_r1",
            CubeListBuilder.create().texOffs(60, 0).addBox(-3.7186F, 1.8917F, -2.9187F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.7068F, 5.1989F, 0.1468F, -0.0217F, -0.0703F, -0.1311F)
         );
         PartDefinition leftarm_r2 = left_arm.addOrReplaceChild(
            "leftarm_r2",
            CubeListBuilder.create()
               .texOffs(19, 51)
               .addBox(-3.031F, -8.4881F, -2.5253F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(54, 45)
               .addBox(-3.531F, -9.2381F, -3.0253F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.7068F, 5.9489F, 0.1468F, -0.0201F, 0.0188F, -0.0457F)
         );
         PartDefinition leftarm_r3 = left_arm.addOrReplaceChild(
            "leftarm_r3",
            CubeListBuilder.create().texOffs(27, 25).addBox(-2.25F, -0.5F, -2.25F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.1739F, 7.9288F, 3.1338F, -0.2964F, -0.8435F, 0.0782F)
         );
         PartDefinition leftarm_r4 = left_arm.addOrReplaceChild(
            "leftarm_r4",
            CubeListBuilder.create()
               .texOffs(77, 27)
               .addBox(2.0F, 1.5F, 0.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 25)
               .addBox(2.5F, -1.5F, 0.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5866F, 3.2643F, -0.8694F, -0.0132F, -0.7673F, -0.0356F)
         );
         PartDefinition feathers = left_arm.addOrReplaceChild("feathers", CubeListBuilder.create(), PartPose.offset(-1.4287F, 0.229F, 0.15F));
         PartDefinition feather_r1 = feathers.addOrReplaceChild(
            "feather_r1",
            CubeListBuilder.create().texOffs(23, 46).addBox(0.25F, 0.5F, 0.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.3075F, -3.5968F, 2.3999F, -0.5107F, -0.5313F, -0.6482F)
         );
         PartDefinition feather_r2 = feathers.addOrReplaceChild(
            "feather_r2",
            CubeListBuilder.create().texOffs(23, 45).addBox(-1.5F, -0.2F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.4492F, -1.1555F, 3.3527F, -1.7342F, -0.5874F, 0.389F)
         );
         PartDefinition feather_r3 = feathers.addOrReplaceChild(
            "feather_r3",
            CubeListBuilder.create().texOffs(23, 44).addBox(-0.5F, 1.25F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.2999F, 0.6819F, 3.9032F, -2.0813F, -0.3443F, 1.1283F)
         );
         PartDefinition feather_r4 = feathers.addOrReplaceChild(
            "feather_r4",
            CubeListBuilder.create().texOffs(53, 12).addBox(-1.0F, 0.25F, -1.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.3075F, -3.5968F, -2.3999F, 0.5107F, 0.5313F, -0.6482F)
         );
         PartDefinition feather_r5 = feathers.addOrReplaceChild(
            "feather_r5",
            CubeListBuilder.create().texOffs(47, 12).addBox(-0.5F, 1.25F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.2999F, 0.6819F, -3.9032F, 2.0813F, 0.3443F, 1.1283F)
         );
         PartDefinition feather_r6 = feathers.addOrReplaceChild(
            "feather_r6",
            CubeListBuilder.create().texOffs(23, 42).addBox(-0.5F, 0.1F, 1.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.1114F, -1.5281F, 3.6198F, -2.0357F, -0.4057F, 1.0041F)
         );
         PartDefinition feather_r7 = feathers.addOrReplaceChild(
            "feather_r7",
            CubeListBuilder.create().texOffs(23, 43).addBox(-2.0F, -0.4F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.1114F, -1.5281F, 3.6198F, -1.4383F, -0.5375F, 0.5115F)
         );
         PartDefinition feather_r8 = feathers.addOrReplaceChild(
            "feather_r8",
            CubeListBuilder.create().texOffs(23, 43).addBox(-2.0F, -0.15F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.1114F, -1.5281F, -3.6198F, 1.4383F, 0.5375F, 0.5115F)
         );
         PartDefinition feather_r9 = feathers.addOrReplaceChild(
            "feather_r9",
            CubeListBuilder.create().texOffs(23, 43).addBox(-0.5F, 0.1F, -2.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.1114F, -1.5281F, -3.6198F, 2.0357F, 0.4057F, 1.0041F)
         );
         PartDefinition feather_r10 = feathers.addOrReplaceChild(
            "feather_r10",
            CubeListBuilder.create().texOffs(47, 13).addBox(-1.5F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.4492F, -1.1555F, -3.3527F, 1.7342F, 0.5874F, 0.389F)
         );
         PartDefinition feather_r11 = feathers.addOrReplaceChild(
            "feather_r11",
            CubeListBuilder.create().texOffs(58, 57).addBox(-1.5F, 0.25F, -0.25F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.7935F, -3.8956F, -1.885F, 0.2882F, 0.5236F, -0.4894F)
         );
         PartDefinition feather_r12 = feathers.addOrReplaceChild(
            "feather_r12",
            CubeListBuilder.create().texOffs(58, 56).addBox(-1.3959F, -0.2137F, 0.6724F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.909F, -3.7249F, -0.1261F, -0.2465F, -0.2182F, -0.4841F)
         );
         PartDefinition feather_r13 = feathers.addOrReplaceChild(
            "feather_r13",
            CubeListBuilder.create().texOffs(58, 56).addBox(-1.4228F, -0.2341F, -1.4583F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.909F, -3.7249F, 0.1261F, 0.0243F, 0.436F, -0.3914F)
         );
         PartDefinition feather_r14 = feathers.addOrReplaceChild(
            "feather_r14",
            CubeListBuilder.create().texOffs(11, 58).addBox(-0.75F, 0.25F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.8214F, -3.843F, 2.1146F, -0.4088F, -0.3926F, -0.4746F)
         );
         PartDefinition feather_r15 = feathers.addOrReplaceChild(
            "feather_r15",
            CubeListBuilder.create().texOffs(23, 41).addBox(-0.8894F, -0.9203F, 1.1842F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.4017F, 3.3284F, 0.0985F, -0.5627F, -0.3357F, 1.3216F)
         );
         PartDefinition feather_r16 = feathers.addOrReplaceChild(
            "feather_r16",
            CubeListBuilder.create().texOffs(23, 40).addBox(-1.0927F, -1.0827F, -2.3443F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.4017F, 3.3284F, 0.0985F, 0.6259F, 0.2169F, 1.1857F)
         );
         PartDefinition feather_r17 = feathers.addOrReplaceChild(
            "feather_r17",
            CubeListBuilder.create().texOffs(23, 39).addBox(-0.3885F, 0.5048F, -0.772F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.4017F, 3.3284F, 0.0985F, -0.008F, -0.1183F, 1.2219F)
         );
         PartDefinition feather_r18 = feathers.addOrReplaceChild(
            "feather_r18",
            CubeListBuilder.create().texOffs(23, 49).addBox(-1.0927F, -0.8327F, -2.0443F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.9017F, 0.3284F, 0.0985F, 0.6259F, 0.2169F, 1.1857F)
         );
         PartDefinition feather_r19 = feathers.addOrReplaceChild(
            "feather_r19",
            CubeListBuilder.create().texOffs(23, 48).addBox(-0.3885F, 0.5048F, -0.022F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.9017F, 0.3284F, 0.0985F, -0.0081F, 0.2308F, 1.2191F)
         );
         PartDefinition feather_r20 = feathers.addOrReplaceChild(
            "feather_r20",
            CubeListBuilder.create().texOffs(23, 47).addBox(-0.8894F, -0.6703F, 1.0842F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.9017F, 0.3284F, 0.0985F, -0.5627F, -0.3357F, 1.3216F)
         );
         PartDefinition feather_r21 = feathers.addOrReplaceChild(
            "feather_r21",
            CubeListBuilder.create().texOffs(17, 55).addBox(-1.5F, 0.75F, -0.25F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.401F, -2.0952F, 2.0806F, -0.5627F, -0.3357F, 0.6235F)
         );
         PartDefinition feather_r22 = feathers.addOrReplaceChild(
            "feather_r22",
            CubeListBuilder.create().texOffs(54, 28).addBox(-1.5F, 0.75F, -0.75F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.3731F, -2.1478F, -1.919F, 0.6259F, 0.2169F, 0.4876F)
         );
         PartDefinition feather_r23 = feathers.addOrReplaceChild(
            "feather_r23",
            CubeListBuilder.create().texOffs(53, 13).addBox(-0.5F, 1.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.679F, -3.3455F, 0.1018F, -0.0109F, 0.0101F, 0.2609F)
         );
         PartDefinition feather_r24 = feathers.addOrReplaceChild(
            "feather_r24",
            CubeListBuilder.create().texOffs(58, 58).addBox(-0.75F, 0.0F, -0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.679F, -3.3455F, 0.1018F, 0.4006F, -0.1312F, -0.2155F)
         );
         PartDefinition feather_r25 = feathers.addOrReplaceChild(
            "feather_r25",
            CubeListBuilder.create().texOffs(23, 50).addBox(-1.5F, 0.0F, 0.25F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0712F, -2.7099F, 2.5799F, -0.9739F, -0.6044F, 0.6491F)
         );
         PartDefinition feather_r26 = feathers.addOrReplaceChild(
            "feather_r26",
            CubeListBuilder.create().texOffs(59, 0).addBox(-1.5F, 0.0F, -1.25F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.0712F, -2.7099F, -2.5799F, 0.9739F, 0.6044F, 0.6491F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-2.0F, 12.0F, 0.0F));
         PartDefinition feather_r27 = right_leg.addOrReplaceChild(
            "feather_r27",
            CubeListBuilder.create().texOffs(32, 31).addBox(-14.4073F, 8.6673F, -6.5443F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-8.4017F, -8.6716F, 0.0985F, 0.6259F, -0.2169F, -1.1857F)
         );
         PartDefinition feather_r28 = right_leg.addOrReplaceChild(
            "feather_r28",
            CubeListBuilder.create().texOffs(26, 33).addBox(-1.25F, 0.25F, -1.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0794F, 6.8242F, -0.7251F, 0.652F, 0.1706F, -1.1512F)
         );
         PartDefinition feather_r29 = right_leg.addOrReplaceChild(
            "feather_r29",
            CubeListBuilder.create().texOffs(35, 7).mirror().addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-2.4226F, 6.7928F, 3.351F, -1.0817F, 0.55F, -1.552F)
         );
         PartDefinition feather_r30 = right_leg.addOrReplaceChild(
            "feather_r30",
            CubeListBuilder.create().texOffs(32, 31).addBox(-1.25F, 0.35F, 0.25F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0794F, 6.8242F, -0.7251F, -0.1277F, 7.0E-4F, -1.1415F)
         );
         PartDefinition feather_r31 = right_leg.addOrReplaceChild(
            "feather_r31",
            CubeListBuilder.create().texOffs(26, 32).addBox(-1.0F, 0.75F, 0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0794F, 6.8242F, 0.7251F, -0.5606F, 0.1156F, -1.1423F)
         );
         PartDefinition feather_r32 = right_leg.addOrReplaceChild(
            "feather_r32",
            CubeListBuilder.create().texOffs(26, 31).addBox(-14.4073F, 8.9173F, 5.7943F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-8.4017F, -9.0716F, -0.0985F, -0.6259F, 0.2169F, -1.1857F)
         );
         PartDefinition rightboot_r1 = right_leg.addOrReplaceChild(
            "rightboot_r1",
            CubeListBuilder.create()
               .texOffs(19, 67)
               .addBox(-2.5F, -17.75F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(78, 0)
               .addBox(-2.5F, -14.75F, -3.5F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(13, 69)
               .addBox(-2.0F, -13.75F, -4.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, 0.0873F, 0.0F)
         );
         PartDefinition rightboot_r2 = right_leg.addOrReplaceChild(
            "rightboot_r2",
            CubeListBuilder.create().texOffs(59, 56).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 5.75F, 0.0F, 0.0718F, 0.0936F, -0.175F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(2.0F, 12.0F, 0.0F));
         PartDefinition feather_r33 = left_leg.addOrReplaceChild(
            "feather_r33",
            CubeListBuilder.create().texOffs(21, 37).addBox(-1.75F, 0.35F, 0.25F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0794F, 6.8242F, -0.7251F, -0.1277F, -7.0E-4F, 1.1415F)
         );
         PartDefinition feather_r34 = left_leg.addOrReplaceChild(
            "feather_r34",
            CubeListBuilder.create().texOffs(35, 7).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.4226F, 6.7928F, 3.351F, -1.0817F, -0.55F, 1.552F)
         );
         PartDefinition feather_r35 = left_leg.addOrReplaceChild(
            "feather_r35",
            CubeListBuilder.create().texOffs(35, 7).addBox(11.4073F, 8.9173F, 5.7943F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(8.4017F, -9.0716F, -0.0985F, -0.6259F, -0.2169F, 1.1857F)
         );
         PartDefinition feather_r36 = left_leg.addOrReplaceChild(
            "feather_r36",
            CubeListBuilder.create().texOffs(35, 6).addBox(-2.0F, 0.75F, 0.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0794F, 6.8242F, 0.7251F, -0.5606F, -0.1156F, 1.1423F)
         );
         PartDefinition feather_r37 = left_leg.addOrReplaceChild(
            "feather_r37",
            CubeListBuilder.create().texOffs(21, 38).addBox(-1.75F, 0.25F, -1.5F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0794F, 6.8242F, -0.7251F, 0.652F, -0.1706F, 1.1512F)
         );
         PartDefinition feather_r38 = left_leg.addOrReplaceChild(
            "feather_r38",
            CubeListBuilder.create().texOffs(38, 28).addBox(11.4073F, 8.6673F, -6.5443F, 3.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(8.4017F, -8.6716F, 0.0985F, 0.6259F, 0.2169F, 1.1857F)
         );
         PartDefinition leftboot_r1 = left_leg.addOrReplaceChild(
            "leftboot_r1",
            CubeListBuilder.create()
               .texOffs(66, 8)
               .addBox(-2.5F, -17.75F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(77, 55)
               .addBox(-2.5F, -14.75F, -3.5F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(9, 55)
               .addBox(-2.0F, -13.75F, -4.5F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, 0.0F, -0.0873F, 0.0F)
         );
         PartDefinition leftboot_r2 = left_leg.addOrReplaceChild(
            "leftboot_r2",
            CubeListBuilder.create().texOffs(53, 64).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 5.75F, 0.0F, 0.0718F, -0.0936F, 0.175F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
