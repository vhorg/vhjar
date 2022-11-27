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

public class LionguardArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? LionguardArmorLayers.LeggingsLayer::createBodyLayer : LionguardArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? LionguardArmorLayers.LeggingsLayer::new : LionguardArmorLayers.MainLayer::new;
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
               .texOffs(0, 91)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(24, 91)
               .addBox(-5.0F, -9.0F, -6.0F, 10.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(34, 70)
               .addBox(-3.0F, -8.0F, -7.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 96)
               .addBox(-5.0F, -1.0F, -6.0F, 10.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(34, 74)
               .addBox(-3.0F, -1.0F, -6.5F, 6.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(0, 70)
               .addBox(-6.0F, -10.0F, -4.0F, 12.0F, 11.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(24, 99)
               .addBox(-5.0F, -11.0F, -3.0F, 10.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 70).addBox(-0.5F, -0.25F, -0.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.4F, -0.7527F, -5.7789F, 0.7418F, -1.5708F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 72).addBox(-0.5F, -0.5F, -0.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.4F, -5.191F, -6.1159F, 0.7418F, 1.5708F, 0.0F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(3, 71).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.9F, -0.7527F, -5.7789F, 0.7418F, -1.5708F, 0.0F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(3, 73).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.9F, -5.191F, -6.1159F, 0.7418F, -1.5708F, 0.0F)
         );
         PartDefinition cube_r5 = head.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(0, 74).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.8F, -0.7527F, -5.7789F, 0.7418F, -1.5708F, 0.0F)
         );
         PartDefinition cube_r6 = head.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(3, 75).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.8F, -5.191F, -6.1159F, 0.7418F, -1.5708F, 0.0F)
         );
         PartDefinition cube_r7 = head.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(0, 76).addBox(-0.5F, -0.75F, -0.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.4F, -0.7527F, -5.7789F, 0.7418F, -1.5708F, 0.0F)
         );
         PartDefinition cube_r8 = head.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(6, 70).addBox(-0.5F, -0.5F, -0.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.4F, -5.191F, -6.1159F, 0.7418F, -1.5272F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 33)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-5.0F, 1.0F, -4.0F, 10.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(24, 40)
               .addBox(-4.0F, 9.25F, -3.5F, 8.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(50, 59).addBox(-7.0F, 1.0F, -7.5F, 14.0F, 0.0F, 24.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 2.3995F, 9.0825F, -0.829F, 0.0F, 0.0F)
         );
         PartDefinition cube_r10 = body.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(24, 40).addBox(-4.0F, -1.5F, -3.5F, 8.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-9.0F, -0.25F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r11 = body.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(24, 40).addBox(-4.0F, -1.5F, -3.5F, 8.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(9.0F, -0.25F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r12 = body.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(32, 58).addBox(-4.2417F, -4.0F, -1.4261F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.048F, 5.4F, -4.3293F, 0.2752F, 0.473F, 0.1279F)
         );
         PartDefinition cube_r13 = body.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create().texOffs(44, 61).addBox(-2.5F, -4.0F, -0.5F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, 5.3F, 4.55F, -0.3001F, -0.504F, 0.1483F)
         );
         PartDefinition cube_r14 = body.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(44, 24).addBox(-0.7583F, -4.0F, -1.4261F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.048F, 5.4F, -4.3293F, 0.2752F, -0.473F, -0.1279F)
         );
         PartDefinition cube_r15 = body.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create().texOffs(56, 61).addBox(-2.5F, -4.0F, -0.5F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.8301F, 5.3F, 4.55F, -0.3001F, 0.504F, -0.1483F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(56, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r16 = right_arm.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create()
               .texOffs(47, 33)
               .addBox(-4.65F, 3.5F, -3.5F, 3.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(24, 25)
               .addBox(-5.0F, 1.5F, -4.1F, 6.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0F, -0.5F, 0.0F, 0.0F, 0.0F, -0.2182F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(16, 50)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 0)
               .addBox(1.0F, 7.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r17 = left_arm.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create()
               .texOffs(47, 47)
               .addBox(1.55F, 3.5F, -3.5F, 3.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(28, 9)
               .addBox(-1.0F, 1.5F, -4.2F, 6.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.2182F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 49).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
