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

public class RoyalArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? RoyalArmorLayers.LeggingsLayer::createBodyLayer : RoyalArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? RoyalArmorLayers.LeggingsLayer::new : RoyalArmorLayers.MainLayer::new;
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
               .texOffs(0, 17)
               .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(40, 50)
               .addBox(-3.0F, -10.0F, -5.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 52)
               .addBox(-2.0F, -11.0F, -5.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 50)
               .addBox(-2.0F, -11.0F, 4.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 17)
               .addBox(-1.0F, -12.0F, -5.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 6)
               .addBox(-1.0F, -12.0F, 4.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(36, 0)
               .addBox(-3.0F, -10.0F, 4.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(32, 50)
               .addBox(-5.0F, -10.0F, -3.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(28, 0)
               .addBox(4.0F, -10.0F, -3.0F, 1.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(28, 3)
               .addBox(-4.0F, -10.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(28, 0)
               .addBox(-4.0F, -10.0F, 3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 20)
               .addBox(3.0F, -10.0F, 3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 17)
               .addBox(3.0F, -10.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(60, 32)
               .addBox(-5.0F, -11.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 3)
               .addBox(-5.0F, -12.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(38, 4)
               .addBox(4.0F, -11.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(4.0F, -12.0F, -1.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(27, 45)
               .addBox(-2.2F, -9.5F, -5.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(27, 41)
               .addBox(-2.2F, -9.5F, 4.15F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 44)
               .addBox(1.2F, -9.5F, -5.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 40)
               .addBox(1.2F, -9.5F, 4.15F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 22)
               .addBox(-0.4F, -10.5F, -5.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 19)
               .addBox(-0.4F, -10.5F, 4.15F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 22)
               .addBox(4.4F, -10.5F, -0.55F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 5)
               .addBox(4.4F, -9.5F, -2.15F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(44, 3)
               .addBox(4.4F, -9.5F, 1.15F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(27, 43)
               .addBox(-5.2F, -9.5F, 1.15F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 42)
               .addBox(-5.2F, -9.5F, -2.35F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 19)
               .addBox(-5.3F, -10.5F, -0.55F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 33)
               .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(1.01F))
               .texOffs(0, 0)
               .addBox(-5.0F, 1.0F, -4.0F, 10.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(24, 40)
               .addBox(-4.0F, 9.25F, -3.5F, 8.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(32, 58).addBox(-4.2417F, -4.0F, -1.4261F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.048F, 5.4F, -4.3293F, 0.2752F, 0.473F, 0.1279F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(44, 61).addBox(-2.5F, -4.0F, -0.5F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-2.0F, 5.3F, 4.55F, -0.3001F, -0.504F, 0.1483F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(44, 24).addBox(-0.7583F, -4.0F, -1.4261F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.048F, 5.4F, -4.3293F, 0.2752F, -0.473F, -0.1279F)
         );
         PartDefinition cube_r4 = body.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(56, 61).addBox(-2.5F, -4.0F, -0.5F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.8301F, 5.3F, 4.55F, -0.3001F, 0.504F, -0.1483F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(56, 16)
               .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(36, 3)
               .addBox(-6.25F, 2.75F, 2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 35)
               .addBox(-6.25F, 2.75F, 0.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 35)
               .addBox(-6.25F, 2.75F, -3.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(20, 33)
               .addBox(-6.25F, 2.75F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r5 = right_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create()
               .texOffs(47, 33)
               .addBox(-2.65F, -1.5F, -3.5F, 3.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(24, 25)
               .addBox(-3.0F, -3.5F, -4.1F, 6.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.6981F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(16, 50)
               .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F))
               .texOffs(4, 0)
               .addBox(5.25F, 2.75F, 0.35F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(24, 23)
               .addBox(5.25F, 2.75F, 2.15F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(4, 3)
               .addBox(5.25F, 2.75F, -1.35F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
               .texOffs(0, 33)
               .addBox(5.25F, 2.75F, -3.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r6 = left_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create()
               .texOffs(47, 47)
               .addBox(-0.45F, -1.5F, -3.5F, 3.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(28, 9)
               .addBox(-3.0F, -3.5F, -4.2F, 6.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(3.0F, -0.5F, 0.0F, 0.0F, 0.0F, -0.6981F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 49).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }
   }
}
