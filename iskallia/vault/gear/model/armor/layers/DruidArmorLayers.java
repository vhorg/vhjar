package iskallia.vault.gear.model.armor.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
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

public class DruidArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? DruidArmorLayers.LeggingsLayer::createBodyLayer : DruidArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? DruidArmorLayers.LeggingsLayer::new : DruidArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(0, 86).addBox(-5.5F, 10.0F, -3.5F, 11.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
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
            CubeListBuilder.create().texOffs(0, 110).addBox(-3.0F, -1.0F, -2.99F, 5.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_leg_r1 = right_leg.addOrReplaceChild(
            "right_leg_r1",
            CubeListBuilder.create()
               .texOffs(19, 101)
               .mirror()
               .addBox(-1.1696F, -2.8122F, -3.3306F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(19, 101)
               .mirror()
               .addBox(0.5982F, -4.5799F, -3.3306F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offsetAndRotation(-0.4897F, 3.8373F, 0.0226F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition right_leg_r2 = right_leg.addOrReplaceChild(
            "right_leg_r2",
            CubeListBuilder.create()
               .texOffs(19, 101)
               .addBox(-1.5774F, -4.6007F, -3.4306F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(19, 101)
               .addBox(0.1903F, -2.833F, -3.4306F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.4897F, 3.8373F, 0.0226F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition right_leg_r3 = right_leg.addOrReplaceChild(
            "right_leg_r3",
            CubeListBuilder.create()
               .texOffs(0, 101)
               .addBox(-1.5774F, -4.6007F, 2.4354F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 101)
               .addBox(0.1903F, -2.833F, 2.4354F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.4897F, 3.8373F, 0.0226F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition right_leg_r4 = right_leg.addOrReplaceChild(
            "right_leg_r4",
            CubeListBuilder.create()
               .texOffs(0, 101)
               .mirror()
               .addBox(0.5982F, -4.5799F, 2.3354F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(0, 101)
               .mirror()
               .addBox(-1.1696F, -2.8122F, 2.3354F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offsetAndRotation(-0.4897F, 3.8373F, 0.0226F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition right_leg_r5 = right_leg.addOrReplaceChild(
            "right_leg_r5",
            CubeListBuilder.create().texOffs(0, 78).addBox(-2.9835F, -0.981F, -3.6937F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.4897F, 4.0373F, 0.0226F, -0.3054F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r6 = right_leg.addOrReplaceChild(
            "right_leg_r6",
            CubeListBuilder.create().texOffs(0, 78).addBox(-2.9835F, -0.821F, -3.384F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.4897F, 4.0373F, 0.0226F, 0.3054F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r7 = right_leg.addOrReplaceChild(
            "right_leg_r7",
            CubeListBuilder.create()
               .texOffs(0, 102)
               .addBox(-2.9835F, -3.8402F, -3.4976F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(0, 102)
               .addBox(-2.9835F, 2.7598F, -3.4976F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-0.4897F, 3.8373F, 0.0226F)
         );
         PartDefinition right_leg_r8 = right_leg.addOrReplaceChild(
            "right_leg_r8",
            CubeListBuilder.create().texOffs(49, 121).addBox(-0.1699F, -0.7262F, 0.0424F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.6764F, 6.9405F, 1.1254F, -0.85F, -0.4211F, 1.6939F)
         );
         PartDefinition right_leg_r9 = right_leg.addOrReplaceChild(
            "right_leg_r9",
            CubeListBuilder.create().texOffs(49, 118).addBox(-0.2207F, -1.4946F, -0.1371F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0764F, 6.7405F, 1.9254F, 2.4003F, 0.0576F, -0.6523F)
         );
         PartDefinition right_leg_r10 = right_leg.addOrReplaceChild(
            "right_leg_r10",
            CubeListBuilder.create().texOffs(49, 118).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.1719F, 5.4737F, 0.227F, 2.8792F, 0.8588F, -0.9424F)
         );
         PartDefinition right_leg_r11 = right_leg.addOrReplaceChild(
            "right_leg_r11",
            CubeListBuilder.create().texOffs(49, 118).addBox(-0.5898F, -0.7177F, -0.29F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.9886F, 6.5494F, -1.8674F, 1.0093F, 0.7614F, 0.2047F)
         );
         PartDefinition right_leg_r12 = right_leg.addOrReplaceChild(
            "right_leg_r12",
            CubeListBuilder.create().texOffs(49, 121).addBox(-0.6538F, -0.3525F, -0.794F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.1886F, 4.8494F, 1.5326F, -0.01F, -1.1486F, 0.519F)
         );
         PartDefinition right_leg_r13 = right_leg.addOrReplaceChild(
            "right_leg_r13",
            CubeListBuilder.create().texOffs(49, 121).addBox(-0.6538F, -0.3525F, -0.794F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.9886F, 6.5494F, -1.8674F, -0.2169F, 0.0193F, 0.5191F)
         );
         PartDefinition right_leg_r14 = right_leg.addOrReplaceChild(
            "right_leg_r14",
            CubeListBuilder.create().texOffs(16, 112).addBox(-3.1835F, 0.8067F, -3.8034F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.7897F, 3.8373F, 0.7726F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r15 = right_leg.addOrReplaceChild(
            "right_leg_r15",
            CubeListBuilder.create().texOffs(11, 95).addBox(-3.1835F, 0.8067F, -3.8034F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.4897F, 3.8373F, 0.0226F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r16 = right_leg.addOrReplaceChild(
            "right_leg_r16",
            CubeListBuilder.create().texOffs(0, 102).addBox(-2.9835F, 2.7598F, -3.4976F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-0.4897F, 5.0873F, 0.0226F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 110).mirror().addBox(-2.0F, -1.0F, -2.99F, 5.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg_r1 = left_leg.addOrReplaceChild(
            "left_leg_r1",
            CubeListBuilder.create().texOffs(16, 112).mirror().addBox(2.1835F, 0.8067F, -3.8034F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(0.7897F, 3.8373F, 0.7726F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r2 = left_leg.addOrReplaceChild(
            "left_leg_r2",
            CubeListBuilder.create().texOffs(49, 121).mirror().addBox(-0.3462F, -0.3525F, -0.794F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(3.9886F, 6.5494F, -1.8674F, -0.2169F, -0.0193F, -0.5191F)
         );
         PartDefinition left_leg_r3 = left_leg.addOrReplaceChild(
            "left_leg_r3",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-0.4102F, -0.7177F, -0.29F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(3.9886F, 6.5494F, -1.8674F, 1.0093F, -0.7614F, -0.2047F)
         );
         PartDefinition left_leg_r4 = left_leg.addOrReplaceChild(
            "left_leg_r4",
            CubeListBuilder.create().texOffs(49, 121).mirror().addBox(-0.3462F, -0.3525F, -0.794F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.1886F, 4.8494F, 1.5326F, -0.01F, 1.1486F, -0.519F)
         );
         PartDefinition left_leg_r5 = left_leg.addOrReplaceChild(
            "left_leg_r5",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(4.1719F, 5.4737F, 0.227F, 2.8792F, -0.8588F, 0.9424F)
         );
         PartDefinition left_leg_r6 = left_leg.addOrReplaceChild(
            "left_leg_r6",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-0.7793F, -1.4946F, -0.1371F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(4.0764F, 6.7405F, 1.9254F, 2.4003F, -0.0576F, 0.6523F)
         );
         PartDefinition left_leg_r7 = left_leg.addOrReplaceChild(
            "left_leg_r7",
            CubeListBuilder.create().texOffs(49, 121).mirror().addBox(-0.8301F, -0.7262F, 0.0424F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(4.6764F, 6.9405F, 1.1254F, -0.85F, 0.4211F, -1.6939F)
         );
         PartDefinition left_leg_r8 = left_leg.addOrReplaceChild(
            "left_leg_r8",
            CubeListBuilder.create().texOffs(19, 101).addBox(-1.25F, -4.5F, -2.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.3F, 6.0346F, -1.333F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition left_leg_r9 = left_leg.addOrReplaceChild(
            "left_leg_r9",
            CubeListBuilder.create().texOffs(19, 101).addBox(-1.25F, -4.5F, -2.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.3F, 3.5346F, -1.333F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition left_leg_r10 = left_leg.addOrReplaceChild(
            "left_leg_r10",
            CubeListBuilder.create().texOffs(19, 101).mirror().addBox(0.25F, -4.5F, -2.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(0.65F, 3.5346F, -1.433F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition left_leg_r11 = left_leg.addOrReplaceChild(
            "left_leg_r11",
            CubeListBuilder.create().texOffs(19, 101).mirror().addBox(0.25F, -4.5F, -2.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(0.65F, 6.0346F, -1.433F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition left_leg_r12 = left_leg.addOrReplaceChild(
            "left_leg_r12",
            CubeListBuilder.create().texOffs(0, 101).mirror().addBox(0.25F, -4.5F, 1.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(0.65F, 3.5346F, 1.433F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition left_leg_r13 = left_leg.addOrReplaceChild(
            "left_leg_r13",
            CubeListBuilder.create().texOffs(0, 101).addBox(-1.25F, -4.5F, 1.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.3F, 3.5346F, 1.333F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition left_leg_r14 = left_leg.addOrReplaceChild(
            "left_leg_r14",
            CubeListBuilder.create().texOffs(0, 101).addBox(0.25F, -4.5F, 1.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.65F, 6.0346F, 1.433F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition left_leg_r15 = left_leg.addOrReplaceChild(
            "left_leg_r15",
            CubeListBuilder.create().texOffs(0, 101).addBox(-1.25F, -4.5F, 1.0F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.3F, 6.0346F, 1.333F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition left_leg_r16 = left_leg.addOrReplaceChild(
            "left_leg_r16",
            CubeListBuilder.create().texOffs(0, 78).mirror().addBox(-2.5F, 2.5F, -0.467F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-0.0268F, -0.2529F, -2.033F, -0.3054F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r17 = left_leg.addOrReplaceChild(
            "left_leg_r17",
            CubeListBuilder.create().texOffs(0, 78).mirror().addBox(-2.5F, 2.5F, -0.467F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-0.0268F, 1.7471F, -3.783F, 0.3054F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r18 = left_leg.addOrReplaceChild(
            "left_leg_r18",
            CubeListBuilder.create().texOffs(0, 102).mirror().addBox(-2.5F, 2.5F, -0.467F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(-0.0268F, -2.5029F, -3.033F)
         );
         PartDefinition left_leg_r19 = left_leg.addOrReplaceChild(
            "left_leg_r19",
            CubeListBuilder.create().texOffs(11, 95).mirror().addBox(-3.0F, -1.5F, -1.5F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(0.6732F, 7.0971F, 0.0F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r20 = left_leg.addOrReplaceChild(
            "left_leg_r20",
            CubeListBuilder.create().texOffs(0, 102).mirror().addBox(-2.5F, 2.5F, -0.467F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(-0.0268F, 5.3471F, -3.033F)
         );
         PartDefinition left_leg_r21 = left_leg.addOrReplaceChild(
            "left_leg_r21",
            CubeListBuilder.create().texOffs(0, 102).mirror().addBox(-2.5F, 2.5F, -0.467F, 6.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(-0.0268F, 4.0971F, -3.033F)
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
            "head",
            CubeListBuilder.create()
               .texOffs(88, 0)
               .addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(71, 9)
               .addBox(-4.0F, -9.5F, -5.25F, 2.0F, 11.0F, 11.0F, new CubeDeformation(0.0F))
               .texOffs(102, 70)
               .addBox(-5.3F, -9.5F, 2.0F, 11.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(102, 57)
               .addBox(-5.8F, -9.75F, -3.0F, 11.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(69, 33)
               .addBox(1.0F, -9.5F, -5.5F, 2.0F, 11.0F, 11.0F, new CubeDeformation(0.0F))
               .texOffs(84, 31)
               .addBox(-5.5F, -3.5F, -5.55F, 11.0F, 2.0F, 11.0F, new CubeDeformation(0.0F))
               .texOffs(84, 44)
               .addBox(-5.5F, -7.0F, -5.4F, 11.0F, 2.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition head_r1 = head.addOrReplaceChild(
            "head_r1",
            CubeListBuilder.create().texOffs(118, 0).addBox(-3.0F, -34.8247F, -4.2223F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(23.9174F, 13.7529F, -8.3746F, -0.2519F, -0.2443F, -0.7543F)
         );
         PartDefinition head_r2 = head.addOrReplaceChild(
            "head_r2",
            CubeListBuilder.create().texOffs(114, 20).addBox(-3.0F, -35.5747F, -3.4723F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(23.7406F, 13.9223F, -8.3293F, -0.2519F, -0.2443F, -0.7543F)
         );
         PartDefinition head_r3 = head.addOrReplaceChild(
            "head_r3",
            CubeListBuilder.create().texOffs(94, 1).mirror().addBox(-7.6194F, -39.0474F, 0.4112F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.0464F, -40.4121F, 17.0143F, 2.5148F, 0.3804F, -0.0759F)
         );
         PartDefinition head_r4 = head.addOrReplaceChild(
            "head_r4",
            CubeListBuilder.create()
               .texOffs(120, 5)
               .mirror()
               .addBox(-9.3332F, -33.7448F, -0.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(101, 22)
               .mirror()
               .addBox(-10.4332F, -32.4948F, -2.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offsetAndRotation(27.4391F, -1.0286F, 0.0F, 0.0F, 0.0F, -1.0908F)
         );
         PartDefinition head_r5 = head.addOrReplaceChild(
            "head_r5",
            CubeListBuilder.create().texOffs(120, 5).mirror().addBox(-9.4623F, -35.5016F, 0.6311F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(20.9154F, -2.1179F, -20.3936F, -0.5905F, 0.1693F, -1.1386F)
         );
         PartDefinition head_r6 = head.addOrReplaceChild(
            "head_r6",
            CubeListBuilder.create().texOffs(94, 1).mirror().addBox(-9.3193F, -36.7539F, 0.751F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(20.9802F, 4.3846F, -15.9731F, -0.4375F, 0.6446F, -1.1751F)
         );
         PartDefinition head_r7 = head.addOrReplaceChild(
            "head_r7",
            CubeListBuilder.create().texOffs(94, 1).mirror().addBox(-8.8037F, -36.5606F, -1.1663F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(24.0435F, -5.0253F, 17.182F, 0.6237F, 0.3336F, -0.9454F)
         );
         PartDefinition head_r8 = head.addOrReplaceChild(
            "head_r8",
            CubeListBuilder.create().texOffs(94, 4).mirror().addBox(-9.3868F, -38.034F, -0.6244F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-17.2726F, -11.4835F, 34.5168F, 1.73F, -0.4507F, -0.5033F)
         );
         PartDefinition head_r9 = head.addOrReplaceChild(
            "head_r9",
            CubeListBuilder.create().texOffs(94, 4).mirror().addBox(-10.3247F, -39.0839F, -1.0173F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-16.7634F, 8.4062F, 28.5896F, 1.3972F, -0.8338F, -0.8103F)
         );
         PartDefinition head_r10 = head.addOrReplaceChild(
            "head_r10",
            CubeListBuilder.create().texOffs(94, 1).mirror().addBox(-9.3463F, -35.8338F, -1.0439F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-17.9633F, 19.2343F, -15.155F, -0.7248F, -0.6036F, 0.9784F)
         );
         PartDefinition head_r11 = head.addOrReplaceChild(
            "head_r11",
            CubeListBuilder.create().texOffs(94, 1).mirror().addBox(-8.2004F, -35.9889F, -1.0633F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-27.2873F, 18.5891F, -6.1254F, -0.3758F, -0.6036F, 0.9784F)
         );
         PartDefinition head_r12 = head.addOrReplaceChild(
            "head_r12",
            CubeListBuilder.create().texOffs(90, 2).mirror().addBox(-6.0591F, -39.5499F, -2.336F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(4.1915F, 22.6667F, -3.8066F, 0.0751F, 0.9219F, -0.1458F)
         );
         PartDefinition head_r13 = head.addOrReplaceChild(
            "head_r13",
            CubeListBuilder.create().texOffs(94, 1).mirror().addBox(-9.4547F, -38.7144F, -0.3169F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-19.5846F, -2.9879F, -32.2164F, -1.5534F, 0.6284F, -0.5469F)
         );
         PartDefinition head_r14 = head.addOrReplaceChild(
            "head_r14",
            CubeListBuilder.create().texOffs(94, 4).mirror().addBox(-9.7277F, -37.9719F, 0.7261F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-14.6542F, 19.441F, -16.6895F, -0.5079F, 0.8084F, 1.0E-4F)
         );
         PartDefinition head_r15 = head.addOrReplaceChild(
            "head_r15",
            CubeListBuilder.create().texOffs(94, 1).mirror().addBox(-7.2549F, -35.0303F, 2.0295F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-29.1114F, -27.9053F, 22.7652F, 1.2092F, -1.0412F, 1.1195F)
         );
         PartDefinition head_r16 = head.addOrReplaceChild(
            "head_r16",
            CubeListBuilder.create().texOffs(90, 2).mirror().addBox(-4.5807F, -38.0143F, 0.7931F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-13.8446F, -0.5413F, 33.7413F, 1.5973F, -0.555F, -1.0437F)
         );
         PartDefinition head_r17 = head.addOrReplaceChild(
            "head_r17",
            CubeListBuilder.create().texOffs(94, 4).mirror().addBox(-9.1623F, -40.2696F, 0.4769F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-9.9278F, 24.4381F, 3.017F, 0.5475F, 1.0625F, 0.6509F)
         );
         PartDefinition head_r18 = head.addOrReplaceChild(
            "head_r18",
            CubeListBuilder.create().texOffs(120, 5).mirror().addBox(-9.524F, -35.2047F, -1.4286F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(27.2274F, -5.6177F, 9.5383F, 0.3235F, 0.2057F, -1.1478F)
         );
         PartDefinition head_r19 = head.addOrReplaceChild(
            "head_r19",
            CubeListBuilder.create()
               .texOffs(120, 5)
               .addBox(7.3332F, -33.7448F, -0.5F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(101, 22)
               .addBox(6.4332F, -32.4948F, -2.0F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-27.4391F, -1.0286F, 0.0F, 0.0F, 0.0F, 1.0908F)
         );
         PartDefinition head_r20 = head.addOrReplaceChild(
            "head_r20",
            CubeListBuilder.create().texOffs(120, 5).addBox(7.4623F, -35.5016F, 0.6311F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-20.9154F, -2.1179F, -20.3936F, -0.5905F, -0.1693F, 1.1386F)
         );
         PartDefinition head_r21 = head.addOrReplaceChild(
            "head_r21",
            CubeListBuilder.create().texOffs(120, 5).addBox(7.524F, -35.2047F, -1.4286F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-27.2274F, -5.6177F, 9.5383F, 0.3235F, -0.2057F, 1.1478F)
         );
         PartDefinition head_r22 = head.addOrReplaceChild(
            "head_r22",
            CubeListBuilder.create().texOffs(94, 1).addBox(7.8037F, -36.5606F, -1.1663F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-24.0435F, -5.0253F, 17.182F, 0.6237F, -0.3336F, 0.9454F)
         );
         PartDefinition head_r23 = head.addOrReplaceChild(
            "head_r23",
            CubeListBuilder.create().texOffs(94, 1).addBox(8.3193F, -36.7539F, 0.751F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-20.9802F, 4.3846F, -15.9731F, -0.4375F, -0.6446F, 1.1751F)
         );
         PartDefinition head_r24 = head.addOrReplaceChild(
            "head_r24",
            CubeListBuilder.create().texOffs(94, 1).addBox(8.4547F, -38.7144F, -0.3169F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(19.5846F, -2.9879F, -32.2164F, -1.5534F, -0.6284F, 0.5469F)
         );
         PartDefinition head_r25 = head.addOrReplaceChild(
            "head_r25",
            CubeListBuilder.create().texOffs(94, 4).addBox(8.7277F, -37.9719F, 0.7261F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(14.6542F, 19.441F, -16.6895F, -0.5079F, -0.8084F, -1.0E-4F)
         );
         PartDefinition head_r26 = head.addOrReplaceChild(
            "head_r26",
            CubeListBuilder.create().texOffs(90, 2).addBox(3.5807F, -38.0143F, 0.7931F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(13.8446F, -0.5413F, 33.7413F, 1.5973F, 0.555F, 1.0437F)
         );
         PartDefinition head_r27 = head.addOrReplaceChild(
            "head_r27",
            CubeListBuilder.create().texOffs(94, 4).addBox(8.1623F, -40.2696F, 0.4769F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(9.9278F, 24.4381F, 3.017F, 0.5475F, -1.0625F, -0.6509F)
         );
         PartDefinition head_r28 = head.addOrReplaceChild(
            "head_r28",
            CubeListBuilder.create().texOffs(94, 1).addBox(6.2549F, -35.0303F, 2.0295F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(29.1114F, -27.9053F, 22.7652F, 1.2092F, 1.0412F, -1.1195F)
         );
         PartDefinition head_r29 = head.addOrReplaceChild(
            "head_r29",
            CubeListBuilder.create().texOffs(94, 1).addBox(8.3463F, -35.8338F, -1.0439F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(17.9633F, 19.2343F, -15.155F, -0.7248F, 0.6036F, -0.9784F)
         );
         PartDefinition head_r30 = head.addOrReplaceChild(
            "head_r30",
            CubeListBuilder.create().texOffs(90, 2).addBox(5.0591F, -39.5499F, -2.336F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.1915F, 22.6667F, -3.8066F, 0.0751F, -0.9219F, 0.1458F)
         );
         PartDefinition head_r31 = head.addOrReplaceChild(
            "head_r31",
            CubeListBuilder.create().texOffs(94, 4).addBox(8.3868F, -38.034F, -0.6244F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(17.2726F, -11.4835F, 34.5168F, 1.73F, 0.4507F, 0.5033F)
         );
         PartDefinition head_r32 = head.addOrReplaceChild(
            "head_r32",
            CubeListBuilder.create().texOffs(94, 1).addBox(6.6194F, -39.0474F, 0.4112F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0464F, -40.4121F, 17.0143F, 2.5148F, -0.3804F, 0.0759F)
         );
         PartDefinition head_r33 = head.addOrReplaceChild(
            "head_r33",
            CubeListBuilder.create().texOffs(94, 4).addBox(9.3247F, -39.0839F, -1.0173F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(16.7634F, 8.4062F, 28.5896F, 1.3972F, 0.8338F, 0.8103F)
         );
         PartDefinition head_r34 = head.addOrReplaceChild(
            "head_r34",
            CubeListBuilder.create().texOffs(94, 1).addBox(7.2004F, -35.9889F, -1.0633F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(27.2873F, 18.5891F, -6.1254F, -0.3758F, 0.6036F, -0.9784F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(96, 110)
               .addBox(-5.0F, -1.0F, -3.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(96, 110)
               .addBox(-5.0F, -1.3F, -3.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(-0.5F))
               .texOffs(122, 101)
               .addBox(-4.5F, 0.5F, -4.25F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(122, 101)
               .addBox(-4.5F, 0.5F, 3.25F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body_r1 = body.addOrReplaceChild(
            "body_r1",
            CubeListBuilder.create()
               .texOffs(112, 100)
               .addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(112, 100)
               .addBox(-1.5F, -1.5F, -7.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, 1.5F, 3.5F, 0.0F, 0.0F, 1.1781F)
         );
         PartDefinition body_r2 = body.addOrReplaceChild(
            "body_r2",
            CubeListBuilder.create()
               .texOffs(112, 100)
               .addBox(-1.5F, -1.5F, -0.75F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(112, 100)
               .addBox(-1.5F, -1.5F, -7.25F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, 1.5F, 3.5F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition body_r3 = body.addOrReplaceChild(
            "body_r3",
            CubeListBuilder.create().texOffs(109, 96).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0573F, 7.8623F, 2.8318F, -0.6088F, 0.1517F, -1.6538F)
         );
         PartDefinition body_r4 = body.addOrReplaceChild(
            "body_r4",
            CubeListBuilder.create().texOffs(109, 96).addBox(-1.25F, 0.25F, -0.35F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.9922F, 10.1929F, 2.85F, -0.4028F, 0.2626F, -1.5957F)
         );
         PartDefinition body_r5 = body.addOrReplaceChild(
            "body_r5",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0537F, 9.3264F, 2.8F, 0.0F, 0.2618F, -1.0036F)
         );
         PartDefinition body_r6 = body.addOrReplaceChild(
            "body_r6",
            CubeListBuilder.create().texOffs(116, 94).addBox(-1.0F, -1.5F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.5824F, 7.2169F, 2.9F, 0.0F, 0.2618F, -1.1781F)
         );
         PartDefinition body_r7 = body.addOrReplaceChild(
            "body_r7",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.1694F, 5.9657F, 3.0F, 0.0F, 0.2618F, -1.0472F)
         );
         PartDefinition body_r8 = body.addOrReplaceChild(
            "body_r8",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0239F, 7.0735F, 2.9F, 0.0F, 0.2618F, -0.8727F)
         );
         PartDefinition body_r9 = body.addOrReplaceChild(
            "body_r9",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.1031F, 4.3527F, 3.0F, 0.0F, 0.2618F, -0.4363F)
         );
         PartDefinition body_r10 = body.addOrReplaceChild(
            "body_r10",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.3977F, 3.5555F, 3.1F, 0.0F, 0.2618F, -0.6109F)
         );
         PartDefinition body_r11 = body.addOrReplaceChild(
            "body_r11",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.3403F, 2.6075F, 3.2F, 0.0F, 0.2618F, -0.7854F)
         );
         PartDefinition body_r12 = body.addOrReplaceChild(
            "body_r12",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.689F, 4.5368F, 3.1F, 0.0F, 0.2618F, -1.2217F)
         );
         PartDefinition body_r13 = body.addOrReplaceChild(
            "body_r13",
            CubeListBuilder.create().texOffs(109, 96).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.7422F, 5.2929F, 3.0F, -0.0901F, 0.2577F, -1.5332F)
         );
         PartDefinition body_r14 = body.addOrReplaceChild(
            "body_r14",
            CubeListBuilder.create().texOffs(109, 96).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.4838F, 5.3647F, 2.8393F, -0.475F, 0.1261F, -1.7235F)
         );
         PartDefinition body_r15 = body.addOrReplaceChild(
            "body_r15",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.3403F, 2.6075F, -3.2F, 0.0F, -0.2618F, -0.7854F)
         );
         PartDefinition body_r16 = body.addOrReplaceChild(
            "body_r16",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.689F, 4.5368F, -3.1F, 0.0F, -0.2618F, -1.2217F)
         );
         PartDefinition body_r17 = body.addOrReplaceChild(
            "body_r17",
            CubeListBuilder.create().texOffs(109, 96).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0573F, 7.8623F, -2.8318F, 0.6088F, -0.1517F, -1.6538F)
         );
         PartDefinition body_r18 = body.addOrReplaceChild(
            "body_r18",
            CubeListBuilder.create().texOffs(109, 96).addBox(-1.25F, 0.25F, -0.65F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.9922F, 10.1929F, -2.85F, 0.4028F, -0.2626F, -1.5957F)
         );
         PartDefinition body_r19 = body.addOrReplaceChild(
            "body_r19",
            CubeListBuilder.create().texOffs(109, 96).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.4838F, 5.3647F, -2.8393F, 0.475F, -0.1261F, -1.7235F)
         );
         PartDefinition body_r20 = body.addOrReplaceChild(
            "body_r20",
            CubeListBuilder.create().texOffs(109, 96).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.7422F, 5.2929F, -3.0F, 0.0901F, -0.2577F, -1.5332F)
         );
         PartDefinition body_r21 = body.addOrReplaceChild(
            "body_r21",
            CubeListBuilder.create().texOffs(114, 98).addBox(-5.5F, -1.0F, -3.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(9.7795F, 9.0349F, 0.2F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition body_r22 = body.addOrReplaceChild(
            "body_r22",
            CubeListBuilder.create().texOffs(114, 98).addBox(-5.5F, -1.0F, -3.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(10.0295F, 6.6349F, 0.2F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition body_r23 = body.addOrReplaceChild(
            "body_r23",
            CubeListBuilder.create().texOffs(114, 98).mirror().addBox(-0.5F, -1.0F, -3.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(4.9999F, 6.779F, 0.0F, 0.0F, 0.0F, -0.48F)
         );
         PartDefinition body_r24 = body.addOrReplaceChild(
            "body_r24",
            CubeListBuilder.create().texOffs(114, 98).mirror().addBox(-0.5F, -1.0F, -3.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(4.9999F, 9.179F, 0.0F, 0.0F, 0.0F, -0.48F)
         );
         PartDefinition body_r25 = body.addOrReplaceChild(
            "body_r25",
            CubeListBuilder.create().texOffs(114, 98).addBox(-5.5F, -1.0F, -3.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(9.7795F, 4.1849F, 0.2F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition body_r26 = body.addOrReplaceChild(
            "body_r26",
            CubeListBuilder.create().texOffs(116, 94).addBox(-1.0F, -1.5F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.5824F, 7.2169F, -2.9F, 0.0F, -0.2618F, -1.1781F)
         );
         PartDefinition body_r27 = body.addOrReplaceChild(
            "body_r27",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.1694F, 5.9657F, -3.0F, 0.0F, -0.2618F, -1.0472F)
         );
         PartDefinition body_r28 = body.addOrReplaceChild(
            "body_r28",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.3977F, 3.5555F, -3.1F, 0.0F, -0.2618F, -0.6109F)
         );
         PartDefinition body_r29 = body.addOrReplaceChild(
            "body_r29",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0537F, 9.3264F, -2.8F, 0.0F, -0.2618F, -1.0036F)
         );
         PartDefinition body_r30 = body.addOrReplaceChild(
            "body_r30",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0239F, 7.0735F, -2.9F, 0.0F, -0.2618F, -0.8727F)
         );
         PartDefinition body_r31 = body.addOrReplaceChild(
            "body_r31",
            CubeListBuilder.create().texOffs(122, 83).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.1031F, 4.3527F, -3.0F, 0.0F, -0.2618F, -0.4363F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(48, 112)
               .addBox(-3.65F, 6.7F, -2.4F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(48, 112)
               .addBox(-3.65F, 7.95F, -2.4F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(63, 93)
               .addBox(-3.65F, 0.45F, -2.4F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_arm_r1 = right_arm.addOrReplaceChild(
            "right_arm_r1",
            CubeListBuilder.create().texOffs(49, 118).addBox(-0.3356F, -0.5979F, -0.661F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.8834F, -5.1284F, -1.1755F, 0.8172F, -0.8349F, 1.3677F)
         );
         PartDefinition right_arm_r2 = right_arm.addOrReplaceChild(
            "right_arm_r2",
            CubeListBuilder.create().texOffs(49, 114).addBox(0.683F, -1.1041F, -0.9313F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.2366F, -6.9338F, 0.1F, 0.6001F, 0.083F, 0.7782F)
         );
         PartDefinition right_arm_r3 = right_arm.addOrReplaceChild(
            "right_arm_r3",
            CubeListBuilder.create().texOffs(49, 114).addBox(-0.6969F, -2.3208F, -0.3293F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5764F, -3.6083F, 0.1F, -0.3494F, 0.5119F, -1.294F)
         );
         PartDefinition right_arm_r4 = right_arm.addOrReplaceChild(
            "right_arm_r4",
            CubeListBuilder.create().texOffs(49, 114).addBox(-1.3397F, -0.9403F, 0.0948F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.2366F, -6.9338F, 0.1F, -0.48F, 0.0F, -0.6108F)
         );
         PartDefinition right_arm_r5 = right_arm.addOrReplaceChild(
            "right_arm_r5",
            CubeListBuilder.create().texOffs(49, 114).addBox(-0.5866F, -1.9747F, -0.1759F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-8.2072F, -0.6112F, 0.1F, -0.2613F, -0.3535F, -1.8467F)
         );
         PartDefinition right_arm_r6 = right_arm.addOrReplaceChild(
            "right_arm_r6",
            CubeListBuilder.create().texOffs(49, 114).addBox(-0.6969F, -2.3208F, -0.3293F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5764F, -3.6083F, 0.1F, 0.49F, -0.1925F, 0.0826F)
         );
         PartDefinition right_arm_r7 = right_arm.addOrReplaceChild(
            "right_arm_r7",
            CubeListBuilder.create().texOffs(49, 121).addBox(-13.2349F, -25.5859F, 0.0713F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-20.6804F, 13.3622F, -14.9881F, -0.4784F, 0.9598F, 0.5456F)
         );
         PartDefinition right_arm_r8 = right_arm.addOrReplaceChild(
            "right_arm_r8",
            CubeListBuilder.create().texOffs(49, 121).addBox(-0.2127F, -0.623F, -0.2454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5963F, -4.094F, -1.7989F, -0.6543F, -0.0841F, 1.7366F)
         );
         PartDefinition right_arm_r9 = right_arm.addOrReplaceChild(
            "right_arm_r9",
            CubeListBuilder.create().texOffs(49, 118).addBox(0.7445F, -2.2562F, -0.0691F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.372F, -4.9876F, 0.1F, 2.9358F, 0.284F, 2.4575F)
         );
         PartDefinition right_arm_r10 = right_arm.addOrReplaceChild(
            "right_arm_r10",
            CubeListBuilder.create().texOffs(49, 118).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.8632F, -5.0215F, 0.1F, 0.0F, 0.0F, 0.6109F)
         );
         PartDefinition right_arm_r11 = right_arm.addOrReplaceChild(
            "right_arm_r11",
            CubeListBuilder.create().texOffs(49, 121).addBox(0.234F, -0.6524F, -0.5245F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5807F, -5.9779F, 0.1F, -1.7303F, -1.3635F, 1.8882F)
         );
         PartDefinition right_arm_r12 = right_arm.addOrReplaceChild(
            "right_arm_r12",
            CubeListBuilder.create().texOffs(49, 118).addBox(-2.3736F, -2.1501F, -1.5921F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.8759F, -3.4953F, 1.1442F, -1.5319F, -0.2222F, -0.1793F)
         );
         PartDefinition right_arm_r13 = right_arm.addOrReplaceChild(
            "right_arm_r13",
            CubeListBuilder.create().texOffs(49, 118).addBox(-10.6419F, -27.7578F, -1.1089F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.4726F, 10.3955F, 23.7926F, 0.9675F, -0.3481F, -0.1011F)
         );
         PartDefinition right_arm_r14 = right_arm.addOrReplaceChild(
            "right_arm_r14",
            CubeListBuilder.create().texOffs(49, 114).addBox(-11.2441F, -26.8225F, -0.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(11.485F, 18.5535F, 0.0F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition right_arm_r15 = right_arm.addOrReplaceChild(
            "right_arm_r15",
            CubeListBuilder.create().texOffs(49, 121).addBox(-14.3631F, -24.7777F, 0.7084F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-10.978F, 20.7615F, -14.6449F, -0.2911F, 1.0075F, 0.168F)
         );
         PartDefinition right_arm_r16 = right_arm.addOrReplaceChild(
            "right_arm_r16",
            CubeListBuilder.create().texOffs(49, 121).addBox(-0.2345F, 0.0797F, -0.1051F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-9.5266F, 0.0855F, -0.5017F, -1.129F, -0.9288F, 0.6326F)
         );
         PartDefinition right_arm_r17 = right_arm.addOrReplaceChild(
            "right_arm_r17",
            CubeListBuilder.create().texOffs(49, 118).addBox(-0.886F, -0.4982F, 1.053F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-10.4201F, -1.8175F, 0.1F, 0.3927F, -0.8727F, -1.6581F)
         );
         PartDefinition right_arm_r18 = right_arm.addOrReplaceChild(
            "right_arm_r18",
            CubeListBuilder.create().texOffs(49, 118).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-8.6049F, -2.0368F, 0.1F, 0.3736F, -0.7347F, -0.0928F)
         );
         PartDefinition right_arm_r19 = right_arm.addOrReplaceChild(
            "right_arm_r19",
            CubeListBuilder.create().texOffs(49, 121).addBox(-0.354F, -0.8549F, -0.1795F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-8.8757F, -2.9463F, 0.1F, 0.9911F, -0.6932F, -0.3895F)
         );
         PartDefinition right_arm_r20 = right_arm.addOrReplaceChild(
            "right_arm_r20",
            CubeListBuilder.create().texOffs(49, 118).addBox(-14.0613F, -24.5251F, 0.6084F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.6114F, 9.7252F, -22.6236F, -0.9013F, 0.602F, -0.6409F)
         );
         PartDefinition right_arm_r21 = right_arm.addOrReplaceChild(
            "right_arm_r21",
            CubeListBuilder.create().texOffs(49, 118).addBox(1.242F, -0.1306F, 0.0216F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.8977F, -1.0763F, -0.6425F, -0.8326F, -0.4531F, 1.7451F)
         );
         PartDefinition right_arm_r22 = right_arm.addOrReplaceChild(
            "right_arm_r22",
            CubeListBuilder.create().texOffs(49, 114).addBox(-14.5702F, -24.0249F, -0.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(18.6707F, 1.7755F, 0.0F, 0.0F, 0.0F, -0.9337F)
         );
         PartDefinition right_arm_r23 = right_arm.addOrReplaceChild(
            "right_arm_r23",
            CubeListBuilder.create().texOffs(49, 121).addBox(-1.1149F, -1.1652F, -0.7334F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.8834F, -5.3784F, 1.6255F, -0.7751F, 0.3681F, 0.309F)
         );
         PartDefinition right_arm_r24 = right_arm.addOrReplaceChild(
            "right_arm_r24",
            CubeListBuilder.create().texOffs(49, 118).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.1018F, -6.9094F, 0.1F, -0.263F, 0.5086F, 0.4112F)
         );
         PartDefinition right_arm_r25 = right_arm.addOrReplaceChild(
            "right_arm_r25",
            CubeListBuilder.create().texOffs(49, 118).addBox(-0.9463F, -1.7479F, -0.4313F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.1067F, -6.8069F, -0.15F, 0.7484F, -1.1436F, -1.5956F)
         );
         PartDefinition right_arm_r26 = right_arm.addOrReplaceChild(
            "right_arm_r26",
            CubeListBuilder.create().texOffs(49, 118).addBox(-0.7709F, -2.2982F, -1.5266F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.612F, -5.0083F, 1.0969F, -1.1108F, -0.4842F, -0.5326F)
         );
         PartDefinition right_arm_r27 = right_arm.addOrReplaceChild(
            "right_arm_r27",
            CubeListBuilder.create().texOffs(49, 118).addBox(-0.9245F, -0.9814F, -1.5639F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.612F, -5.0083F, -0.8969F, 2.0644F, -0.0468F, 1.3657F)
         );
         PartDefinition right_arm_r28 = right_arm.addOrReplaceChild(
            "right_arm_r28",
            CubeListBuilder.create().texOffs(49, 114).addBox(-7.7642F, -27.9748F, -0.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.1965F, 21.6987F, 0.0F, 0.0F, 0.0F, -0.0436F)
         );
         PartDefinition right_arm_r29 = right_arm.addOrReplaceChild(
            "right_arm_r29",
            CubeListBuilder.create().texOffs(40, 119).addBox(-10.7106F, -24.9698F, -1.401F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(18.167F, -14.8747F, 0.0F, 0.0F, 0.0F, -1.6581F)
         );
         PartDefinition right_arm_r30 = right_arm.addOrReplaceChild(
            "right_arm_r30",
            CubeListBuilder.create().texOffs(40, 119).addBox(-12.9543F, -22.5601F, -1.401F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(12.5234F, -15.6373F, 0.0F, 0.0F, 0.0F, -1.7191F)
         );
         PartDefinition right_arm_r31 = right_arm.addOrReplaceChild(
            "right_arm_r31",
            CubeListBuilder.create().texOffs(36, 123).addBox(-0.3471F, -43.7482F, -1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(29.455F, 23.3209F, 0.0F, 0.0F, 0.0F, -1.021F)
         );
         PartDefinition right_arm_r32 = right_arm.addOrReplaceChild(
            "right_arm_r32",
            CubeListBuilder.create().texOffs(36, 123).addBox(-10.8853F, -24.8152F, -1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(19.7199F, 6.8245F, 0.0F, 0.0F, 0.0F, -0.829F)
         );
         PartDefinition right_arm_r33 = right_arm.addOrReplaceChild(
            "right_arm_r33",
            CubeListBuilder.create().texOffs(41, 108).addBox(-10.5657F, -20.3615F, -1.4F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(13.4237F, 14.2147F, 0.0F, 0.0F, 0.0F, -0.5236F)
         );
         PartDefinition right_arm_r34 = right_arm.addOrReplaceChild(
            "right_arm_r34",
            CubeListBuilder.create().texOffs(38, 115).addBox(-13.469F, -20.8846F, -0.837F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(14.4252F, 4.1753F, -8.8474F, -0.0011F, 0.7939F, -1.0584F)
         );
         PartDefinition right_arm_r35 = right_arm.addOrReplaceChild(
            "right_arm_r35",
            CubeListBuilder.create()
               .texOffs(46, 99)
               .addBox(2.8105F, -39.0551F, -2.4F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(68, 117)
               .addBox(3.5605F, -38.6551F, -2.9F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(20.0929F, 28.3552F, 0.0F, 0.0F, 0.0F, -0.8727F)
         );
         PartDefinition right_arm_r36 = right_arm.addOrReplaceChild(
            "right_arm_r36",
            CubeListBuilder.create().texOffs(40, 119).addBox(-7.5942F, -26.9254F, -1.401F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(24.4784F, 1.4498F, 0.0F, 0.0F, 0.0F, -1.0908F)
         );
         PartDefinition right_arm_r37 = right_arm.addOrReplaceChild(
            "right_arm_r37",
            CubeListBuilder.create().texOffs(36, 123).addBox(-8.5324F, -26.1959F, -1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(13.7239F, 17.5811F, 0.0F, 0.0F, 0.0F, -0.3491F)
         );
         PartDefinition right_arm_r38 = right_arm.addOrReplaceChild(
            "right_arm_r38",
            CubeListBuilder.create()
               .texOffs(63, 105)
               .addBox(-2.4461F, -28.0648F, -3.4F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(49, 105)
               .addBox(-2.4461F, -28.5648F, -2.9F, 4.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5898F, 23.4505F, 0.0F, 0.0F, 0.0F, -0.2618F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(63, 93)
               .mirror()
               .addBox(-1.1F, 0.45F, -2.4F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(48, 112)
               .addBox(-1.15F, 6.7F, -2.4F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(48, 112)
               .addBox(-1.15F, 7.95F, -2.4F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm_r1 = left_arm.addOrReplaceChild(
            "left_arm_r1",
            CubeListBuilder.create().texOffs(49, 121).mirror().addBox(12.2349F, -25.5859F, 0.0713F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(20.6804F, 13.3622F, -14.9881F, -0.4784F, -0.9598F, -0.5456F)
         );
         PartDefinition left_arm_r2 = left_arm.addOrReplaceChild(
            "left_arm_r2",
            CubeListBuilder.create().texOffs(49, 121).mirror().addBox(-0.7873F, -0.623F, -0.2454F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.5963F, -4.094F, -1.7989F, -0.6543F, 0.0841F, -1.7366F)
         );
         PartDefinition left_arm_r3 = left_arm.addOrReplaceChild(
            "left_arm_r3",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-1.7445F, -2.2562F, -0.0691F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.372F, -4.9876F, 0.1F, 2.9358F, -0.284F, -2.4575F)
         );
         PartDefinition left_arm_r4 = left_arm.addOrReplaceChild(
            "left_arm_r4",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(4.8632F, -5.0215F, 0.1F, 0.0F, 0.0F, -0.6109F)
         );
         PartDefinition left_arm_r5 = left_arm.addOrReplaceChild(
            "left_arm_r5",
            CubeListBuilder.create().texOffs(49, 121).mirror().addBox(-1.234F, -0.6524F, -0.5245F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(4.5807F, -5.9779F, 0.1F, -1.7303F, 1.3635F, -1.8882F)
         );
         PartDefinition left_arm_r6 = left_arm.addOrReplaceChild(
            "left_arm_r6",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(1.3736F, -2.1501F, -1.5921F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(4.8759F, -3.4953F, 1.1442F, -1.5319F, 0.2222F, 0.1793F)
         );
         PartDefinition left_arm_r7 = left_arm.addOrReplaceChild(
            "left_arm_r7",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(9.6419F, -27.7578F, -1.1089F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(1.4726F, 10.3955F, 23.7926F, 0.9675F, 0.3481F, 0.1011F)
         );
         PartDefinition left_arm_r8 = left_arm.addOrReplaceChild(
            "left_arm_r8",
            CubeListBuilder.create().texOffs(49, 114).mirror().addBox(10.2441F, -26.8225F, -0.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-11.485F, 18.5535F, 0.0F, 0.0F, 0.0F, 0.2618F)
         );
         PartDefinition left_arm_r9 = left_arm.addOrReplaceChild(
            "left_arm_r9",
            CubeListBuilder.create().texOffs(49, 114).mirror().addBox(-0.3031F, -2.3208F, -0.3293F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.5764F, -3.6083F, 0.1F, 0.49F, 0.1925F, -0.0826F)
         );
         PartDefinition left_arm_r10 = left_arm.addOrReplaceChild(
            "left_arm_r10",
            CubeListBuilder.create().texOffs(49, 114).mirror().addBox(-0.3031F, -2.3208F, -0.3293F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(5.5764F, -3.6083F, 0.1F, -0.3494F, -0.5119F, 1.294F)
         );
         PartDefinition left_arm_r11 = left_arm.addOrReplaceChild(
            "left_arm_r11",
            CubeListBuilder.create().texOffs(40, 119).mirror().addBox(9.7106F, -24.9698F, -1.401F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-18.167F, -14.8747F, 0.0F, 0.0F, 0.0F, 1.6581F)
         );
         PartDefinition left_arm_r12 = left_arm.addOrReplaceChild(
            "left_arm_r12",
            CubeListBuilder.create().texOffs(49, 121).mirror().addBox(13.3631F, -24.7777F, 0.7084F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(10.978F, 20.7615F, -14.6449F, -0.2911F, -1.0075F, -0.168F)
         );
         PartDefinition left_arm_r13 = left_arm.addOrReplaceChild(
            "left_arm_r13",
            CubeListBuilder.create().texOffs(49, 121).mirror().addBox(-0.7655F, 0.0797F, -0.1051F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(9.5266F, 0.0855F, -0.5017F, -1.129F, 0.9288F, -0.6326F)
         );
         PartDefinition left_arm_r14 = left_arm.addOrReplaceChild(
            "left_arm_r14",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-0.114F, -0.4982F, 1.053F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(10.4201F, -1.8175F, 0.1F, 0.3927F, 0.8727F, 1.6581F)
         );
         PartDefinition left_arm_r15 = left_arm.addOrReplaceChild(
            "left_arm_r15",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(8.6049F, -2.0368F, 0.1F, 0.3736F, 0.7347F, 0.0928F)
         );
         PartDefinition left_arm_r16 = left_arm.addOrReplaceChild(
            "left_arm_r16",
            CubeListBuilder.create().texOffs(49, 121).mirror().addBox(-0.646F, -0.8549F, -0.1795F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(8.8757F, -2.9463F, 0.1F, 0.9911F, 0.6932F, 0.3895F)
         );
         PartDefinition left_arm_r17 = left_arm.addOrReplaceChild(
            "left_arm_r17",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(13.0613F, -24.5251F, 0.6084F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-0.6114F, 9.7252F, -22.6236F, -0.9013F, -0.602F, 0.6409F)
         );
         PartDefinition left_arm_r18 = left_arm.addOrReplaceChild(
            "left_arm_r18",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-2.242F, -0.1306F, 0.0216F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(7.8977F, -1.0763F, -0.6425F, -0.8326F, 0.4531F, -1.7451F)
         );
         PartDefinition left_arm_r19 = left_arm.addOrReplaceChild(
            "left_arm_r19",
            CubeListBuilder.create().texOffs(49, 114).mirror().addBox(13.5702F, -24.0249F, -0.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-18.6707F, 1.7755F, 0.0F, 0.0F, 0.0F, 0.9337F)
         );
         PartDefinition left_arm_r20 = left_arm.addOrReplaceChild(
            "left_arm_r20",
            CubeListBuilder.create().texOffs(49, 114).mirror().addBox(-0.4134F, -1.9747F, -0.1759F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(8.2072F, -0.6112F, 0.1F, -0.2613F, 0.3535F, 1.8467F)
         );
         PartDefinition left_arm_r21 = left_arm.addOrReplaceChild(
            "left_arm_r21",
            CubeListBuilder.create().texOffs(40, 119).mirror().addBox(11.9543F, -22.5601F, -1.401F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-12.5234F, -15.6373F, 0.0F, 0.0F, 0.0F, 1.7191F)
         );
         PartDefinition left_arm_r22 = left_arm.addOrReplaceChild(
            "left_arm_r22",
            CubeListBuilder.create().texOffs(36, 123).mirror().addBox(-1.6529F, -43.7482F, -1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-29.455F, 23.3209F, 0.0F, 0.0F, 0.0F, 1.021F)
         );
         PartDefinition left_arm_r23 = left_arm.addOrReplaceChild(
            "left_arm_r23",
            CubeListBuilder.create().texOffs(36, 123).mirror().addBox(8.8853F, -24.8152F, -1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-19.7199F, 6.8245F, 0.0F, 0.0F, 0.0F, 0.829F)
         );
         PartDefinition left_arm_r24 = left_arm.addOrReplaceChild(
            "left_arm_r24",
            CubeListBuilder.create().texOffs(41, 108).mirror().addBox(9.5657F, -20.3615F, -1.4F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-13.4237F, 14.2147F, 0.0F, 0.0F, 0.0F, 0.5236F)
         );
         PartDefinition left_arm_r25 = left_arm.addOrReplaceChild(
            "left_arm_r25",
            CubeListBuilder.create().texOffs(38, 115).mirror().addBox(10.469F, -20.8846F, -0.837F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-14.4252F, 4.1753F, -8.8474F, -0.0011F, -0.7939F, 1.0584F)
         );
         PartDefinition left_arm_r26 = left_arm.addOrReplaceChild(
            "left_arm_r26",
            CubeListBuilder.create()
               .texOffs(46, 99)
               .mirror()
               .addBox(-8.8105F, -39.0551F, -2.4F, 6.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(68, 117)
               .mirror()
               .addBox(-8.5605F, -38.6551F, -2.9F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offsetAndRotation(-20.0929F, 28.3552F, 0.0F, 0.0F, 0.0F, 0.8727F)
         );
         PartDefinition left_arm_r27 = left_arm.addOrReplaceChild(
            "left_arm_r27",
            CubeListBuilder.create().texOffs(49, 121).mirror().addBox(0.1149F, -1.1652F, -0.7334F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(2.8834F, -5.3784F, 1.6255F, -0.7751F, -0.3681F, -0.309F)
         );
         PartDefinition left_arm_r28 = left_arm.addOrReplaceChild(
            "left_arm_r28",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-0.6644F, -0.5979F, -0.661F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(2.8834F, -5.1284F, -1.1755F, 0.8172F, 0.8349F, -1.3677F)
         );
         PartDefinition left_arm_r29 = left_arm.addOrReplaceChild(
            "left_arm_r29",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(2.1018F, -6.9094F, 0.1F, -0.263F, -0.5086F, -0.4112F)
         );
         PartDefinition left_arm_r30 = left_arm.addOrReplaceChild(
            "left_arm_r30",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-0.0537F, -1.7479F, -0.4313F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(1.1067F, -6.8069F, -0.15F, 0.7484F, 1.1436F, 1.5956F)
         );
         PartDefinition left_arm_r31 = left_arm.addOrReplaceChild(
            "left_arm_r31",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-0.2291F, -2.2982F, -1.5266F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(1.612F, -5.0083F, 1.0969F, -1.1108F, 0.4842F, 0.5326F)
         );
         PartDefinition left_arm_r32 = left_arm.addOrReplaceChild(
            "left_arm_r32",
            CubeListBuilder.create().texOffs(49, 118).mirror().addBox(-0.0755F, -0.9814F, -1.5639F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(1.612F, -5.0083F, -0.8969F, 2.0644F, 0.0468F, -1.3657F)
         );
         PartDefinition left_arm_r33 = left_arm.addOrReplaceChild(
            "left_arm_r33",
            CubeListBuilder.create().texOffs(49, 114).mirror().addBox(6.7642F, -27.9748F, -0.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-6.1965F, 21.6987F, 0.0F, 0.0F, 0.0F, 0.0436F)
         );
         PartDefinition left_arm_r34 = left_arm.addOrReplaceChild(
            "left_arm_r34",
            CubeListBuilder.create().texOffs(49, 114).mirror().addBox(0.3397F, -0.9403F, 0.0948F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(2.2366F, -6.9338F, 0.1F, -0.48F, 0.0F, 0.6108F)
         );
         PartDefinition left_arm_r35 = left_arm.addOrReplaceChild(
            "left_arm_r35",
            CubeListBuilder.create().texOffs(49, 114).mirror().addBox(-1.683F, -1.1041F, -0.9313F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(2.2366F, -6.9338F, 0.1F, 0.6001F, -0.083F, -0.7782F)
         );
         PartDefinition left_arm_r36 = left_arm.addOrReplaceChild(
            "left_arm_r36",
            CubeListBuilder.create().texOffs(40, 119).mirror().addBox(6.5942F, -26.9254F, -1.401F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-24.4784F, 1.4498F, 0.0F, 0.0F, 0.0F, 1.0908F)
         );
         PartDefinition left_arm_r37 = left_arm.addOrReplaceChild(
            "left_arm_r37",
            CubeListBuilder.create().texOffs(36, 123).mirror().addBox(6.5324F, -26.1959F, -1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-13.7239F, 17.5811F, 0.0F, 0.0F, 0.0F, 0.3491F)
         );
         PartDefinition left_arm_r38 = left_arm.addOrReplaceChild(
            "left_arm_r38",
            CubeListBuilder.create()
               .texOffs(49, 105)
               .mirror()
               .addBox(-1.5539F, -28.5648F, -2.9F, 4.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(63, 105)
               .mirror()
               .addBox(-2.5539F, -28.0648F, -3.4F, 5.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offsetAndRotation(-5.5898F, 23.4505F, 0.0F, 0.0F, 0.0F, 0.2618F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-3.478F, 9.16F, -3.515F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(18, 6)
               .addBox(-3.728F, 8.66F, -3.94F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(19, 3)
               .addBox(-3.978F, 8.66F, -3.69F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 21)
               .addBox(-3.978F, 11.41F, 1.14F, 7.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 21)
               .addBox(-3.978F, 11.41F, -3.36F, 7.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(19, 0)
               .addBox(-3.978F, 8.66F, 2.81F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 15)
               .addBox(-2.578F, 12.16F, -4.81F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 15)
               .addBox(-0.978F, 12.16F, -4.81F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 15)
               .addBox(0.622F, 12.16F, -4.81F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 11)
               .addBox(1.772F, 8.66F, -3.94F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 11.0F, 0.0F)
         );
         PartDefinition right_leg_r1 = right_leg.addOrReplaceChild(
            "right_leg_r1",
            CubeListBuilder.create().texOffs(19, 7).addBox(-0.5F, -0.5F, -4.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.728F, 10.16F, 0.56F, 0.0F, 0.0F, 1.5708F)
         );
         PartDefinition right_leg_r2 = right_leg.addOrReplaceChild(
            "right_leg_r2",
            CubeListBuilder.create().texOffs(20, 0).addBox(-2.5F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.978F, 10.16F, -3.69F, 1.5708F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r3 = right_leg.addOrReplaceChild(
            "right_leg_r3",
            CubeListBuilder.create().texOffs(19, 16).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.772F, 10.16F, -0.44F, 0.0F, 0.0F, -1.5708F)
         );
         PartDefinition right_leg_r4 = right_leg.addOrReplaceChild(
            "right_leg_r4",
            CubeListBuilder.create().texOffs(20, 0).addBox(-3.5F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.022F, 10.16F, 3.81F, 1.5708F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r5 = right_leg.addOrReplaceChild(
            "right_leg_r5",
            CubeListBuilder.create()
               .texOffs(12, 15)
               .addBox(-2.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 15)
               .addBox(-4.0F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 15)
               .addBox(-5.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.022F, 12.26F, -4.01F, -0.8727F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r6 = right_leg.addOrReplaceChild(
            "right_leg_r6",
            CubeListBuilder.create().texOffs(4, 13).addBox(-2.5F, -0.5F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.478F, 12.477F, -2.9386F, -0.3054F, 0.0F, 0.0F)
         );
         PartDefinition right_leg_r7 = right_leg.addOrReplaceChild(
            "right_leg_r7",
            CubeListBuilder.create().texOffs(4, 11).addBox(-3.5F, -1.0F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.522F, 11.81F, -3.61F, -0.6981F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .mirror()
               .addBox(-2.522F, 9.16F, -3.54F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(18, 6)
               .mirror()
               .addBox(2.728F, 8.66F, -4.04F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(19, 3)
               .mirror()
               .addBox(-3.022F, 8.66F, -3.79F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(0, 21)
               .mirror()
               .addBox(-3.022F, 11.41F, 1.04F, 7.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(0, 21)
               .mirror()
               .addBox(-3.022F, 11.41F, -3.46F, 7.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(19, 0)
               .mirror()
               .addBox(-3.022F, 8.66F, 2.71F, 7.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(8, 15)
               .mirror()
               .addBox(1.578F, 12.16F, -4.91F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(8, 15)
               .mirror()
               .addBox(-0.022F, 12.16F, -4.91F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(8, 15)
               .mirror()
               .addBox(-1.622F, 12.16F, -4.91F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(8, 11)
               .mirror()
               .addBox(-2.772F, 8.66F, -4.04F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offset(1.9F, 11.0F, 0.0F)
         );
         PartDefinition left_leg_r1 = left_leg.addOrReplaceChild(
            "left_leg_r1",
            CubeListBuilder.create().texOffs(19, 7).mirror().addBox(-0.5F, -0.5F, -4.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(3.728F, 10.16F, 0.46F, 0.0F, 0.0F, -1.5708F)
         );
         PartDefinition left_leg_r2 = left_leg.addOrReplaceChild(
            "left_leg_r2",
            CubeListBuilder.create().texOffs(20, 0).mirror().addBox(-3.5F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(0.978F, 10.16F, -3.79F, 1.5708F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r3 = left_leg.addOrReplaceChild(
            "left_leg_r3",
            CubeListBuilder.create().texOffs(19, 16).mirror().addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-2.772F, 10.16F, -0.54F, 0.0F, 0.0F, 1.5708F)
         );
         PartDefinition left_leg_r4 = left_leg.addOrReplaceChild(
            "left_leg_r4",
            CubeListBuilder.create().texOffs(20, 0).mirror().addBox(-2.5F, -0.5F, -0.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-0.022F, 10.16F, 3.71F, 1.5708F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r5 = left_leg.addOrReplaceChild(
            "left_leg_r5",
            CubeListBuilder.create()
               .texOffs(12, 15)
               .mirror()
               .addBox(1.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(12, 15)
               .mirror()
               .addBox(3.0F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(12, 15)
               .mirror()
               .addBox(4.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offsetAndRotation(-3.022F, 12.26F, -4.11F, -0.8727F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r6 = left_leg.addOrReplaceChild(
            "left_leg_r6",
            CubeListBuilder.create().texOffs(4, 13).mirror().addBox(-2.5F, -0.5F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(0.478F, 12.477F, -3.0386F, -0.3054F, 0.0F, 0.0F)
         );
         PartDefinition left_leg_r7 = left_leg.addOrReplaceChild(
            "left_leg_r7",
            CubeListBuilder.create().texOffs(4, 11).mirror().addBox(-1.5F, -1.0F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(-0.522F, 11.81F, -3.71F, -0.6981F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }

      @Override
      public void adjustForFirstPersonRender(@Nonnull PoseStack poseStack) {
      }
   }
}
