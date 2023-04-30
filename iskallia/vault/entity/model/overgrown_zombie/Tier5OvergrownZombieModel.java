package iskallia.vault.entity.model.overgrown_zombie;

import iskallia.vault.entity.entity.overgrown_zombie.Tier5OvergrownZombieEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier5OvergrownZombieModel extends ZombieModel<Tier5OvergrownZombieEntity> {
   public Tier5OvergrownZombieModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(33, 26).addBox(-5.0F, -3.0F, -5.5F, 10.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition flatlayer_r1 = head.addOrReplaceChild(
         "flatlayer_r1",
         CubeListBuilder.create()
            .texOffs(0, 26)
            .addBox(-8.0F, -9.0F, -1.5F, 16.0F, 20.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(0, 26)
            .addBox(-8.0F, -9.0F, 4.5F, 16.0F, 20.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(0, 26)
            .addBox(-8.0F, -9.0F, 1.5F, 16.0F, 20.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(0, 26)
            .addBox(-8.0F, -9.0F, -4.5F, 16.0F, 20.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-5.0F, -6.0F, -5.5F, 10.0F, 14.0F, 11.0F, new CubeDeformation(0.25F)),
         PartPose.offsetAndRotation(0.0F, -4.0F, -0.5F, -0.3054F, 0.0F, 0.0F)
      );
      PartDefinition lamp_r1 = head.addOrReplaceChild(
         "lamp_r1",
         CubeListBuilder.create()
            .texOffs(32, 0)
            .addBox(-1.5F, -10.7249F, 12.5607F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
            .texOffs(43, 9)
            .addBox(-1.0F, -10.2249F, 17.5607F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(44, 0)
            .addBox(-1.0F, -10.2249F, 11.5607F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(43, 0)
            .addBox(-0.5F, -9.2249F, -2.4393F, 1.0F, 0.0F, 14.0F, new CubeDeformation(0.0F))
            .texOffs(44, 44)
            .addBox(0.0F, -9.7249F, -2.4393F, 0.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -15.8053F, 3.5982F, -1.5708F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = head.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create()
            .texOffs(0, 47)
            .addBox(-0.5F, -9.9749F, 0.5607F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-1.0F, -4.9749F, 0.0607F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -15.8053F, 3.5982F, -1.1781F, 0.0F, 0.0F)
      );
      PartDefinition cube_r2 = head.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(33, 26).addBox(-1.0F, -2.75F, -0.5F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.01F)),
         PartPose.offsetAndRotation(0.0F, -13.4466F, 2.0323F, -0.7854F, 0.0F, 0.0F)
      );
      PartDefinition cube_r3 = head.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(43, 15).addBox(-1.5F, -1.75F, -0.75F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -10.5F, 0.0F, -0.3927F, 0.0F, 0.0F)
      );
      PartDefinition head_r1 = head.addOrReplaceChild(
         "head_r1",
         CubeListBuilder.create().texOffs(25, 41).addBox(-4.0F, -8.0F, -9.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, -0.3054F, 0.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(68, 77)
            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(51, 60)
            .addBox(0.0F, -3.0F, -4.0F, 0.0F, 18.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(34, 58)
            .addBox(2.0F, -3.0F, -4.0F, 0.0F, 18.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(0, 74)
            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(17, 58)
            .addBox(0.0F, -3.0F, -4.0F, 0.0F, 18.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 47)
            .addBox(-2.0F, -3.0F, -4.0F, 0.0F, 18.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(63, 15)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(59, 41)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(84, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(68, 60).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
