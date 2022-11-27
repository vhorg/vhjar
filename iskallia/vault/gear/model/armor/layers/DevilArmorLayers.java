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

public class DevilArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? DevilArmorLayers.LeggingsLayer::createBodyLayer : DevilArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? DevilArmorLayers.LeggingsLayer::new : DevilArmorLayers.MainLayer::new;
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
               .texOffs(32, 22)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 61)
               .addBox(-8.0F, -15.0F, -3.0F, 3.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 61)
               .addBox(5.0F, -15.0F, -3.0F, 3.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(5.0F, -23.0F, -2.0F, 2.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-7.0F, -23.0F, -2.0F, 2.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(15, 41)
               .addBox(-2.0F, -25.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(74, 40).addBox(-0.5429F, -5.0F, -1.3107F, 5.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0184F, -7.0F, -5.5F, -0.4674F, -0.3542F, 0.1733F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 75).addBox(-4.4571F, -5.0F, -1.3107F, 5.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0184F, -7.0F, -5.5F, -0.4674F, 0.3542F, -0.1733F)
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
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(29, 38).addBox(1.694F, -4.5F, -9.0318F, 2.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(11.3404F, 0.6929F, 6.8071F, 1.113F, -0.6783F, -0.3741F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 41).addBox(-3.694F, -4.5F, -9.0318F, 2.0F, 9.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-11.3404F, 0.9429F, 7.8071F, 1.1569F, 0.7276F, 0.3912F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(44, 38).addBox(-2.6596F, -4.5F, 1.232F, 13.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-11.3404F, 0.9429F, 7.8071F, 0.9096F, 0.523F, -0.0252F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(56, 13).addBox(-10.3404F, -4.5F, 1.232F, 13.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(11.3404F, 0.6929F, 6.8071F, 0.8967F, -0.4645F, 0.02F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(76, 77).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 7.0F, 4.0F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(69, 0).addBox(-3.0F, -3.0F, -2.5F, 7.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.35F, 5.0F, -5.5F, 0.9855F, -0.9275F, -0.879F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(30, 74).addBox(-4.0F, -3.0F, -2.5F, 7.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.9913F, 5.0F, -5.5F, 0.8213F, 0.8189F, 0.6654F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(14, 74).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r10 = right_arm.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create()
               .texOffs(0, 19)
               .addBox(-8.5F, -3.0F, -5.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(47, 50)
               .addBox(-6.5F, -8.0F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0041F, 0.4183F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(72, 24).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r11 = left_arm.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create()
               .texOffs(0, 30)
               .addBox(-1.5F, -3.0F, -5.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(37, 0)
               .addBox(-1.5F, -8.0F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0041F, 0.4183F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(62, 63).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(46, 63).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
