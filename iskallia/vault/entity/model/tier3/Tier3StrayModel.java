package iskallia.vault.entity.model.tier3;

import iskallia.vault.entity.entity.tier3.Tier3StrayEntity;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class Tier3StrayModel extends SkeletonModel<Tier3StrayEntity> {
   public Tier3StrayModel(ModelPart root) {
      super(root);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(22, 42)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(0, 36)
            .addBox(-3.5F, -2.0F, 2.0F, 7.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(26, 0)
            .addBox(-3.5F, 6.0F, -1.0F, 7.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(36, 24)
            .addBox(-4.0F, 11.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(36, 52)
            .addBox(-1.0F, 6.0F, 0.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .texOffs(48, 4)
            .addBox(0.0F, -11.0F, -3.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(48, 11)
            .addBox(0.0F, -10.0F, 2.0F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(54, 43)
            .addBox(0.5F, -14.0F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(54, 38)
            .addBox(-3.5F, -11.0F, 1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(54, 17)
            .addBox(0.5F, -13.0F, 3.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create()
            .texOffs(0, 48)
            .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 23)
            .addBox(-4.0F, -4.0F, -4.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-4.0F, -4.0F, 3.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(30, 12)
            .addBox(-5.0F, -5.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(24, 52)
            .addBox(-7.0F, -4.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(22, 36)
            .addBox(-2.0F, -8.0F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(26, 0)
            .addBox(-4.0F, -7.0F, 0.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create()
            .texOffs(46, 42)
            .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(30, 30)
            .addBox(-1.0F, -5.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
            .texOffs(0, 18)
            .addBox(0.0F, -4.0F, -4.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 5)
            .addBox(0.0F, -4.0F, 3.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(54, 30)
            .addBox(5.0F, -4.0F, -2.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(54, 48)
            .addBox(0.0F, -8.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(26, 22)
            .addBox(3.0F, -7.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(1.0F, 7.0F, 1.0F));
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(16, 50).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(8, 48).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 128, 128);
   }
}
