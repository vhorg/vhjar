package iskallia.vault.gear.model.armor.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.util.ModelPartHelper;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BeeKnightArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? BeeKnightArmorLayers.LeggingsLayer::createBodyLayer : BeeKnightArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? BeeKnightArmorLayers.LeggingsLayer::new : BeeKnightArmorLayers.MainLayer::new;
   }

   @OnlyIn(Dist.CLIENT)
   public static class LeggingsLayer extends ArmorLayers.LeggingsLayer {
      public LeggingsLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
         PartDefinition cube_r1 = body.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(1, 1).addBox(-4.0F, -2.75F, -2.5F, 8.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r2 = body.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -2.75F, -2.5F, 9.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, 11.75F, 0.5F, -3.1416F, 0.0F, 3.1416F)
         );
         return LayerDefinition.create(meshdefinition, 64, 64);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MainLayer extends ArmorLayers.MainLayer {
      protected ModelPart leftWing;
      protected ModelPart rightWing;

      public MainLayer(ArmorPieceModel definition, ModelPart root) {
         super(definition, root);
         ModelPart body = root.getChild("body");
         this.leftWing = body.getChild("rightWing_r1");
         this.rightWing = body.getChild("leftWing_r1");
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(27, 0).addBox(-5.5F, -2.0F, -2.0F, 10.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.5F, -7.25F, -3.25F, -2.7053F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.0F, -5.0F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, 0.25F, -0.25F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(27, 24).addBox(-6.0F, -3.0F, -7.0F, 9.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.5F, 1.7651F, -2.0841F, 2.9234F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r4 = head.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(20, 57).addBox(-1.0F, -7.0F, 0.0F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.5F, -8.75F, 1.75F, 2.7053F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r5 = head.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(0, 29).addBox(-1.0F, -7.0F, 0.0F, 0.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-3.5F, -8.75F, 1.75F, 2.7053F, 0.0F, 3.1416F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
         PartDefinition cube_r6 = body.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(88, 11).mirror().addBox(-1.5F, -1.5F, -4.5F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offsetAndRotation(4.5F, 0.0F, -0.75F, -3.1416F, 0.0F, -2.8362F)
         );
         PartDefinition cube_r7 = body.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(88, 11).addBox(-1.5F, -1.5F, -4.5F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-4.5F, 0.0F, -0.75F, -3.1416F, 0.0F, 2.8362F)
         );
         PartDefinition rightWing_r1 = body.addOrReplaceChild(
            "rightWing_r1",
            CubeListBuilder.create().texOffs(32, 26).addBox(0.416F, -4.0F, -8.9607F, 0.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.1F, 4.0F, 4.5F, -3.1416F, 0.3927F, 3.1416F)
         );
         PartDefinition leftWing_r1 = body.addOrReplaceChild(
            "leftWing_r1",
            CubeListBuilder.create().texOffs(32, 35).addBox(-0.4045F, -4.0F, -9.0499F, 0.0F, 9.0F, 10.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.1F, 4.0F, 4.5F, -3.1416F, -0.48F, 3.1416F)
         );
         PartDefinition cube_r8 = body.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(0, 18).addBox(-5.0F, -5.0F, -5.0F, 11.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, 5.25F, -1.5F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r9 = body.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(36, 8).addBox(-4.0F, -22.0F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(1.01F)),
            PartPose.offsetAndRotation(0.0F, 24.0F, 0.0F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
         PartDefinition cube_r10 = right_arm.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(54, 21).addBox(-1.0F, 2.0F, -2.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.25F, 4.0F, 1.0F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r11 = right_arm.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(0, 60).addBox(-1.0F, -1.0F, -2.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.25F, 8.0F, 0.5F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r12 = right_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(46, 48).addBox(-1.0F, -2.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.25F, -1.0F, 0.0F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r13 = right_arm.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create().texOffs(52, 87).addBox(6.0F, -23.0F, -2.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(1.0F)),
            PartPose.offsetAndRotation(5.0F, 22.0F, -0.5F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));
         PartDefinition cube_r14 = left_arm.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(16, 54).addBox(-5.0F, 2.0F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.25F, 4.0F, 0.0F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r15 = left_arm.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create().texOffs(55, 59).addBox(-4.0F, -1.0F, -2.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.75F, 8.0F, 0.5F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r16 = left_arm.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create().texOffs(0, 49).addBox(-4.0F, -2.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-0.75F, -1.0F, 0.0F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r17 = left_arm.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create().texOffs(18, 87).addBox(-8.0F, -23.0F, -1.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(1.0F)),
            PartPose.offsetAndRotation(-6.0F, 22.0F, 0.5F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
         PartDefinition cube_r18 = right_leg.addOrReplaceChild(
            "cube_r18",
            CubeListBuilder.create().texOffs(54, 14).addBox(-2.0F, -1.0F, -2.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.8F, 8.0F, 1.25F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r19 = right_leg.addOrReplaceChild(
            "cube_r19",
            CubeListBuilder.create().texOffs(41, 20).addBox(-2.0F, -2.0F, -2.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.3F, 12.0F, -4.25F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r20 = right_leg.addOrReplaceChild(
            "cube_r20",
            CubeListBuilder.create().texOffs(56, 0).addBox(-2.0F, -4.0F, -2.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.3F, 12.0F, 0.75F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r21 = right_leg.addOrReplaceChild(
            "cube_r21",
            CubeListBuilder.create().texOffs(0, 86).addBox(-0.1F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offsetAndRotation(1.9F, 12.0F, 0.0F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
         PartDefinition cube_r22 = left_leg.addOrReplaceChild(
            "cube_r22",
            CubeListBuilder.create().texOffs(52, 36).addBox(-2.0F, -1.0F, -2.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.0F, 8.0F, 1.25F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r23 = left_leg.addOrReplaceChild(
            "cube_r23",
            CubeListBuilder.create().texOffs(29, 20).addBox(-2.0F, -2.0F, -2.0F, 5.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, 12.0F, -4.25F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r24 = left_leg.addOrReplaceChild(
            "cube_r24",
            CubeListBuilder.create().texOffs(35, 59).addBox(-2.0F, -4.0F, -2.0F, 5.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.5F, 12.0F, 0.75F, -3.1416F, 0.0F, 3.1416F)
         );
         PartDefinition cube_r25 = left_leg.addOrReplaceChild(
            "cube_r25",
            CubeListBuilder.create().texOffs(35, 86).addBox(-3.9F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(1.0F)),
            PartPose.offsetAndRotation(-1.9F, 12.0F, 0.0F, -3.1416F, 0.0F, 3.1416F)
         );
         return LayerDefinition.create(meshdefinition, 128, 128);
      }

      @Override
      public void renderToBuffer(
         @Nonnull PoseStack poseStack,
         @Nonnull VertexConsumer vertexConsumer,
         int packedLight,
         int packedOverlay,
         float red,
         float green,
         float blue,
         float alpha
      ) {
         ModelPartHelper.runPreservingTransforms(() -> {
            this.animateParts();
            super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
         }, this.rightWing, this.leftWing);
      }

      private void animateParts() {
         this.leftWing.xRot = Mth.map((float)Math.sin(System.currentTimeMillis() / 1000.0), -1.0F, 1.0F, 0.0F, 0.17453294F);
         this.leftWing.yRot = Mth.map((float)Math.sin(System.currentTimeMillis() / 250.0), -1.0F, 1.0F, 2.8274336F, (float) (Math.PI * 3.0 / 4.0));
         this.rightWing.xRot = this.leftWing.xRot;
         this.rightWing.yRot = -this.leftWing.yRot;
      }
   }
}
