package iskallia.vault.entity.model.deep_dark;

import iskallia.vault.entity.entity.deepdark.DeepDarkSilverfishEntity;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DeepDarkSilverfishModel extends SilverfishModel<DeepDarkSilverfishEntity> {
   private static final int[][] BODY_SIZES = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};

   public DeepDarkSilverfishModel(ModelPart root) {
      super(root);
   }

   private static String getLayerName(int pIndex) {
      return "layer" + pIndex;
   }

   private static String getSegmentName(int pIndex) {
      return "segment" + pIndex;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      float[] afloat = new float[7];
      float f = -3.5F;

      for (int i = 0; i < 7; i++) {
         afloat[i] = f;
         if (i < 6) {
            f += (BODY_SIZES[i][2] + BODY_SIZES[i + 1][2]) * 0.5F;
         }
      }

      PartDefinition segment0 = partdefinition.addOrReplaceChild(
         "segment0",
         CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -5.0F, 0.0F, 0.0F, 5.0F, 15.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      PartDefinition segment1 = partdefinition.addOrReplaceChild(
         "segment1",
         CubeListBuilder.create().texOffs(31, 9).addBox(-1.5F, -2.0F, 4.0F, 3.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      PartDefinition segment2 = partdefinition.addOrReplaceChild(
         "segment2",
         CubeListBuilder.create().texOffs(21, 21).addBox(-2.5F, -3.0F, 0.0F, 5.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      PartDefinition segment3 = partdefinition.addOrReplaceChild(
         "segment3",
         CubeListBuilder.create().texOffs(16, 0).addBox(-3.0F, -4.0F, -4.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      PartDefinition segment4 = partdefinition.addOrReplaceChild(
         "segment4",
         CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -7.0F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      PartDefinition cube_r1 = segment4.addOrReplaceChild(
         "cube_r1",
         CubeListBuilder.create().texOffs(0, 21).addBox(-4.0F, -1.0F, -3.5F, 5.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -6.0F, -5.0F, 0.0F, 0.0F, -0.7854F)
      );
      PartDefinition segment5 = partdefinition.addOrReplaceChild("segment5", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      PartDefinition segment6 = partdefinition.addOrReplaceChild("segment6", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      partdefinition.addOrReplaceChild(getLayerName(0), CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      partdefinition.addOrReplaceChild(getLayerName(1), CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      partdefinition.addOrReplaceChild(getLayerName(2), CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 64, 64);
   }
}
