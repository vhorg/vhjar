package iskallia.vault.entity.model.skeleton_pirate;

import iskallia.vault.entity.entity.skeleton_pirate.Tier5SkeletonPirateEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier5SkeletonPirateModel extends SkeletonModel<Tier5SkeletonPirateEntity> {
   public Tier5SkeletonPirateModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(27, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(22, 26)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(37, 43).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition spear_r1 = body.addOrReplaceChild(
         "spear_r1",
         CubeListBuilder.create()
            .texOffs(54, 11)
            .addBox(0.0F, -2.0F, -12.0F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 26)
            .addBox(-0.5F, -1.5F, -6.0F, 1.0F, 1.0F, 19.0F, new CubeDeformation(0.0F))
            .texOffs(27, 17)
            .addBox(-1.0F, -2.0F, 13.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-1.5F, 2.5F, 2.0F, 0.4097F, -0.2815F, -0.1201F)
      );
      PartDefinition spear_r2 = body.addOrReplaceChild(
         "spear_r2",
         CubeListBuilder.create().texOffs(17, 17).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.01F)),
         PartPose.offsetAndRotation(0.4546F, 3.9484F, -4.11F, -0.3757F, -0.2815F, -0.1201F)
      );
      PartDefinition trident_r1 = body.addOrReplaceChild(
         "trident_r1",
         CubeListBuilder.create()
            .texOffs(52, 0)
            .addBox(0.5F, -0.5F, -13.5F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(52, 0)
            .addBox(4.5F, -0.5F, -13.5F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(30, 58)
            .addBox(2.5F, -0.5F, -14.5F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 19)
            .addBox(1.5F, -0.5F, -10.5F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(2.5F, -0.5F, -8.5F, 1.0F, 1.0F, 24.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.5F, 3.5F, 3.5F, 0.4215F, 0.3614F, 0.1572F)
      );
      PartDefinition wrap_r1 = body.addOrReplaceChild(
         "wrap_r1",
         CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -8.0F, -2.0F, 5.0F, 14.0F, 4.0F, new CubeDeformation(0.1F)),
         PartPose.offsetAndRotation(0.5459F, 7.0607F, 0.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 0.0F, 0.0F)
      );
      PartDefinition shoulder_r1 = left_arm.addOrReplaceChild(
         "shoulder_r1",
         CubeListBuilder.create().texOffs(47, 22).addBox(-2.0F, -2.5F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create()
            .texOffs(58, 31)
            .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(17, 47)
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 47)
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(0, 26).mirror().addBox(-3.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(-5.0F, 0.0F, 0.0F)
      );
      PartDefinition shoulder_r2 = right_arm.addOrReplaceChild(
         "shoulder_r2",
         CubeListBuilder.create().texOffs(47, 22).mirror().addBox(-3.0F, -2.5F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false),
         PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create()
            .texOffs(58, 31)
            .mirror()
            .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(17, 47)
            .mirror()
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(0, 47)
            .mirror()
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.25F))
            .mirror(false),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
