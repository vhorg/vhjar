package iskallia.vault.entity.model;

import iskallia.vault.entity.entity.wraith.VaultWraithEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class VaultWraithModel extends HumanoidModel<VaultWraithEntity> {
   public VaultWraithModel(ModelPart root) {
      super(root, RenderType::entityTranslucent);
   }

   public static LayerDefinition small() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition body = root.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 23.0F, 10.0F), PartPose.ZERO
      );
      body.addOrReplaceChild(
         "tail",
         CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, 3.5F, 20.0F, 4.0F, 8.0F, 2.0F).texOffs(0, 33).addBox(-3.0F, 7.5F, 14.0F, 6.0F, 7.0F, 6.0F),
         PartPose.offsetAndRotation(0.0F, -7.5F, 1.0F, -0.7854F, 0.0F, 0.0F)
      );
      root.addOrReplaceChild(
         "right_arm", CubeListBuilder.create().texOffs(24, 33).addBox(-4.0F, -2.0F, -2.0F, 5.0F, 12.0F, 4.0F), PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      root.addOrReplaceChild(
         "left_arm", CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, -2.0F, -2.0F, 5.0F, 12.0F, 4.0F), PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
      root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
      root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
      return LayerDefinition.create(mesh, 64, 64);
   }

   public static LayerDefinition wide() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      PartDefinition body = root.addOrReplaceChild(
         "body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 23.0F, 12.0F), PartPose.ZERO
      );
      body.addOrReplaceChild(
         "tail",
         CubeListBuilder.create().texOffs(48, 18).addBox(-3.0F, 3.5F, 19.0F, 6.0F, 8.0F, 4.0F).texOffs(0, 35).addBox(-4.0F, 7.5F, 13.0F, 8.0F, 7.0F, 8.0F),
         PartPose.offsetAndRotation(0.0F, -7.5F, 1.0F, -0.7854F, 0.0F, 0.0F)
      );
      root.addOrReplaceChild(
         "right_arm", CubeListBuilder.create().texOffs(32, 35).addBox(-5.0F, -2.0F, -3.0F, 7.0F, 12.0F, 6.0F), PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      root.addOrReplaceChild(
         "left_arm", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, -2.0F, -3.0F, 7.0F, 12.0F, 6.0F), PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
      root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
      root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
      return LayerDefinition.create(mesh, 128, 128);
   }
}
