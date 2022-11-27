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

public class SoulEaterArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SoulEaterArmorLayers.LeggingsLayer::createBodyLayer : SoulEaterArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SoulEaterArmorLayers.LeggingsLayer::new : SoulEaterArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.52F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 17).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 17).mirror().addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.51F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(33, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(46, 43)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
               .texOffs(21, 28)
               .addBox(0.0F, -9.0F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 17)
               .addBox(0.0F, -9.0F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.5F))
               .texOffs(11, 47)
               .addBox(0.0F, 4.0F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.5F))
               .texOffs(42, 17)
               .addBox(0.0F, -20.0F, -2.0F, 9.0F, 14.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(42, 17)
               .addBox(0.0F, -20.0F, 2.0F, 9.0F, 14.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(46, 43)
               .mirror()
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
               .mirror(false)
               .texOffs(21, 28)
               .mirror()
               .addBox(-6.0F, -9.0F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(0, 17)
               .mirror()
               .addBox(-6.0F, -9.0F, -4.0F, 6.0F, 10.0F, 8.0F, new CubeDeformation(0.5F))
               .mirror(false)
               .texOffs(11, 47)
               .mirror()
               .addBox(-4.0F, 4.0F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.5F))
               .mirror(false)
               .texOffs(42, 17)
               .mirror()
               .addBox(-9.0F, -20.0F, -2.0F, 9.0F, 14.0F, 0.0F, new CubeDeformation(0.0F))
               .mirror(false)
               .texOffs(42, 17)
               .mirror()
               .addBox(-9.0F, -20.0F, 2.0F, 9.0F, 14.0F, 0.0F, new CubeDeformation(0.0F))
               .mirror(false),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(0, 36)
               .addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
               .texOffs(50, 32)
               .addBox(-1.9F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.75F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(0, 36)
               .mirror()
               .addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F))
               .mirror(false)
               .texOffs(50, 32)
               .mirror()
               .addBox(-2.1F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.75F))
               .mirror(false),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
