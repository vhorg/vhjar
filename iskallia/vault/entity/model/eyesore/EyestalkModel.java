package iskallia.vault.entity.model.eyesore;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.entity.entity.eyesore.EyestalkEntity;
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

public class EyestalkModel extends HierarchicalModel<EyestalkEntity> {
   private final ModelPart root;
   private final ModelPart body;
   private final ModelPart tail;

   public EyestalkModel(ModelPart root) {
      this.root = root;
      this.body = root.getChild("body");
      this.tail = root.getChild("tail");
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
         CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      PartDefinition cube_r1 = tail.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -4.5F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -8.5F, 2.0F, 0.2618F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public void setupAnim(@Nonnull EyestalkEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netbodyYaw, float bodyPitch) {
      this.tail.xRot = 0.5F + 0.1F * Mth.sin(ageInTicks * 0.3F);
      this.body.yRot = netbodyYaw * (float) (Math.PI / 180.0);
      this.body.xRot = bodyPitch * (float) (Math.PI / 180.0);
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
