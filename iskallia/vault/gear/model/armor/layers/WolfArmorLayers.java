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

public class WolfArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? WolfArmorLayers.LeggingsLayer::createBodyLayer : WolfArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? WolfArmorLayers.LeggingsLayer::new : WolfArmorLayers.MainLayer::new;
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
               .texOffs(22, 26)
               .addBox(-4.0F, -6.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(93, 8)
               .addBox(-2.0F, -7.0F, -5.75F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(93, 15)
               .addBox(-1.0F, -6.0F, -5.75F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(93, 8)
               .addBox(1.0F, -7.0F, -5.75F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(32, 54)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(30, 42)
               .addBox(-6.0F, -4.0F, 3.0F, 12.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-6.5F, -4.5F, 2.75F, 13.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(54, 34)
               .addBox(-4.0F, 3.0F, 3.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(58, 56).addBox(-8.5F, -4.5F, -4.25F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(62, 2).addBox(0.0F, -3.25F, 0.0F, 4.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-12.5F, 4.0F, 3.75F, 1.0472F, 0.3927F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(32, 0)
               .addBox(-5.0F, -7.75F, 2.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 34)
               .addBox(2.0F, -7.75F, 2.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(2, 96)
               .addBox(-4.0F, -1.75F, 2.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offsetAndRotation(-11.0F, -4.25F, -4.25F, 0.0F, 0.3927F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(30, 22).addBox(-1.0F, 4.25F, 5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-9.0F, -4.25F, -4.25F, 0.0F, 0.3927F, 0.0F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(0, 18)
               .addBox(-0.75F, 6.0F, 4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 4)
               .addBox(-2.75F, 6.0F, 4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(23, 18)
               .addBox(-3.75F, 6.0F, 4.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-9.0F, -4.25F, -4.25F, -1.5708F, 0.3927F, 0.0F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(48, 73)
               .addBox(-2.5F, 3.0F, 1.25F, 5.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 14)
               .addBox(-4.0F, -10.0F, -1.75F, 8.0F, 13.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-11.0F, -4.25F, -4.25F, -1.5708F, 0.3927F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(70, 42)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 34)
               .addBox(-6.0F, -3.5F, -4.0F, 7.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(32, 70)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(30, 6)
               .addBox(-1.0F, -3.5F, -4.0F, 7.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(16, 62)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(46, 22)
               .addBox(-4.0F, 6.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(0, 62)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 50)
               .addBox(-4.05F, 6.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
