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

public class GuybrushArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? GuybrushArmorLayers.LeggingsLayer::createBodyLayer : GuybrushArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? GuybrushArmorLayers.LeggingsLayer::new : GuybrushArmorLayers.MainLayer::new;
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
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
               .texOffs(28, 1)
               .addBox(-6.0F, 10.0F, -4.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(12, 16)
               .addBox(-4.5F, 11.0F, -4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(28, 15)
               .addBox(-6.0F, 7.5F, -4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-5.5F, 8.5F, -3.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(6, 32)
               .addBox(-4.0F, 11.25F, -3.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 0)
               .addBox(-6.0F, 6.0F, -4.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 32)
               .addBox(-4.25F, 6.25F, -3.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(27, 27)
               .addBox(4.5F, 9.0F, -4.0F, 1.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(5.0F, 12.0F, -4.0F, 0.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)),
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
         return LayerDefinition.create(meshdefinition, 64, 64);
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
               .texOffs(14, 19)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 0)
               .addBox(-1.0F, -7.0F, 5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(38, 8).addBox(0.0F, -3.0F, -4.0F, 0.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, -6.0F, 9.0F, 0.0F, -0.3927F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 39).addBox(1.0F, -3.0F, -4.0F, 0.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.0F, -6.0F, 9.0F, 0.0F, 0.3927F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(22, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(38, 22)
               .addBox(-5.0F, -1.5F, 0.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(46, 26)
               .addBox(-4.0F, -4.75F, 0.25F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(48, 36)
               .addBox(0.0F, -4.75F, 0.25F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, 7.0F, -5.0F, 0.0F, 0.0F, 0.829F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -11.0F, 1.0F, 3.0F, 19.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, 7.0F, -5.0F, 0.0F, 0.0F, -0.7418F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(46, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(32, 35).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 35).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r5 = right_leg.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(16, 51).addBox(-2.0F, -3.0F, -0.25F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 7.0F, -3.5F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 31).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r6 = left_leg.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(48, 46).addBox(-2.0F, -3.0F, -0.25F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.2F, 7.0F, -3.5F, 0.3927F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
