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

public class SweetheartArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SweetheartArmorLayers.LeggingsLayer::createBodyLayer : SweetheartArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SweetheartArmorLayers.LeggingsLayer::new : SweetheartArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(31, 31).addBox(-1.5F, 0.0F, -0.5F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.767F, 9.1294F, 0.5F, 0.0F, 1.5708F, -0.2618F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 32).addBox(-2.5F, 0.0F, -0.5F, 4.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.767F, 9.1294F, 0.5F, 0.0F, -1.5708F, 0.2618F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(24, 0).addBox(-5.0F, -2.0F, 0.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.0F, 11.169F, 1.7066F, 0.2182F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(28, 9).addBox(-5.0F, -2.0F, -1.0F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.0F, 11.169F, -1.7066F, -0.2182F, 0.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
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
            CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(27, 0).addBox(0.9744F, -3.9376F, -0.875F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.375F, -7.875F, -0.1005F, 0.0F, 0.0F, 0.2182F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(15, 33).addBox(-4.9744F, -3.9376F, -0.875F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.875F, -7.625F, -0.1005F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(0, 17).addBox(-1.0954F, -2.767F, -1.275F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.625F, -7.625F, 0.1495F, 0.0F, 0.0F, 0.3054F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(28, 29)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-5.0F, -0.775F, -3.5F, 10.0F, 10.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(32, 29)
               .addBox(-4.0F, -1.25F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(0, 48)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 33)
               .addBox(-4.025F, -3.025F, -3.5F, 4.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(34, 45)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(27, 10)
               .addBox(0.025F, -3.0F, -3.5F, 4.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(18, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(42, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
