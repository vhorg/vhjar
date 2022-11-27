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

public class ThermalArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ThermalArmorLayers.LeggingsLayer::createBodyLayer : ThermalArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ThermalArmorLayers.LeggingsLayer::new : ThermalArmorLayers.MainLayer::new;
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
               .texOffs(64, 49)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(76, 19)
               .addBox(-3.0F, -10.0F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(88, 47)
               .addBox(-5.0F, -9.0F, -6.0F, 10.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(18, 69)
               .addBox(-5.0F, -9.0F, 5.0F, 10.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(16, 92)
               .addBox(1.5F, -7.0F, 7.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(88, 52)
               .addBox(-5.5F, -7.0F, 7.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(24, 76)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(32, 12)
               .addBox(-5.0F, -4.0F, -6.0F, 10.0F, 9.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(64, 19)
               .addBox(-4.0F, 5.0F, -4.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 92)
               .addBox(-3.0F, 5.0F, 3.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(70, 27)
               .addBox(-6.5F, -6.0F, 6.0F, 6.0F, 14.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 69)
               .addBox(0.5F, -6.0F, 6.0F, 6.0F, 14.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(6, 11)
               .addBox(2.5F, 7.75F, 8.25F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(30, 33)
               .addBox(3.0F, 8.75F, 8.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(68, 78)
               .addBox(3.0F, 9.75F, 2.75F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(60, 40)
               .addBox(-4.0F, 9.75F, 2.75F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(30, 12)
               .addBox(-4.0F, 8.75F, 8.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 10)
               .addBox(-4.5F, 7.75F, 8.25F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(34, 0).addBox(-1.0F, -1.5F, -0.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 8.5F, 3.5F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.5F, -1.5F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 7.6455F, -3.1695F, 0.3491F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(0, 89)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 24)
               .addBox(-8.0F, -8.0F, -7.0F, 8.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
               .texOffs(0, 48)
               .addBox(-8.0F, 4.0F, -6.0F, 8.0F, 9.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(76, 78)
               .addBox(-6.0F, -9.0F, -4.0F, 6.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(88, 0)
               .addBox(-5.0F, -10.0F, -3.0F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(62, 66)
               .addBox(-7.0F, 2.0F, -5.0F, 6.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(30, 35)
               .addBox(-9.0F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 0)
               .addBox(-10.0F, -1.0F, 2.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(33, 34)
               .addBox(-9.0F, 7.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(34, 8)
               .addBox(-9.0F, 7.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(8, 24)
               .addBox(-10.0F, -1.0F, -3.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(34, 12)
               .addBox(-9.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(30, 0)
               .addBox(1.0F, 2.0F, -5.0F, 6.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(32, 36)
               .addBox(0.0F, 4.0F, -6.0F, 8.0F, 9.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(48, 78)
               .addBox(0.0F, -9.0F, -4.0F, 6.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(0.0F, -8.0F, -7.0F, 8.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
               .texOffs(80, 87)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(84, 65)
               .addBox(0.0F, -10.0F, -3.0F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(8, 34)
               .addBox(8.0F, -1.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(34, 6)
               .addBox(8.0F, 7.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 24)
               .addBox(9.0F, -1.0F, -3.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 34)
               .addBox(8.0F, -1.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 24)
               .addBox(9.0F, -1.0F, 2.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 34)
               .addBox(8.0F, 7.0F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(64, 87)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(64, 0)
               .addBox(-4.0F, 2.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(52, 0)
               .addBox(-3.95F, 8.0F, -6.0F, 8.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(48, 87)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(40, 57)
               .addBox(-3.8F, 2.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(88, 26)
               .addBox(-3.75F, 8.0F, -6.0F, 8.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
