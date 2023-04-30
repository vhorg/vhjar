package iskallia.vault.entity.model;

import iskallia.vault.entity.entity.VaultDoodEntity;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class VaultDoodModel extends IronGolemModel<VaultDoodEntity> {
   public VaultDoodModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(24, 0)
            .addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -7.0F, -2.0F)
      );
      PartDefinition hat = head.addOrReplaceChild(
         "hat",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(1, 20)
            .addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.45F))
            .texOffs(25, 21)
            .addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F))
            .texOffs(24, 0)
            .addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(45, 113)
            .addBox(-6.5F, -8.75F, -8.0F, 13.0F, 0.0F, 13.0F, new CubeDeformation(0.25F))
            .texOffs(3, 114)
            .addBox(-4.5F, -13.0F, -6.5F, 9.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(1, 118)
            .addBox(-1.0F, -11.0F, -7.75F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition cube_r1 = hat.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(90, 112).mirror().addBox(-4.8284F, 0.0F, -6.5F, 5.0F, 0.0F, 13.0F, new CubeDeformation(0.001F)).mirror(false),
         PartPose.offsetAndRotation(-6.8558F, -9.1363F, -1.749F, 0.0F, 0.0F, 0.9163F)
      );
      PartDefinition cube_r2 = hat.addOrReplaceChild(
         "cube_r2",
         CubeListBuilder.create().texOffs(90, 112).addBox(0.0F, 0.0F, -8.5F, 5.0F, 0.0F, 13.0F, new CubeDeformation(0.001F)),
         PartPose.offsetAndRotation(6.7514F, -9.0002F, 0.251F, 0.0F, 0.0F, -0.9163F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -7.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -7.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-4.0F, 11.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(60, 0).mirror().addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(5.0F, 11.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(0, 40)
            .addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, new CubeDeformation(0.0F))
            .texOffs(0, 70)
            .addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, new CubeDeformation(0.5F))
            .texOffs(0, 100)
            .addBox(-6.0F, 10.75F, -4.0F, 12.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, -7.0F, 0.0F)
      );
      PartDefinition cube_r3 = body.addOrReplaceChild(
         "cube_r3",
         CubeListBuilder.create().texOffs(105, 101).addBox(-1.0F, -5.0F, 1.0F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.001F)),
         PartPose.offsetAndRotation(1.5F, 4.75F, -7.0F, 0.0F, 0.0F, 0.1309F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
