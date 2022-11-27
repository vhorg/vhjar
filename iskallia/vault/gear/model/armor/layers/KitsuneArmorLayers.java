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

public class KitsuneArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? KitsuneArmorLayers.LeggingsLayer::createBodyLayer : KitsuneArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? KitsuneArmorLayers.LeggingsLayer::new : KitsuneArmorLayers.MainLayer::new;
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
               .texOffs(0, 0)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(24, 0)
               .addBox(-5.0F, -9.0F, -6.0F, 10.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(38, 35)
               .addBox(2.6667F, 0.8333F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 24)
               .addBox(1.6667F, -1.1667F, -0.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-0.3333F, -2.1667F, -0.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.3333F, -11.8333F, -4.5F, 0.0F, 0.0F, 0.5236F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(38, 35)
               .addBox(-3.6667F, 0.8333F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 24)
               .addBox(-2.6667F, -1.1667F, -0.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-1.6667F, -2.1667F, -0.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.3333F, -11.8333F, -4.5F, 0.0F, 0.0F, -0.5236F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(42, 14).addBox(-1.5F, -1.5F, -2.5F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -3.5F, -7.5F, 0.3927F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 16)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(14, 32)
               .addBox(-5.0F, 0.0F, -4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(14, 32)
               .addBox(-4.0F, 2.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(14, 32)
               .addBox(3.0F, 0.0F, -4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(14, 32)
               .addBox(2.0F, 2.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(14, 32)
               .addBox(1.0F, 4.0F, -4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(14, 32)
               .addBox(-1.0F, 5.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(14, 32)
               .addBox(-3.0F, 4.0F, -4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(34, 32)
               .addBox(-0.5F, 1.0F, 0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 32)
               .addBox(-1.0F, 0.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, 15.5F, 10.5F, -0.9599F, 0.0F, 0.0F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, 0.5F, -5.0F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, 11.5F, 7.0F, -0.7418F, 0.0F, 0.0F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(24, 8).addBox(-1.5F, -0.5F, -3.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, 11.5F, 7.0F, -0.9599F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(38, 38)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(40, 0)
               .addBox(-5.0F, 2.0F, -4.0F, 3.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(40, 0)
               .addBox(-5.0F, 8.0F, -4.0F, 3.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(38, 38)
               .mirror()
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .mirror(false)
               .texOffs(40, 0)
               .addBox(2.0F, 8.0F, -4.0F, 3.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(40, 0)
               .addBox(2.0F, 2.0F, -4.0F, 3.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(22, 32)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(24, 24)
               .addBox(-3.5F, 12.25F, -3.75F, 7.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(24, 20)
               .addBox(-2.5F, 13.25F, -2.75F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 20)
               .addBox(-2.5F, 13.25F, 1.25F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(22, 32)
               .mirror()
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .mirror(false)
               .texOffs(24, 20)
               .addBox(-2.3F, 13.25F, -2.75F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 20)
               .addBox(-2.3F, 13.25F, 1.25F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 24)
               .addBox(-3.3F, 12.25F, -3.75F, 7.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
