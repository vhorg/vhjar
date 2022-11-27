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

public class SkallibombaArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SkallibombaArmorLayers.LeggingsLayer::createBodyLayer : SkallibombaArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? SkallibombaArmorLayers.LeggingsLayer::new : SkallibombaArmorLayers.MainLayer::new;
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
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-9.0F, 14.0F, 4.0F, 18.0F, 8.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(0, 34)
               .addBox(-1.0F, 11.0F, 6.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(31, 20)
               .addBox(-1.0F, 13.0F, 17.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(12, 34)
               .addBox(-1.0F, 17.0F, 23.0F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 20)
               .addBox(-6.0F, 15.0F, 16.0F, 12.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
               .texOffs(31, 27)
               .addBox(-4.0F, 18.0F, 23.0F, 8.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
         PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
         return LayerDefinition.create(meshdefinition, 64, 64);
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
               .texOffs(111, 104)
               .addBox(-5.0F, -27.0F, -6.0F, 10.0F, 24.0F, 11.0F, new CubeDeformation(0.0F))
               .texOffs(100, 28)
               .addBox(-5.0F, -32.0F, -25.0F, 10.0F, 5.0F, 16.0F, new CubeDeformation(0.0F))
               .texOffs(106, 87)
               .addBox(-6.0F, -34.0F, -34.0F, 12.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
               .texOffs(85, 17)
               .addBox(-5.0F, -36.0F, -34.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(98, 0)
               .addBox(-5.0F, -41.0F, -1.0F, 2.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(0, 16)
               .addBox(3.0F, -36.0F, -34.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(76, 10)
               .addBox(3.0F, -41.0F, -1.0F, 2.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
               .texOffs(15, 18)
               .addBox(3.0F, -27.0F, -33.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(9, 16)
               .addBox(-5.0F, -27.0F, -33.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(74, 59)
               .addBox(-8.0F, -34.0F, -9.0F, 16.0F, 12.0F, 16.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(34, 115).addBox(-3.0F, 0.5F, -8.0F, 7.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, -22.5F, -17.0F, 0.3491F, 0.0F, 0.0F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-13.0F, -10.0F, -14.0F, 26.0F, 35.0F, 24.0F, new CubeDeformation(0.0F))
               .texOffs(0, 59)
               .addBox(-13.0F, -3.0F, -25.0F, 26.0F, 28.0F, 11.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 126).addBox(-0.5F, -5.0F, -7.0F, 1.0F, 10.0F, 14.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.5F, -10.0F, 15.0F, 0.3491F, -0.3054F, 0.0F)
         );
         PartDefinition cube_r3 = body.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(92, 130).addBox(-0.5F, -5.0F, -7.0F, 1.0F, 10.0F, 14.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.5F, -10.0F, 16.0F, 0.2618F, 0.3054F, 0.0F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create()
               .texOffs(122, 49)
               .addBox(-12.0F, -9.0F, -9.0F, 7.0F, 10.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(63, 59)
               .addBox(-12.0F, -3.0F, -15.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create()
               .texOffs(68, 122)
               .addBox(6.0F, -9.0F, -9.0F, 7.0F, 10.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(76, 0)
               .addBox(8.0F, -3.0F, -15.0F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create()
               .texOffs(100, 0)
               .addBox(-22.1F, -3.0F, -9.0F, 13.0F, 16.0F, 12.0F, new CubeDeformation(0.0F))
               .texOffs(138, 0)
               .addBox(-22.1F, 10.0F, -15.0F, 13.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create()
               .texOffs(136, 28)
               .addBox(9.1F, 10.0F, -15.0F, 13.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(0, 98)
               .addBox(9.1F, -3.0F, -9.0F, 13.0F, 16.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         return LayerDefinition.create(meshdefinition, 256, 256);
      }

      @Override
      public void adjustForFirstPersonRender(@Nonnull PoseStack poseStack) {
         if (Minecraft.getInstance().options.mainHand == HumanoidArm.RIGHT) {
            poseStack.translate(-0.1F, 0.7F, -0.7F);
            poseStack.scale(0.8F, 0.8F, 0.8F);
            poseStack.mulPose(Quaternion.fromXYZ(0.1F, 1.5F, 0.0F));
         } else {
            poseStack.translate(0.0, 0.6F, -0.7F);
            poseStack.scale(0.8F, 0.8F, 0.8F);
            poseStack.mulPose(Quaternion.fromXYZ(-0.1F, -1.4F, 0.0F));
         }
      }
   }
}
