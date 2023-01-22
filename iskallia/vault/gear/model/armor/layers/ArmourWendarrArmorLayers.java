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

public class ArmourWendarrArmorLayers extends ArmorLayers {
   @OnlyIn(Dist.CLIENT)
   @Override
   public Supplier<LayerDefinition> getGeometrySupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS
         ? ArmourWendarrArmorLayers.LeggingsLayer::createBodyLayer
         : ArmourWendarrArmorLayers.MainLayer::createBodyLayer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public ArmorLayers.VaultArmorLayerSupplier<? extends ArmorLayers.BaseLayer> getLayerSupplier(EquipmentSlot equipmentSlot) {
      return equipmentSlot == EquipmentSlot.LEGS ? ArmourWendarrArmorLayers.LeggingsLayer::new : ArmourWendarrArmorLayers.MainLayer::new;
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
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -1.0F, -2.5F, 9.0F, 13.0F, 5.0F, new CubeDeformation(0.01F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 19).addBox(-2.4F, -0.5F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 19).mirror().addBox(-2.6F, -0.5F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
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
         this.leftWing = body.getChild("left_wing");
         this.rightWing = body.getChild("right_wing");
      }

      public static LayerDefinition createBodyLayer() {
         MeshDefinition meshdefinition = createBaseLayer();
         PartDefinition partdefinition = meshdefinition.getRoot();
         PartDefinition head = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create()
               .texOffs(35, 35)
               .addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(0, 26)
               .addBox(-6.0F, -17.0F, -5.0F, 12.0F, 8.0F, 10.0F, new CubeDeformation(0.1F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition cube_r1 = head.addOrReplaceChild(
            "cube_r1",
            CubeListBuilder.create().texOffs(79, 54).addBox(-1.75F, -0.25F, -5.0F, 2.0F, 2.0F, 10.0F, new CubeDeformation(0.2F)),
            PartPose.offsetAndRotation(0.0F, -11.0F, 0.0F, 0.0F, 0.0F, -0.7854F)
         );
         PartDefinition cube_r2 = head.addOrReplaceChild(
            "cube_r2",
            CubeListBuilder.create().texOffs(50, 60).addBox(-0.25F, -3.9087F, 0.3901F, 0.0F, 7.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(5.05F, -3.5075F, -0.633F, 0.6333F, 0.2489F, 0.1789F)
         );
         PartDefinition cube_r3 = head.addOrReplaceChild(
            "cube_r3",
            CubeListBuilder.create().texOffs(64, 44).addBox(0.25F, -3.9087F, 0.3901F, 0.0F, 7.0F, 12.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-5.05F, -3.5075F, -0.633F, 0.6333F, -0.2489F, -0.1789F)
         );
         PartDefinition left_arm = partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(66, 20).addBox(-1.0F, -2.0F, -3.0F, 5.0F, 13.0F, 6.0F, new CubeDeformation(0.25F)),
            PartPose.offset(5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r4 = left_arm.addOrReplaceChild(
            "cube_r4",
            CubeListBuilder.create().texOffs(55, 20).addBox(1.25F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(9.0141F, 3.2945F, 8.8729F, 0.5713F, 0.5724F, -1.8783F)
         );
         PartDefinition cube_r5 = left_arm.addOrReplaceChild(
            "cube_r5",
            CubeListBuilder.create().texOffs(22, 95).addBox(-1.6412F, -0.3145F, -1.1338F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(5.4578F, 1.1274F, 4.9472F, 0.7854F, 0.0F, -1.1781F)
         );
         PartDefinition cube_r6 = left_arm.addOrReplaceChild(
            "cube_r6",
            CubeListBuilder.create().texOffs(92, 84).addBox(-2.5F, -1.75F, -3.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(3.8436F, -1.7341F, 5.0F, 0.7137F, -0.3614F, -0.7905F)
         );
         PartDefinition cube_r7 = left_arm.addOrReplaceChild(
            "cube_r7",
            CubeListBuilder.create().texOffs(60, 20).addBox(1.25F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(9.0141F, 3.2945F, -8.8729F, -0.5713F, -0.5724F, -1.8783F)
         );
         PartDefinition cube_r8 = left_arm.addOrReplaceChild(
            "cube_r8",
            CubeListBuilder.create().texOffs(31, 95).addBox(-1.6412F, -0.3145F, -0.8662F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(5.4578F, 1.1274F, -4.9472F, -0.7854F, 0.0F, -1.1781F)
         );
         PartDefinition cube_r9 = left_arm.addOrReplaceChild(
            "cube_r9",
            CubeListBuilder.create().texOffs(0, 93).addBox(-2.5F, -1.75F, 0.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(3.8436F, -1.7341F, -5.0F, -0.7137F, 0.3614F, -0.7905F)
         );
         PartDefinition cube_r10 = left_arm.addOrReplaceChild(
            "cube_r10",
            CubeListBuilder.create().texOffs(68, 73).addBox(-2.25F, -3.0F, -3.5F, 6.0F, 6.0F, 7.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(2.5F, 0.0F, 0.0F, 0.0F, 0.0F, -1.1781F)
         );
         PartDefinition right_arm = partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(0, 65).addBox(-4.0F, -2.0F, -3.0F, 5.0F, 13.0F, 6.0F, new CubeDeformation(0.25F)),
            PartPose.offset(-5.0F, 2.0F, 0.0F)
         );
         PartDefinition cube_r11 = right_arm.addOrReplaceChild(
            "cube_r11",
            CubeListBuilder.create().texOffs(0, 45).addBox(-2.25F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(-9.0141F, 3.2945F, 8.8729F, 0.5713F, -0.5724F, 1.8783F)
         );
         PartDefinition cube_r12 = right_arm.addOrReplaceChild(
            "cube_r12",
            CubeListBuilder.create().texOffs(0, 26).addBox(-0.3588F, -0.3145F, -1.1338F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(-5.4578F, 1.1274F, 4.9472F, 0.7854F, 0.0F, 1.1781F)
         );
         PartDefinition cube_r13 = right_arm.addOrReplaceChild(
            "cube_r13",
            CubeListBuilder.create().texOffs(88, 67).addBox(-0.5F, -1.75F, -3.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(-3.8436F, -1.7341F, 5.0F, 0.7137F, 0.3614F, 0.7905F)
         );
         PartDefinition cube_r14 = right_arm.addOrReplaceChild(
            "cube_r14",
            CubeListBuilder.create().texOffs(55, 0).addBox(-2.25F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(-9.0141F, 3.2945F, -8.8729F, -0.5713F, 0.5724F, 1.8783F)
         );
         PartDefinition cube_r15 = right_arm.addOrReplaceChild(
            "cube_r15",
            CubeListBuilder.create().texOffs(94, 53).addBox(-0.3588F, -0.3145F, -0.8662F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(-5.4578F, 1.1274F, -4.9472F, -0.7854F, 0.0F, 1.1781F)
         );
         PartDefinition cube_r16 = right_arm.addOrReplaceChild(
            "cube_r16",
            CubeListBuilder.create().texOffs(89, 25).addBox(-0.5F, -1.75F, 0.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(-3.8436F, -1.7341F, -5.0F, -0.7137F, -0.3614F, 0.7905F)
         );
         PartDefinition cube_r17 = right_arm.addOrReplaceChild(
            "cube_r17",
            CubeListBuilder.create().texOffs(23, 72).addBox(-3.75F, -3.0F, -3.5F, 6.0F, 6.0F, 7.0F, new CubeDeformation(0.25F)),
            PartPose.offsetAndRotation(-2.5F, 0.0F, 0.0F, 0.0F, 0.0F, 1.1781F)
         );
         PartDefinition body = partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create()
               .texOffs(55, 0)
               .addBox(-4.5F, 0.0F, -3.0F, 9.0F, 13.0F, 6.0F, new CubeDeformation(0.26F))
               .texOffs(0, 45)
               .addBox(-4.5F, 0.0F, -3.0F, 9.0F, 13.0F, 6.0F, new CubeDeformation(0.5F))
               .texOffs(25, 45)
               .addBox(-1.5F, 5.0F, 3.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.26F)),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
         PartDefinition timelayer_r1 = body.addOrReplaceChild(
            "timelayer_r1",
            CubeListBuilder.create().texOffs(31, 56).addBox(-6.5F, -7.5F, 0.0F, 15.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, 3.5F, 8.5F, 0.0F, 0.0F, 1.5708F)
         );
         PartDefinition cube_r18 = body.addOrReplaceChild(
            "cube_r18",
            CubeListBuilder.create().texOffs(63, 64).addBox(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.26F)),
            PartPose.offsetAndRotation(0.0F, 3.5F, 4.5F, 0.0F, 0.0F, 0.7854F)
         );
         PartDefinition left_wing = body.addOrReplaceChild(
            "left_wing", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, 3.0F, 4.0F, 0.0F, 0.0F, -0.3054F)
         );
         PartDefinition cube_r19 = left_wing.addOrReplaceChild(
            "cube_r19",
            CubeListBuilder.create()
               .texOffs(0, 13)
               .addBox(-4.0F, 1.5F, 0.0F, 27.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(27, 86)
               .addBox(14.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(86, 0)
               .addBox(4.0F, -1.5F, -1.5F, 10.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(83, 16)
               .addBox(-5.0F, -2.0F, -2.0F, 9.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(1.2984F, -1.0843F, 0.3724F, 0.2795F, -0.6485F, -0.4436F)
         );
         PartDefinition cube_r20 = left_wing.addOrReplaceChild(
            "cube_r20",
            CubeListBuilder.create()
               .texOffs(92, 93)
               .addBox(-1.0F, 4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(79, 87)
               .addBox(-1.5F, -4.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(15.4836F, -3.5674F, 12.2752F, 0.5157F, -0.4937F, -0.8745F)
         );
         PartDefinition right_wing = body.addOrReplaceChild(
            "right_wing", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.3054F)
         );
         PartDefinition cube_r21 = right_wing.addOrReplaceChild(
            "cube_r21",
            CubeListBuilder.create()
               .texOffs(0, 0)
               .addBox(-23.0F, 1.5F, 0.0F, 27.0F, 12.0F, 0.0F, new CubeDeformation(0.0F))
               .texOffs(86, 7)
               .addBox(-18.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
               .texOffs(0, 86)
               .addBox(-14.0F, -1.5F, -1.5F, 10.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
               .texOffs(35, 26)
               .addBox(-4.0F, -2.0F, -2.0F, 9.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-1.2984F, -1.0843F, 0.3724F, 0.2795F, 0.6485F, 0.4436F)
         );
         PartDefinition cube_r22 = right_wing.addOrReplaceChild(
            "cube_r22",
            CubeListBuilder.create()
               .texOffs(13, 93)
               .addBox(-1.0F, 4.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
               .texOffs(66, 87)
               .addBox(-1.5F, -4.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(-15.4836F, -3.5674F, 12.2752F, 0.5157F, 0.4937F, 0.8745F)
         );
         PartDefinition left_leg = partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(84, 35).addBox(-2.4F, 0.25F, -2.5F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.5F)),
            PartPose.offset(1.9F, 12.0F, 0.0F)
         );
         PartDefinition right_leg = partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(45, 81).addBox(-2.6F, 0.25F, -2.5F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.5F)),
            PartPose.offset(-1.9F, 12.0F, 0.0F)
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
         this.leftWing.yRot = Mth.map((float)Math.sin(System.currentTimeMillis() / 500.0), -1.0F, 1.0F, 0.0F, (float) (Math.PI / 6));
         this.rightWing.xRot = this.leftWing.xRot;
         this.rightWing.yRot = -this.leftWing.yRot;
      }
   }
}
