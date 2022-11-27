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

public class FairyArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? FairyArmorLayers.LeggingsLayer::createBodyLayer : FairyArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? FairyArmorLayers.LeggingsLayer::new : FairyArmorLayers.MainLayer::new;
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
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
               .texOffs(0, 16)
               .addBox(-5.0F, 10.0F, -3.0F, 10.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(42, 38)
               .addBox(-2.5F, 1.5F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(15, 43)
               .addBox(-2.5F, -0.5F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, 12.5F, 3.5F, -0.4971F, -0.1719F, -0.3053F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(0, 39)
               .addBox(-2.5F, 1.5F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(30, 44)
               .addBox(-2.5F, -0.5F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, 12.5F, -3.5F, 0.3491F, 0.0F, -0.2618F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(27, 37)
               .addBox(-2.5F, 1.5F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 45)
               .addBox(-2.5F, -0.5F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, 12.5F, -3.5F, 0.4232F, -0.1096F, 0.2382F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(32, 31)
               .addBox(-2.5F, 1.5F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(45, 45)
               .addBox(-2.5F, -0.5F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, 12.5F, 3.5F, -0.4363F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(24, 0).addBox(-5.0F, -4.0F, 0.25F, 10.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 15.0341F, 3.2412F, 0.5236F, 0.0F, 0.0F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(32, 17).addBox(-5.0F, -3.0F, 0.2F, 10.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 13.0F, 3.5F, 0.7418F, 0.0F, 0.0F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(26, 9).addBox(-5.0F, -4.0F, -1.5F, 10.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 16.0F, -3.5F, -0.4363F, 0.0F, 0.0F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(32, 24).addBox(-5.0F, -3.0F, -1.25F, 10.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 13.0F, -3.5F, -0.7418F, 0.0F, 0.0F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(14, 49).addBox(0.5F, -4.0F, -3.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, 16.0F, 0.0F, 0.0F, 0.0F, -0.6109F)
         );
         PartDefinition cube_r10 = body.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(28, 50).addBox(-2.0F, -3.5F, -3.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, 16.0F, 0.0F, 0.0F, 0.0F, 0.6981F)
         );
         PartDefinition cube_r11 = body.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(0, 51).addBox(-1.5F, -3.0F, -3.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, 13.0F, 0.0F, 0.0F, 0.0F, 0.8727F)
         );
         PartDefinition cube_r12 = body.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(48, 0).addBox(0.5F, -3.0F, -3.0F, 1.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, 13.0F, 0.0F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 23).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 23).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
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
               .texOffs(22, 29)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(39, 24)
               .addBox(-4.0F, -6.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(39, 22)
               .addBox(-1.0F, -8.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 39)
               .addBox(2.0F, -6.5F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 39)
               .addBox(5.0F, -6.5F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 39)
               .addBox(5.0F, -7.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 39)
               .addBox(5.0F, -5.5F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 37)
               .addBox(3.0F, -5.5F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 37)
               .addBox(0.0F, -7.5F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 37)
               .addBox(-3.25F, -6.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 39)
               .addBox(-6.0F, -5.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 37)
               .addBox(-6.0F, -7.5F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 37)
               .addBox(-6.0F, -7.5F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-11.0F, -6.0F, -11.0F, 22.0F, 0.0F, 21.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 41)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(36, 27)
               .addBox(-4.0F, 5.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 25)
               .addBox(-1.0F, 3.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 21)
               .addBox(1.0F, 5.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(33, 26)
               .addBox(3.0F, 3.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 23)
               .addBox(-3.0F, 2.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 23)
               .addBox(3.0F, 4.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 25)
               .addBox(0.0F, 5.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 27)
               .addBox(1.0F, 2.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(33, 22)
               .addBox(-3.0F, 2.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(33, 24)
               .addBox(-4.0F, 5.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(0, 6).addBox(0.0F, -4.0F, -7.5F, 0.0F, 8.0F, 15.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, 2.1321F, 6.4042F, 0.982F, -0.1733F, -0.2528F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 14).addBox(0.0F, -4.0F, -7.5F, 0.0F, 8.0F, 15.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.0F, 2.1321F, 6.4042F, 0.982F, 0.1733F, 0.2528F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(46, 21)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(16, 11)
               .addBox(-3.0F, 3.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 9)
               .addBox(-1.0F, 6.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 7)
               .addBox(-3.0F, 3.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 16)
               .addBox(-1.0F, 6.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(15, 19)
               .addBox(-5.0F, 3.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 18)
               .addBox(-5.0F, 7.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(15, 17)
               .addBox(4.0F, 3.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 18)
               .addBox(1.0F, 7.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(15, 15)
               .addBox(0.0F, 4.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 2)
               .addBox(0.0F, 4.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(12, 2)
               .addBox(1.0F, 7.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 3)
               .addBox(4.0F, 7.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(40, 45)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(24, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
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
