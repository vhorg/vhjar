package iskallia.vault.entity.model.elite;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.monster.Skeleton;

public class EliteSkeletonModel extends SkeletonModel<Skeleton> {
   public EliteSkeletonModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = body.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(32, 8)
            .addBox(-2.75F, -2.5F, -1.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(0.25F, 1.5F, -4.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(0.25F, -0.5F, -4.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(0.25F, -2.5F, -4.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(32, 8)
            .addBox(-2.75F, 1.5F, -1.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(32, 8)
            .addBox(-2.75F, -0.5F, -1.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(3.75F, 2.5F, 0.5F, 0.0F, 0.7854F, 0.0F)
      );
      PartDefinition cube_r2 = body.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-1.25F, 1.5F, -4.25F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-1.25F, -0.5F, -4.25F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-1.25F, -2.5F, -4.25F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(32, 8)
            .addBox(-1.25F, -2.5F, -1.25F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(32, 8)
            .addBox(-1.25F, 1.5F, -1.25F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(32, 8)
            .addBox(-1.25F, -0.5F, -1.25F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-3.75F, 2.5F, 0.5F, 0.0F, -0.7854F, 0.0F)
      );
      PartDefinition hat = partdefinition.addOrReplaceChild(
         "hat",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F))
            .texOffs(0, 32)
            .addBox(-4.5F, 0.5F, -4.5F, 9.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(0, 37).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition cube_r3 = right_arm.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-2.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.3927F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(0, 37).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition cube_r4 = left_arm.addOrReplaceChild(
         "cube_r4",
         CubeListBuilder.create().texOffs(24, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(2.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.3927F)
      );
      PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(1.0F, 7.0F, 1.0F));
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(18, 32).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(18, 32).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
