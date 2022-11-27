package iskallia.vault.gear.model.armor.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BumboCactoniArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? BumboCactoniArmorLayers.LeggingsLayer::createBodyLayer : BumboCactoniArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? BumboCactoniArmorLayers.LeggingsLayer::new : BumboCactoniArmorLayers.MainLayer::new;
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
         PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
         PartDefinition bone = head.addOrReplaceChild(
            "bone",
            CubeListBuilder.create()
               .texOffs(0, 33)
               .addBox(-15.25F, 3.0F, -13.75F, 30.0F, 2.0F, 27.0F, new CubeDeformation(0.0F))
               .texOffs(0, 0)
               .addBox(-17.25F, 1.0F, -15.75F, 34.0F, 2.0F, 31.0F, new CubeDeformation(0.0F))
               .texOffs(68, 85)
               .addBox(-7.25F, -8.0F, -5.75F, 15.0F, 9.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(68, 62)
               .addBox(-9.25F, -6.0F, -7.75F, 19.0F, 7.0F, 16.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.25F, -17.0F, -0.25F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 62).addBox(-10.0F, -11.0F, -7.0F, 20.0F, 35.0F, 14.0F, new CubeDeformation(1.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(99, 16)
               .addBox(-11.0F, 3.0F, -4.0F, 4.0F, 7.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(87, 33)
               .addBox(-19.0F, -6.0F, -4.0F, 6.0F, 16.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(0, 33)
               .addBox(7.0F, 0.0F, -4.0F, 4.0F, 7.0F, 8.0F, new CubeDeformation(1.0F))
               .texOffs(0, 0)
               .addBox(13.0F, -9.0F, -4.0F, 6.0F, 16.0F, 8.0F, new CubeDeformation(1.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(92, 106).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(68, 62).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 256, 256);
      }

      @Override
      public void adjustForFirstPersonRender(@Nonnull PoseStack poseStack) {
         if (Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT) {
            poseStack.translate(-1.7F, 0.5, -0.1F);
            poseStack.mulPose(Quaternion.fromXYZ(0.0F, 3.0F, 0.0F));
         } else {
            poseStack.translate(1.7F, 0.5, -0.2F);
            poseStack.mulPose(Quaternion.fromXYZ(0.0F, 3.4F, 0.0F));
         }
      }
   }
}
