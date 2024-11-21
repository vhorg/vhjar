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

public class CrystalPlateArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return CrystalPlateArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return CrystalPlateArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(32, 22).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(42, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(52, 51)
               .addBox(-5.0F, 7.0F, -4.0F, 10.0F, 15.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(32, 51)
               .addBox(-5.0F, 7.0F, 4.0F, 10.0F, 15.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-6.0F, -1.0F, -4.0F, 12.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(44, 72).addBox(0.4438F, -4.5F, -4.1548F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.9816F, 8.5F, -1.75F, 0.4215F, -0.3614F, -0.1572F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(8, 71).addBox(-2.7483F, -4.5F, -2.624F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.9816F, 8.5F, -1.75F, 0.4215F, 0.3614F, 0.1572F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(64, 44).addBox(0.4438F, -4.5F, -4.1548F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.9816F, 5.5F, -2.25F, 0.4215F, -0.3614F, -0.1572F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(42, 16).addBox(-2.7483F, -4.5F, -2.624F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.9816F, 5.5F, -2.25F, 0.4215F, 0.3614F, 0.1572F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(44, 66).addBox(0.4438F, -4.5F, -4.1548F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.9816F, 2.5F, -2.25F, 0.4215F, -0.3614F, -0.1572F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(58, 66).addBox(-2.7483F, -4.5F, -2.624F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.9816F, 2.5F, -2.25F, 0.4215F, 0.3614F, 0.1572F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(64, 28)
               .addBox(-3.1F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(32, 66)
               .addBox(-7.0F, -10.0F, -1.75F, 2.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 71)
               .addBox(-5.0F, -11.0F, -2.75F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 42)
               .addBox(-2.5F, -5.5F, -4.25F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 42)
               .addBox(-5.0F, -7.5F, 2.75F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(56, 38)
               .addBox(-7.75F, -8.0F, 1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(72, 50)
               .addBox(-7.75F, -7.0F, -2.5F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 18)
               .addBox(-3.0F, -9.0F, 1.25F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r7 = right_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(0, 30).addBox(-5.0F, -2.5F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.75F, 1.25F, 0.25F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition cube_r8 = right_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(0, 42).addBox(-4.0F, -2.5F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0F, -1.75F, 0.25F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(0, 30)
               .addBox(3.0F, -7.5F, 2.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(1.0F, -9.0F, 1.0F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(70, 70)
               .addBox(3.0F, -11.0F, -3.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(66, 0)
               .addBox(5.0F, -10.0F, -2.0F, 2.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(33, 0)
               .addBox(0.5F, -5.5F, -4.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(62, 12)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(22, 71)
               .addBox(5.75F, -7.0F, -2.75F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(30, 18)
               .addBox(5.75F, -8.0F, 0.75F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r9 = left_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(0, 18).addBox(-5.0F, -0.5F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.8787F, -0.5355F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition cube_r10 = left_arm.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(32, 38).addBox(-4.0F, -2.5F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(2.8787F, -2.0355F, 0.0F, 0.0F, 0.0F, -0.3927F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
