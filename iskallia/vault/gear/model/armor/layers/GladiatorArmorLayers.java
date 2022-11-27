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

public class GladiatorArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? GladiatorArmorLayers.LeggingsLayer::createBodyLayer : GladiatorArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? GladiatorArmorLayers.LeggingsLayer::new : GladiatorArmorLayers.MainLayer::new;
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
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(32, 0)
               .addBox(-1.0F, -10.0F, -6.0F, 2.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(22, 67)
               .addBox(-0.5F, -12.0F, 2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(18, 64)
               .addBox(-0.5F, -14.0F, -3.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(60, 0)
               .addBox(-0.5F, -18.0F, -8.0F, 1.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(16, 48)
               .addBox(-0.5F, -14.0F, -1.0F, 1.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(56, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(0, 48)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(28, 16)
               .addBox(-5.0F, -5.0F, -4.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(22, 64)
               .addBox(-2.0F, -9.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r1 = right_arm.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(6, 64).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.5F, -6.0F, 0.0F, -2.3557F, 1.5091F, -2.3567F)
         );
         PartDefinition cube_r2 = right_arm.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(12, 64)
               .addBox(-1.0F, 0.25F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(34, 54)
               .addBox(-1.0F, 2.25F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.0F, -3.25F, 0.0F, 0.0F, 0.0F, -0.6545F)
         );
         PartDefinition cube_r3 = right_arm.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(0, 64)
               .addBox(1.5F, -6.1667F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(34, 48)
               .addBox(0.5F, -2.1667F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(16, 58)
               .addBox(0.5F, 1.8333F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.5F, -8.3333F, 0.0F, 0.0F, 0.0F, 0.3491F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(32, 32)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 16)
               .addBox(-1.0F, -5.0F, -4.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(22, 64)
               .addBox(1.5F, -9.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r4 = left_arm.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(6, 64).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, -6.0F, 0.0F, -2.3557F, 1.5091F, -2.3567F)
         );
         PartDefinition cube_r5 = left_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(0, 64)
               .addBox(-19.5F, 3.8333F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(34, 48)
               .addBox(-20.5F, 7.8333F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(16, 58)
               .addBox(-20.5F, 11.8333F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-16.5F, -8.3333F, 0.0F, -3.1416F, 0.0F, 2.7053F)
         );
         PartDefinition cube_r6 = left_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(34, 54)
               .addBox(-22.0F, -9.75F, -1.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(12, 64)
               .addBox(-22.0F, -11.75F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-17.0F, -3.25F, 0.0F, 3.1416F, 0.0F, -2.618F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
