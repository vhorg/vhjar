package iskallia.vault.entity.model.winterwalker;

import iskallia.vault.entity.entity.winterwalker.Tier5WinterwalkerEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier5WinterwalkerModel extends SkeletonModel<Tier5WinterwalkerEntity> {
   public Tier5WinterwalkerModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(33, 17)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F))
            .texOffs(0, 25)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-5.0F, -9.0F, -5.0F, 10.0F, 14.0F, 10.0F, new CubeDeformation(0.0F))
            .texOffs(66, 20)
            .addBox(-2.0F, -6.0F, -9.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 4)
            .addBox(1.0F, -12.0F, 2.5F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(58, 20)
            .addBox(1.0F, -12.0F, 0.5F, 3.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-4.0F, -12.0F, 2.5F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(31, 0)
            .addBox(-4.0F, -12.0F, 0.5F, 3.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(36, 51)
            .addBox(-1.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(58, 34)
            .addBox(-1.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.3F)),
         PartPose.offset(5.0F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(41, 0)
            .addBox(-3.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(58, 8)
            .addBox(-3.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.3F)),
         PartPose.offset(-5.0F, 0.0F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(33, 34)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(14, 42)
            .addBox(-4.5F, 0.0F, -2.25F, 9.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
            .texOffs(58, 0)
            .addBox(-4.0F, 9.0F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.26F))
            .texOffs(0, 60)
            .addBox(-4.5F, 0.0F, -2.1F, 9.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create()
            .texOffs(53, 51)
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(15, 64)
            .addBox(-2.0F, 4.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.2F))
            .texOffs(0, 42)
            .addBox(-2.0F, 0.0F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.25F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create()
            .texOffs(53, 51)
            .mirror()
            .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .mirror(false)
            .texOffs(15, 64)
            .mirror()
            .addBox(-2.0F, 4.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.2F))
            .mirror(false)
            .texOffs(0, 42)
            .mirror()
            .addBox(-2.0F, 0.0F, -2.5F, 4.0F, 12.0F, 5.0F, new CubeDeformation(0.25F))
            .mirror(false),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
