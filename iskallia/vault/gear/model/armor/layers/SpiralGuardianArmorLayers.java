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

public class SpiralGuardianArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return SpiralGuardianArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return SpiralGuardianArmorLayers.MainLayer::new;
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
               .texOffs(0, 0)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(27, 19)
               .addBox(-2.0F, -2.0F, -5.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(42, 26)
               .addBox(-4.0F, -6.0F, -5.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 18)
               .addBox(-4.0F, -3.0F, -5.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 30)
               .addBox(-4.0F, -5.0F, -5.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(29, 0)
               .addBox(3.0F, -5.0F, -5.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 16)
               .addBox(1.0F, -3.0F, -5.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(1.0F, -2.0F, -5.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(28, 44)
               .addBox(-6.0F, -3.75F, -2.25F, 1.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-6.0F, -3.75F, -0.25F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(24, 0)
               .addBox(5.0F, -3.75F, -0.25F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(34, 46)
               .addBox(5.0F, -3.75F, -2.25F, 1.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -9.25F, 1.25F, -0.3927F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(42, 16).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 6.0F, -4.0F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(12, 44).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r3 = right_arm.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(17, 30).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, -3.0F, 0.5F, 0.0F, 0.0F, 1.1781F)
         );
         PartDefinition cube_r4 = right_arm.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(47, 28)
               .addBox(-1.0F, -2.0F, -1.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(48, 0)
               .addBox(-1.0F, -2.0F, -7.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, -1.0F, 3.25F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(35, 30).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r5 = left_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(24, 16).addBox(-1.0F, -3.5F, -3.5F, 2.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.25F, -2.5F, 0.0F, 0.0F, 0.0F, -1.1781F)
         );
         PartDefinition cube_r6 = left_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(40, 46)
               .addBox(-1.0F, -2.0F, -1.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 48)
               .addBox(-1.0F, -2.0F, -7.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, -1.0F, 3.25F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
