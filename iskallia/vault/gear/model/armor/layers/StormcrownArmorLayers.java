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

public class StormcrownArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return StormcrownArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return StormcrownArmorLayers.MainLayer::new;
   }

   @OnlyIn(Dist.CLIENT)
   public static class MainLayer extends ArmorLayers.MainLayer {
      public MainLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition Head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(32, 32)
               .addBox(-3.0F, -12.0F, -7.0F, 6.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = Head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(24, 2)
               .addBox(0.0F, 3.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 0)
               .addBox(-2.0F, 2.0F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0F, -6.25F, -5.75F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition cube_r2 = Head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(24, 0)
               .addBox(-5.0F, -1.25F, -0.5F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 2)
               .addBox(-2.0F, -2.25F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.0F, -3.25F, -5.75F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition cube_r3 = Head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(40, 27).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.0F, -6.25F, -5.75F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition cube_r4 = Head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(40, 41)
               .addBox(-2.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(20, 16)
               .addBox(1.0F, -9.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(40, 16)
               .addBox(0.0F, -7.0F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.25F, -13.0F, -5.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r5 = Head.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(24, 4).addBox(-2.0F, -4.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-7.1634F, -17.6194F, -5.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r6 = Head.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(32, 41)
               .addBox(-2.0F, -7.0F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(46, 25)
               .addBox(0.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.25F, -13.0F, -5.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition Body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition RightArm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(16, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition LeftArm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition RightLeg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition LeftLeg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(24, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
