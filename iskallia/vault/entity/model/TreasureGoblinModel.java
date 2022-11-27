package iskallia.vault.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.entity.entity.TreasureGoblinEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class TreasureGoblinModel extends PlayerModel<TreasureGoblinEntity> {
   public TreasureGoblinModel(ModelPart pRoot) {
      super(pRoot, false);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = PlayerModel.createMesh(CubeDeformation.NONE, false);
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      PartDefinition head = partdefinition.addOrReplaceChild(
         "head",
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition head2 = head.addOrReplaceChild(
         "head2",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(1.0F))
            .texOffs(0, 26)
            .addBox(-1.0F, -2.0F, -7.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 21)
            .addBox(-4.0F, -4.0F, -6.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition ear6_r1 = head2.addOrReplaceChild(
         "ear6_r1",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-0.125F, -2.125F, 0.875F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-0.125F, -1.125F, -1.125F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-0.125F, -0.125F, -2.125F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-0.125F, 0.875F, -3.125F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(6.375F, -4.875F, 2.125F, 0.0F, 0.3927F, 0.0F)
      );
      PartDefinition ear5_r1 = head2.addOrReplaceChild(
         "ear5_r1",
         CubeListBuilder.create()
            .texOffs(0, 0)
            .addBox(-0.125F, -2.125F, 0.875F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-0.125F, -1.125F, -1.125F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0)
            .addBox(-0.125F, -0.125F, -2.125F, 0.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 1)
            .addBox(-0.125F, 0.875F, -3.125F, 0.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(-6.625F, -4.875F, 2.125F, 0.0F, -0.7854F, 0.0F)
      );
      PartDefinition body = partdefinition.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .texOffs(16, 16)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(16, 32)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition rightArm = partdefinition.addOrReplaceChild(
         "right_arm",
         CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      PartDefinition leftArm = partdefinition.addOrReplaceChild(
         "left_arm",
         CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      PartDefinition rightLeg = partdefinition.addOrReplaceChild(
         "right_leg",
         CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(-1.9F, 12.0F, 0.0F)
      );
      PartDefinition leftLeg = partdefinition.addOrReplaceChild(
         "left_leg",
         CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(1.9F, 12.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
      matrixStack.pushPose();
      this.head.render(matrixStack, buffer, packedLight, packedOverlay);
      this.body.render(matrixStack, buffer, packedLight, packedOverlay);
      this.rightArm.render(matrixStack, buffer, packedLight, packedOverlay);
      this.leftArm.render(matrixStack, buffer, packedLight, packedOverlay);
      this.rightLeg.render(matrixStack, buffer, packedLight, packedOverlay);
      this.leftLeg.render(matrixStack, buffer, packedLight, packedOverlay);
      matrixStack.popPose();
   }
}
