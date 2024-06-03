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

public class CastleArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return CastleArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return CastleArmorLayers.MainLayer::new;
   }

   @OnlyIn(Dist.CLIENT)
   public static class MainLayer extends ArmorLayers.MainLayer {
      public MainLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(24, 36).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(54, 108)
               .addBox(1.25F, 4.0F, -2.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(49, 88)
               .addBox(1.25F, -4.0F, -3.0F, 2.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.8301F, 16.6077F, -2.0F, 0.0F, 0.0F, -0.48F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create()
               .texOffs(36, 115)
               .addBox(-3.0F, 4.0F, -2.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(35, 76)
               .addBox(-3.0F, -4.0F, -3.0F, 2.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.0F, 16.0F, -2.0F, 0.0F, 0.0F, 0.48F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(16, 52).addBox(-2.5F, -4.0F, 1.0F, 5.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.25F, 9.0F, -7.0F, 0.0F, 0.7854F, 0.0F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(26, 52).addBox(-2.5F, -4.0F, 1.0F, 5.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(4.75F, 9.0F, -7.0F, 0.0F, -0.7854F, 0.0F)
         );
         PartDefinition cube_r5 = body.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(0, 15)
               .addBox(5.5F, -2.0F, -2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 11)
               .addBox(1.5F, -2.0F, -2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-2.5F, -2.0F, 5.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 4)
               .addBox(-2.5F, -2.0F, 1.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(25, 4)
               .addBox(-2.5F, -2.0F, -2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(48, 4)
               .addBox(-2.5F, 0.0F, -2.5F, 11.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0178F, 1.0F, -8.6464F, 0.5299F, -0.7119F, -0.3655F)
         );
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(25, 0).addBox(-8.5F, 0.0F, -2.5F, 11.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0178F, 1.0F, -8.6464F, 0.5299F, 0.7119F, 0.3655F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(28, 22).addBox(-2.5F, -5.0F, -2.5F, 11.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0178F, 6.0F, -5.6464F, 0.5299F, -0.7119F, -0.3655F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(0, 38).addBox(-7.5F, -5.0F, -2.5F, 10.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.0178F, 6.0F, -5.6464F, 0.5299F, 0.7119F, 0.3655F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(0, 52)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(0, 11)
               .addBox(-8.0F, -4.0F, -4.5F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
               .texOffs(27, 4)
               .addBox(-7.0F, -2.0F, -3.5F, 7.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(52, 56)
               .addBox(-8.0F, -6.0F, 2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(36, 56)
               .addBox(-8.0F, -6.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(44, 56)
               .addBox(-8.0F, -6.0F, -4.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(55, 8)
               .addBox(-2.0F, -6.0F, -4.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(54, 32)
               .addBox(-5.0F, -6.0F, -4.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(54, 28)
               .addBox(-5.0F, -6.0F, 2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(55, 12)
               .addBox(-2.0F, -6.0F, 2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(55, 16)
               .addBox(-2.0F, -6.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(0, 22)
               .addBox(0.0F, -2.0F, -3.5F, 7.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(55, 16)
               .addBox(0.0F, -6.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(0.0F, -4.0F, -4.5F, 8.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
               .texOffs(44, 36)
               .addBox(0.0F, -6.0F, 2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(51, 0)
               .addBox(3.0F, -6.0F, 2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(52, 20)
               .addBox(6.0F, -6.0F, 2.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(36, 52)
               .addBox(6.0F, -6.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(44, 52)
               .addBox(6.0F, -6.0F, -4.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(52, 52)
               .addBox(3.0F, -6.0F, -4.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(54, 24)
               .addBox(0.0F, -6.0F, -4.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(48, 36)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
