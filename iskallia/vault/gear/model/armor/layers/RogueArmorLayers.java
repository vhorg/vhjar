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

public class RogueArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? RogueArmorLayers.LeggingsLayer::createBodyLayer : RogueArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? RogueArmorLayers.LeggingsLayer::new : RogueArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 32, 32);
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
               .texOffs(0, 14)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 19)
               .addBox(-4.0F, 0.0F, -7.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 19)
               .addBox(2.0F, 0.0F, -7.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 19)
               .addBox(-5.0F, -1.0F, -7.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 19)
               .addBox(4.0F, -1.0F, -7.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(16, 46)
               .addBox(-6.0F, -6.0F, -6.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 46)
               .addBox(5.0F, -6.0F, -6.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(-5.0F, -9.0F, -7.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(4.0F, -9.0F, -7.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(36, 33)
               .addBox(-4.0F, -10.0F, -7.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 15)
               .addBox(-1.5F, -11.0F, -8.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(36, 33)
               .addBox(1.0F, -10.0F, -7.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 33)
               .addBox(5.0F, -6.0F, -5.0F, 1.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(24, 33)
               .addBox(-6.0F, -6.0F, -5.0F, 1.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(26, 4)
               .addBox(-4.0F, -10.0F, -5.0F, 3.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(26, 4)
               .addBox(1.0F, -10.0F, -5.0F, 3.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(20, 20)
               .addBox(-1.5F, -11.0F, -5.0F, 3.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(36, 38)
               .addBox(-3.5F, -11.0F, -5.0F, 2.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(36, 38)
               .addBox(1.5F, -11.0F, -5.0F, 2.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(32, 49)
               .addBox(-4.0F, -10.0F, 5.0F, 8.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(50, 50)
               .addBox(-3.0F, -10.0F, 6.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(29, 0)
               .addBox(-2.0F, -10.0F, 8.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(33, 15)
               .addBox(-2.0F, -10.0F, 9.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 30)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-5.5F, 0.0F, -3.6F, 11.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(36, 36)
               .addBox(-5.0F, 12.0F, -3.5F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(38, 15).addBox(-1.5F, -5.0F, -4.0F, 1.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, 17.0F, 0.0F, 0.0F, 0.0F, 0.4363F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(38, 15).addBox(0.5F, -5.0F, -3.5F, 1.0F, 10.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, 17.0F, -0.5F, 0.0F, 0.0F, -0.4363F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(42, 0).addBox(-5.0F, -5.0F, 0.5F, 10.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 17.0F, 3.5F, 0.4363F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-3.0F, 8.0F, 2.75F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(29, 4)
               .addBox(-2.5F, 12.0F, 2.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 33)
               .addBox(-4.0F, 4.0F, 3.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 48)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(24, 33)
               .addBox(0.0F, 4.0F, 3.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(16, 48)
               .mirror()
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .mirror(false)
               .texOffs(0, 0)
               .addBox(1.0F, 8.0F, 2.75F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(29, 4)
               .addBox(1.5F, 12.0F, 2.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 46).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 46).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)).mirror(false),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
