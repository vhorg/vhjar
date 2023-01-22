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

public class PirateArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? PirateArmorLayers.LeggingsLayer::createBodyLayer : PirateArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? PirateArmorLayers.LeggingsLayer::new : PirateArmorLayers.MainLayer::new;
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
               .texOffs(18, 16)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(42, 14)
               .addBox(-6.0F, -9.0F, 5.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 32)
               .addBox(4.0F, -9.0F, 5.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 58)
               .addBox(-4.0F, -10.0F, 5.0F, 8.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 0)
               .addBox(-3.0F, -11.0F, 5.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 24)
               .addBox(-4.0F, -10.0F, -6.0F, 8.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(4.0F, -9.0F, -6.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 11)
               .addBox(-3.0F, -11.0F, -6.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(26, 2)
               .addBox(-6.0F, -9.0F, -6.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(52, 0).addBox(-5.0F, -1.75F, -1.25F, 10.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, -7.5F, 0.0F, 0.0F, -1.5708F, 0.3927F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(52, 5).addBox(-5.0F, -2.0F, 0.5F, 10.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, -7.5F, 0.0F, 0.0F, -1.5708F, -0.3927F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 32)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(38, 58)
               .addBox(-3.0F, 3.0F, 3.0F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-0.5F, -12.5F, 2.5F, 0.0F, 11.0F, 13.0F, new CubeDeformation(0.0F))
               .texOffs(16, 48)
               .addBox(-1.0F, -12.5F, 1.5F, 1.0F, 19.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, -1.5F, 5.5F, -0.4363F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(56, 40).addBox(-4.8255F, -5.4571F, 0.8107F, 6.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.4111F, 14.0183F, 4.4315F, 0.675F, -0.3786F, -0.1068F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(56, 53).addBox(0.001F, -5.6193F, 0.419F, 6.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.1533F, 13.8006F, 5.2443F, 0.675F, 0.3786F, 0.1068F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(50, 24).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r6 = right_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(42, 14).addBox(-4.0F, -2.0F, -3.5F, 6.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, 7.5F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r7 = right_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(26, 3).addBox(-4.5F, -0.75F, -4.0F, 9.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, -2.5F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(0, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r8 = left_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -1.0F, -4.0F, 9.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.5F, -2.5F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r9 = left_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(24, 32).addBox(-2.25F, -2.0F, -3.5F, 6.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, 7.5F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(40, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(24, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
