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

public class JesterArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? JesterArmorLayers.LeggingsLayer::createBodyLayer : JesterArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? JesterArmorLayers.LeggingsLayer::new : JesterArmorLayers.MainLayer::new;
   }

   @OnlyIn(Dist.CLIENT)
   public static class LeggingsLayer extends ArmorLayers.LeggingsLayer {
      public LeggingsLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
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
               .texOffs(0, 14)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 0)
               .addBox(-6.0F, -6.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(16, 33)
               .addBox(-2.0F, -3.75F, -6.5F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-1.0F, -2.75F, -2.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -16.2943F, -5.7977F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(32, 14).addBox(-2.0F, -2.5F, -2.75F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -11.5F, -3.0F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(36, 0).addBox(-2.0F, -2.5F, 1.25F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.4481F, -15.4333F, 5.4481F, -0.7854F, -0.7854F, 0.0F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.5F, 0.25F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.4481F, -15.4333F, 4.4481F, -0.7854F, -0.7854F, 0.0F)
         );
         PartDefinition cube_r5 = head.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(28, 26).addBox(-2.0F, -2.5F, 1.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.0F, -11.5F, 1.0F, -0.3927F, -0.7854F, 0.0F)
         );
         PartDefinition cube_r6 = head.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(28, 37)
               .addBox(2.0F, -3.5F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(24, 14)
               .addBox(0.0F, -2.5F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.1495F, -15.067F, 4.1495F, -0.6155F, -0.5236F, 0.9553F)
         );
         PartDefinition cube_r7 = head.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(0, 30).addBox(0.75F, -2.5F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.0F, -11.5F, 1.0F, -0.3655F, -0.7119F, 0.5299F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
