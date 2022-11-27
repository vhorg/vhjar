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

public class SamuraiArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SamuraiArmorLayers.LeggingsLayer::createBodyLayer : SamuraiArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SamuraiArmorLayers.LeggingsLayer::new : SamuraiArmorLayers.MainLayer::new;
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
               .texOffs(0, 0)
               .addBox(-4.0F, -7.0F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 15)
               .addBox(-4.0F, -9.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(36, 23).addBox(-1.273F, -1.0F, -1.6649F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.1327F, -6.906F, -5.7145F, -0.5074F, -0.5162F, 0.2679F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 40).addBox(-1.2073F, -4.0F, -1.6417F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0912F, -1.0F, -6.1377F, 0.3596F, -0.5462F, -0.1929F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(24, 0).addBox(-4.5957F, -1.0F, -1.7112F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.1327F, -6.906F, -5.7145F, -0.5175F, 0.5467F, -0.2877F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(14, 40).addBox(-4.5301F, -4.0F, -1.7344F, 5.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0912F, -1.0F, -6.1377F, 0.3674F, 0.5788F, 0.2075F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 24).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(14, 40).addBox(-0.7378F, -3.0F, -1.4173F, 5.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0133F, 4.0F, -3.5F, 0.2934F, -0.4623F, -0.1339F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(14, 40).addBox(-4.2622F, -3.0F, -1.4173F, 5.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0133F, 4.0F, -3.5F, 0.2934F, 0.4623F, 0.1339F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(32, 0)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(24, 16)
               .addBox(-4.0F, -4.0F, -3.0F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r7 = right_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(40, 16).addBox(-1.5F, -2.5F, -0.5F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.5F, -0.5F, -3.5F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r8 = right_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.5F, -0.5F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, -0.5F, 3.5F, 0.3491F, 0.0F, 0.0F)
         );
         PartDefinition cube_r9 = right_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(34, 34).addBox(-0.5F, -2.5F, -3.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, -0.5F, 0.0F, 0.0F, 0.0F, 0.3491F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(24, 16)
               .addBox(-1.0F, -4.0F, -3.0F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(32, 0)
               .mirror()
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .mirror(false),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r10 = left_arm.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(34, 34).addBox(-0.5F, -2.5F, -3.0F, 1.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.5F, -0.5F, 0.0F, 0.0F, 0.0F, -0.3491F)
         );
         PartDefinition cube_r11 = left_arm.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.5F, -0.5F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, -0.5F, 3.5F, 0.3491F, 0.0F, 0.0F)
         );
         PartDefinition cube_r12 = left_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(40, 16).addBox(-1.5F, -2.5F, -0.5F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.5F, -0.5F, -3.5F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(24, 24).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(24, 24).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
