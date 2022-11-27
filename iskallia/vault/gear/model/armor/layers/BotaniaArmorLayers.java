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

public class BotaniaArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? BotaniaArmorLayers.LeggingsLayer::createBodyLayer : BotaniaArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? BotaniaArmorLayers.LeggingsLayer::new : BotaniaArmorLayers.MainLayer::new;
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
               .texOffs(64, 11)
               .addBox(-8.0F, -9.0F, 3.0F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(56, 40)
               .addBox(-7.0F, -10.25F, 0.25F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(33, 17)
               .addBox(-7.0F, -11.0F, 4.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(48, 11)
               .addBox(1.5F, -2.196F, -0.5641F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(15, 51)
               .addBox(-5.5F, -2.196F, -0.5641F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(72, 29)
               .addBox(-5.5F, -2.196F, -7.5641F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(72, 34)
               .addBox(1.5F, -2.196F, -7.5641F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(69, 52)
               .addBox(-2.5F, -3.946F, -4.5641F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 70)
               .addBox(-1.5F, -2.946F, -3.5641F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -14.8353F, -7.6856F, 1.2217F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(68, 17).addBox(-2.5F, -0.154F, -9.0296F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -14.8353F, -7.6856F, 0.7854F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(48, 66).addBox(-2.5F, -1.3932F, 0.4888F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -14.8353F, -7.6856F, 1.8326F, 0.0F, -3.1416F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(63, 67).addBox(2.1775F, -0.8226F, -4.5641F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -14.8353F, -7.6856F, 1.1814F, 0.4488F, 2.9654F)
         );
         PartDefinition cube_r5 = head.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(68, 23).addBox(-7.1316F, -1.2291F, -4.5641F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -14.8353F, -7.6856F, 1.1955F, -0.3678F, -3.0009F)
         );
         PartDefinition cube_r6 = head.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(0, 51).addBox(-2.5F, -0.8607F, -5.8868F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -14.8353F, -7.6856F, 2.0071F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r7 = head.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(32, 35).addBox(-4.0F, -0.2388F, -9.3947F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -14.8353F, -7.6856F, 2.7053F, 0.0F, -3.1416F)
         );
         PartDefinition cube_r8 = head.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create()
               .texOffs(0, 17)
               .addBox(-5.5F, 1.8353F, -12.8144F, 11.0F, 7.0F, 11.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-8.0F, 8.8353F, -15.3144F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -14.8353F, -7.6856F, 3.098F, 0.0F, 3.1416F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(44, 17)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(70, 0)
               .addBox(-4.0F, 1.0F, -4.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create()
               .texOffs(24, 35)
               .addBox(-2.217F, -0.8117F, -0.4F, 7.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 33)
               .addBox(-3.217F, -11.8117F, -0.4F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(31, 50)
               .addBox(-5.217F, -10.8117F, -0.4F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.339F, 6.8117F, 4.8011F, 0.0F, 0.6109F, -0.3229F)
         );
         PartDefinition cube_r10 = body.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create()
               .texOffs(33, 21)
               .addBox(3.7307F, 2.5039F, -0.65F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 17)
               .addBox(1.7307F, 6.5039F, -0.65F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.339F, 6.8117F, 4.8011F, 0.3819F, 0.4891F, 0.3844F)
         );
         PartDefinition cube_r11 = body.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create()
               .texOffs(56, 35)
               .addBox(-3.7418F, 6.589F, -0.65F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 35)
               .addBox(-5.7418F, 2.589F, -0.65F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.3701F, 6.3784F, 4.0906F, 0.2455F, -0.3644F, -0.2636F)
         );
         PartDefinition cube_r12 = body.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create()
               .texOffs(0, 63)
               .addBox(-4.5897F, -0.8784F, -0.4F, 7.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(69, 58)
               .addBox(-3.5897F, -11.8784F, -0.4F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(48, 0)
               .addBox(-4.5897F, -10.8784F, -0.4F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.3701F, 6.3784F, 4.0906F, 0.0F, -0.4363F, 0.3491F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(32, 61).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r13 = right_arm.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create()
               .texOffs(60, 11)
               .addBox(-1.5F, 1.3F, -3.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(65, 46)
               .addBox(-2.5F, 1.3F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.5F, -0.5F, -5.5F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition cube_r14 = right_arm.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create()
               .texOffs(0, 22)
               .addBox(-3.5F, 1.5F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(63, 60)
               .addBox(-2.5F, 1.5F, -3.0F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-6.5F, -0.5F, 0.0F, 0.0F, 0.0F, -0.9163F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(16, 59).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r15 = left_arm.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create()
               .texOffs(0, 17)
               .addBox(2.5F, 1.5F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(56, 33)
               .addBox(-2.5F, 1.5F, -3.0F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(6.5F, -0.5F, 0.0F, 0.0F, 0.0F, 0.9599F)
         );
         PartDefinition cube_r16 = left_arm.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create()
               .texOffs(20, 56)
               .addBox(-1.5F, 1.0F, -3.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(64, 40)
               .addBox(-2.5F, 1.0F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.5F, -0.5F, -5.5F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(53, 50).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
