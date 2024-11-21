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

public class PacifistSandalsArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return PacifistSandalsArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return PacifistSandalsArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition Body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition RightArm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition LeftArm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition RightLeg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(16, 36)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(24, 16)
               .addBox(-3.5F, 11.25F, -4.0F, 7.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r1 = RightLeg.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(48, 0)
               .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 48)
               .addBox(-7.5F, -2.0F, -1.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.25F, 12.0F, -1.0F, 0.7854F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = RightLeg.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(48, 51)
               .addBox(-1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(48, 44)
               .addBox(-8.0F, -2.0F, -1.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.0F, 12.5F, -1.0F, 0.48F, 0.0F, 0.0F)
         );
         PartDefinition LeftLeg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(32, 36)
               .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(24, 26)
               .addBox(-3.55F, 11.25F, -4.0F, 7.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition cube_r3 = LeftLeg.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create()
               .texOffs(16, 52)
               .addBox(-1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(28, 52)
               .addBox(-8.0F, -2.0F, -1.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.95F, 12.5F, -1.0F, 0.48F, 0.0F, 0.0F)
         );
         PartDefinition cube_r4 = LeftLeg.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create()
               .texOffs(48, 8)
               .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(48, 36)
               .addBox(5.5F, -2.0F, -1.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.3F, 12.0F, -1.0F, 0.7854F, 0.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }
}
