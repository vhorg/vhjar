package iskallia.vault.entity.model.elite;

import javax.annotation.Nonnull;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.monster.WitherSkeleton;

public class EliteWitherSkeletonModel extends SkeletonModel<WitherSkeleton> {
   protected ModelPart head;
   protected ModelPart body;
   protected ModelPart head1;
   protected ModelPart head2;
   protected ModelPart right_arm;
   protected ModelPart left_arm;

   public EliteWitherSkeletonModel(ModelPart root) {
      super(root);
      this.head = root.getChild("head");
      this.body = root.getChild("body");
      this.head1 = this.body.getChild("head1");
      this.head2 = this.body.getChild("head2");
      this.right_arm = root.getChild("right_arm");
      this.left_arm = root.getChild("left_arm");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition head1 = body.addOrReplaceChild(
         "head1",
         CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition head2 = body.addOrReplaceChild(
         "head2",
         CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition right_arm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition left_arm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(1.0F, 7.0F, 1.0F));
      PartDefinition right_leg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      PartDefinition left_leg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
         PartPose.offset(2.0F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void setupAnim(@Nonnull WitherSkeleton entity, float pLimbSwing, float pLimbSwingAmount, float ageInTicks, float pNetHeadYaw, float pHeadPitch) {
      super.setupAnim(entity, pLimbSwing, pLimbSwingAmount, ageInTicks, pNetHeadYaw, pHeadPitch);
      this.head1.xRot = pHeadPitch * (float) (Math.PI / 180.0);
      this.head1.yRot = pNetHeadYaw * (float) (Math.PI / 180.0) + 0.1F;
      this.head2.xRot = pHeadPitch * (float) (Math.PI / 180.0);
      this.head2.yRot = pNetHeadYaw * (float) (Math.PI / 180.0) - 0.1F;
   }
}
