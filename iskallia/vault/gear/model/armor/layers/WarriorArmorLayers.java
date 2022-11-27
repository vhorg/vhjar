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

public class WarriorArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? WarriorArmorLayers.LeggingsLayer::createBodyLayer : WarriorArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? WarriorArmorLayers.LeggingsLayer::new : WarriorArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(14.8333F, -3.8333F, -1.75F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(-1.1667F, -3.8333F, -1.75F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 15)
               .addBox(14.3333F, -0.8333F, -2.25F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(42, 0)
               .addBox(-1.6667F, -0.8333F, -2.25F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(30, 0)
               .addBox(12.3333F, 0.1667F, -2.75F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(32, 18)
               .addBox(0.3333F, 0.1667F, -2.75F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.3333F, -8.1667F, -0.5F, 0.829F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(28, 26)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-5.5F, 0.0F, -4.0F, 11.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(48, 38)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(31, 7)
               .addBox(-5.25F, -4.25F, -3.5F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(4, 0)
               .addBox(-2.5F, -2.75F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 5)
               .addBox(-4.5F, -2.75F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 5)
               .addBox(-4.5F, -2.75F, -3.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 14)
               .addBox(-2.5F, -2.75F, -3.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 19)
               .addBox(-5.5F, -2.75F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 19)
               .addBox(-5.5F, -2.75F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 30)
               .addBox(-5.5F, -2.75F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(32, 42)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 30)
               .addBox(-0.75F, -4.25F, -3.5F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(19, 32)
               .addBox(4.5F, -2.75F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 32)
               .addBox(4.5F, -2.75F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(3, 31)
               .addBox(4.5F, -2.75F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(23, 30)
               .addBox(3.5F, -2.75F, -3.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(19, 30)
               .addBox(1.5F, -2.75F, -3.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 2)
               .addBox(1.5F, -2.75F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(30, 0)
               .addBox(3.5F, -2.75F, 2.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
