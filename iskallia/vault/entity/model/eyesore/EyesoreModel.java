package iskallia.vault.entity.model.eyesore;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.entity.entity.eyesore.EyesoreEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class EyesoreModel extends HierarchicalModel<EyesoreEntity> {
   private final ModelPart root;
   private final ModelPart body;
   private final List<ModelPart> tentacles;

   public EyesoreModel(ModelPart root) {
      this.root = root;
      this.tentacles = new ArrayList<>();
      this.body = root.getChild("body");

      for (int i = 0; i < 9; i++) {
         this.tentacles.add(this.body.getChild("tentacles_" + i));
      }
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(14, 32)
            .addBox(-3.0F, -5.0F, 8.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(48, 7)
            .addBox(-3.0F, -9.0F, 6.0F, 6.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48)
            .addBox(-3.0F, -9.0F, 1.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48)
            .addBox(-3.0F, -9.0F, -4.0F, 6.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(41, 47)
            .addBox(-3.0F, -9.0F, -9.0F, 6.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(14, 32)
            .addBox(-3.0F, 0.0F, 8.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(70, 30)
            .addBox(-3.0F, 6.0F, -9.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(48, 0)
            .addBox(-3.0F, 6.0F, -7.0F, 6.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(56, 53)
            .addBox(-6.0F, 6.0F, -7.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(46, 53)
            .addBox(4.0F, 6.0F, -7.0F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(70, 28)
            .addBox(-8.0F, 5.0F, -9.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(66, 2)
            .addBox(2.0F, 5.0F, -9.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(14, 37)
            .addBox(-8.0F, 1.0F, -9.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(48, 13)
            .addBox(-3.0F, 2.0F, -9.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(19, 45)
            .addBox(-5.0F, -7.5F, -11.0F, 10.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(42, 42)
            .addBox(-6.0F, -5.75F, -10.0F, 12.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(37, 32)
            .addBox(-5.0F, -4.5F, -12.0F, 10.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(63, 0)
            .addBox(2.0F, 1.0F, -9.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 16.0F, 0.0F)
      );
      PartDefinition cube_r1 = body.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(42, 66)
            .addBox(12.5F, -4.5F, 0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(24, 59)
            .addBox(7.5F, -3.5F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 48)
            .addBox(4.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(36, 66)
            .addBox(-0.5F, -8.5F, 0.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-6.5F, 5.5F, -8.5F, 0.6981F, 0.0F, 0.0F)
      );
      PartDefinition cube_r2 = body.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create()
            .texOffs(26, 51)
            .addBox(7.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(37, 32)
            .addBox(0.5F, -1.5F, -0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-4.5F, 2.6823F, -8.2535F, -0.7418F, 0.0F, 0.0F)
      );
      PartDefinition cube_r3 = body.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create()
            .texOffs(66, 51)
            .addBox(0.0F, 0.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(14, 70)
            .addBox(1.0F, -2.0F, 3.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(70, 41)
            .addBox(0.0F, -1.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(6.0F, 7.0F, -6.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r4 = body.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create()
            .texOffs(0, 32)
            .addBox(-3.0F, -3.0F, 7.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(22, 71)
            .addBox(-3.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-6.0F, 9.0F, -6.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r5 = body.addOrReplaceChild(
         "cube_r5",
         CubeListBuilder.create().texOffs(54, 68).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-8.389F, 6.9281F, -2.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r6 = body.addOrReplaceChild(
         "cube_r6",
         CubeListBuilder.create()
            .texOffs(0, 32)
            .addBox(8.0F, -6.0F, -3.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(22, 32)
            .addBox(11.0F, -4.0F, -3.0F, 4.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(16, 51)
            .addBox(-15.0F, -3.0F, -7.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(22, 32)
            .addBox(-15.0F, -4.0F, -3.0F, 4.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(0, 32)
            .addBox(-11.0F, -6.0F, -3.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F)
      );
      PartDefinition LeftEyestalks = body.addOrReplaceChild("LeftEyestalks", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition cube_r7 = LeftEyestalks.addOrReplaceChild(
         "cube_r7",
         CubeListBuilder.create()
            .texOffs(61, 29)
            .addBox(7.0F, -1.5F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(44, 38)
            .addBox(1.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.0F, 3.0F, 4.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition cube_r8 = LeftEyestalks.addOrReplaceChild(
         "cube_r8",
         CubeListBuilder.create()
            .texOffs(61, 29)
            .addBox(7.0F, -1.6F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(44, 38)
            .addBox(1.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(7.0F, 5.0F, 0.0F, 0.0F, 0.0F, 0.6109F)
      );
      PartDefinition RightEyestalks = body.addOrReplaceChild("RightEyestalks", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition cube_r9 = RightEyestalks.addOrReplaceChild(
         "cube_r9",
         CubeListBuilder.create()
            .texOffs(61, 29)
            .addBox(-8.5F, -1.5F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(57, 47)
            .addBox(-5.5F, -1.0F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-7.5F, 3.0F, 5.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition cube_r10 = RightEyestalks.addOrReplaceChild(
         "cube_r10",
         CubeListBuilder.create()
            .texOffs(61, 29)
            .addBox(-10.0F, -1.5F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(44, 38)
            .addBox(-7.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-7.0F, 5.0F, 1.0F, 0.0F, 0.0F, -0.6109F)
      );
      PartDefinition tentacles_0 = body.addOrReplaceChild("tentacles_0", CubeListBuilder.create(), PartPose.offset(-3.8F, 7.0F, -5.0F));
      PartDefinition tentacles_1_r1 = tentacles_0.addOrReplaceChild(
         "tentacles_1_r1",
         CubeListBuilder.create().texOffs(63, 65).addBox(-1.5F, -2.4659F, -1.2412F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -22.0469F, -2.9417F, 0.2618F, 0.0F, 0.0F)
      );
      PartDefinition tentacles_0_r1 = tentacles_0.addOrReplaceChild(
         "tentacles_0_r1",
         CubeListBuilder.create().texOffs(46, 62).addBox(-0.8F, -8.0F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-0.2F, -14.0F, 0.0F, 0.2618F, 0.0F, 0.0F)
      );
      PartDefinition tentacles_1 = body.addOrReplaceChild("tentacles_1", CubeListBuilder.create(), PartPose.offset(1.3F, 7.0F, -5.0F));
      PartDefinition tentacles_2_r1 = tentacles_1.addOrReplaceChild(
         "tentacles_2_r1",
         CubeListBuilder.create().texOffs(24, 65).addBox(-1.5F, -2.2599F, -2.6927F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -24.3238F, 3.1091F, -0.2182F, 0.0F, 0.0F)
      );
      PartDefinition tentacles_1_r2 = tentacles_1.addOrReplaceChild(
         "tentacles_1_r2",
         CubeListBuilder.create().texOffs(38, 53).addBox(-1.0F, -10.5F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -13.5F, 0.0F, -0.2182F, 0.0F, 0.0F)
      );
      PartDefinition tentacles_2 = body.addOrReplaceChild("tentacles_2", CubeListBuilder.create(), PartPose.offset(6.3F, 7.0F, -5.0F));
      PartDefinition tentacles_3_r1 = tentacles_2.addOrReplaceChild(
         "tentacles_3_r1",
         CubeListBuilder.create().texOffs(64, 22).addBox(-1.7588F, -2.4659F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(2.2F, -21.2104F, -0.5F, 0.0F, 0.0F, 0.2618F)
      );
      PartDefinition tentacles_2_r2 = tentacles_2.addOrReplaceChild(
         "tentacles_2_r2",
         CubeListBuilder.create().texOffs(6, 66).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -14.0F, 0.0F, 0.0F, 0.0F, 0.2618F)
      );
      PartDefinition tentacles_3 = body.addOrReplaceChild("tentacles_3", CubeListBuilder.create(), PartPose.offset(-6.3F, 7.0F, 0.0F));
      PartDefinition tentacles_4_r1 = tentacles_3.addOrReplaceChild(
         "tentacles_4_r1",
         CubeListBuilder.create().texOffs(63, 10).addBox(-1.3264F, -2.4848F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.9101F, -23.3329F, -0.5F, 0.0F, 0.0F, -0.1745F)
      );
      PartDefinition tentacles_3_r2 = tentacles_3.addOrReplaceChild(
         "tentacles_3_r2",
         CubeListBuilder.create().texOffs(16, 59).addBox(-1.0F, -9.5F, -1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -13.5F, 0.0F, 0.0F, 0.0F, -0.1745F)
      );
      PartDefinition tentacles_4 = body.addOrReplaceChild("tentacles_4", CubeListBuilder.create(), PartPose.offset(-1.3F, 7.0F, 0.0F));
      PartDefinition tentacles_5_r1 = tentacles_4.addOrReplaceChild(
         "tentacles_5_r1",
         CubeListBuilder.create().texOffs(63, 59).addBox(-1.4564F, -2.6918F, -2.2601F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-0.6766F, -26.9978F, -2.9512F, 0.2182F, 0.0F, -0.0436F)
      );
      PartDefinition tentacles_4_r2 = tentacles_4.addOrReplaceChild(
         "tentacles_4_r2",
         CubeListBuilder.create().texOffs(8, 0).addBox(-1.0F, -14.5F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -12.5F, 0.0F, 0.2182F, 0.0F, -0.0436F)
      );
      PartDefinition tentacles_5 = body.addOrReplaceChild("tentacles_5", CubeListBuilder.create(), PartPose.offset(3.8F, 7.0F, 0.0F));
      PartDefinition tentacles_6_r1 = tentacles_5.addOrReplaceChild(
         "tentacles_6_r1",
         CubeListBuilder.create().texOffs(64, 16).addBox(-1.7588F, -2.4623F, -1.4158F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(3.3406F, -23.9672F, -1.6311F, 0.0873F, 0.0F, 0.2618F)
      );
      PartDefinition tentacles_5_r2 = tentacles_5.addOrReplaceChild(
         "tentacles_5_r2",
         CubeListBuilder.create().texOffs(8, 53).addBox(-1.0F, -11.5F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -12.5F, 0.0F, 0.0873F, 0.0F, 0.2618F)
      );
      PartDefinition tentacles_6 = body.addOrReplaceChild("tentacles_6", CubeListBuilder.create(), PartPose.offset(-3.8F, 7.0F, 5.0F));
      PartDefinition tentacles_7_r1 = tentacles_6.addOrReplaceChild(
         "tentacles_7_r1",
         CubeListBuilder.create().texOffs(63, 4).addBox(-1.2412F, -2.4659F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-3.7529F, -26.0059F, -0.5F, 0.0F, 0.0F, -0.2618F)
      );
      PartDefinition tentacles_6_r2 = tentacles_6.addOrReplaceChild(
         "tentacles_6_r2",
         CubeListBuilder.create().texOffs(0, 53).addBox(-1.0F, -13.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -13.0F, 0.0F, 0.0F, 0.0F, -0.2618F)
      );
      PartDefinition tentacles_7 = body.addOrReplaceChild("tentacles_7", CubeListBuilder.create(), PartPose.offset(1.3F, 7.0F, 5.0F));
      PartDefinition tentacles_8_r1 = tentacles_7.addOrReplaceChild(
         "tentacles_8_r1",
         CubeListBuilder.create().texOffs(54, 62).addBox(-1.5F, -2.4537F, -1.8007F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -25.0255F, 3.5827F, -0.3054F, 0.0F, 0.0F)
      );
      PartDefinition tentacles_7_r2 = tentacles_7.addOrReplaceChild(
         "tentacles_7_r2",
         CubeListBuilder.create().texOffs(30, 51).addBox(-1.0F, -12.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -13.0F, 0.0F, -0.3054F, 0.0F, 0.0F)
      );
      PartDefinition tentacles_8 = body.addOrReplaceChild("tentacles_8", CubeListBuilder.create(), PartPose.offset(6.3F, 7.0F, 5.0F));
      PartDefinition tentacles_9_r1 = tentacles_8.addOrReplaceChild(
         "tentacles_9_r1",
         CubeListBuilder.create().texOffs(62, 35).addBox(-1.7164F, -2.4615F, -1.6695F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(3.0029F, -26.0453F, 1.9387F, -0.1745F, 0.0F, 0.2182F)
      );
      PartDefinition tentacles_8_r2 = tentacles_8.addOrReplaceChild(
         "tentacles_8_r2",
         CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -12.5F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -13.5F, 0.0F, -0.1745F, 0.0F, 0.2182F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }

   public void setupAnim(@Nonnull EyesoreEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      for (int i = 0; i < this.tentacles.size(); i++) {
         ModelPart tentacle = this.tentacles.get(i);
         tentacle.xRot = 0.1F * Mth.sin(ageInTicks * 0.3F + i);
      }

      this.body.yRot = netHeadYaw * (float) (Math.PI / 180.0);
      this.body.xRot = headPitch * (float) (Math.PI / 180.0);
   }

   public void renderToBuffer(
      @Nonnull PoseStack matrixStack, @Nonnull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha
   ) {
      this.body.render(matrixStack, buffer, packedLight, packedOverlay);
   }

   @Nonnull
   public ModelPart root() {
      return this.root;
   }
}
