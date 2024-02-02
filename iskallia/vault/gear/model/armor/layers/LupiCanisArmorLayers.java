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

public class LupiCanisArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? LupiCanisArmorLayers.LeggingsLayer::createBodyLayer : LupiCanisArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? LupiCanisArmorLayers.LeggingsLayer::new : LupiCanisArmorLayers.MainLayer::new;
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
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F))
               .texOffs(28, 10)
               .addBox(-6.0F, 11.0F, -4.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(0, 32)
               .addBox(-6.0F, 9.0F, -4.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(12, 16)
               .addBox(-5.5F, 10.0F, -3.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(32, 17)
               .addBox(-2.5F, 10.0F, -3.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(12, 32).addBox(-1.0F, -5.0191F, -4.4048F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, 12.0259F, 0.9202F, 0.3927F, 0.3927F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(29, 29).addBox(-1.5F, -3.6459F, -3.2905F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, 12.0259F, 0.9202F, 1.1781F, 0.3927F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, -2.3745F, -2.779F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.0F, 12.0259F, 0.9202F, 1.9635F, 0.3927F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
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
            CubeListBuilder.create()
               .texOffs(28, 23)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 0)
               .addBox(-6.0F, -6.0F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-1.0F, -11.0F, -7.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(36, 0)
               .addBox(-1.5F, -16.0F, -6.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 31)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(32, 55)
               .addBox(3.25F, 1.0F, 9.25F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(24, 55)
               .addBox(-5.25F, 1.0F, 9.25F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(52, 16)
               .addBox(-5.25F, 1.0F, 3.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(54, 53)
               .addBox(3.25F, 1.0F, 3.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 13)
               .addBox(-5.0F, 1.75F, 3.0F, 10.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create()
               .texOffs(0, 7)
               .addBox(-4.0F, -4.0F, 7.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(0, 13)
               .addBox(6.0F, -4.0F, 7.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(28, 13)
               .addBox(-2.0F, -5.0F, 7.0F, 8.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(16, 47)
               .addBox(1.0F, -2.0F, 7.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
         PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(40, 39).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(24, 39).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
