package iskallia.vault.util;

import net.minecraft.client.model.geom.ModelPart;

public class ModelPartHelper {
   public static void runPreservingTransforms(Runnable procedure, ModelPart... modelParts) {
      float[] x = new float[modelParts.length];
      float[] y = new float[modelParts.length];
      float[] z = new float[modelParts.length];
      float[] xRots = new float[modelParts.length];
      float[] yRots = new float[modelParts.length];
      float[] zRots = new float[modelParts.length];

      for (int i = 0; i < modelParts.length; i++) {
         x[i] = modelParts[i].x;
         y[i] = modelParts[i].y;
         z[i] = modelParts[i].z;
         xRots[i] = modelParts[i].xRot;
         yRots[i] = modelParts[i].yRot;
         zRots[i] = modelParts[i].zRot;
      }

      procedure.run();

      for (int i = 0; i < modelParts.length; i++) {
         modelParts[i].x = x[i];
         modelParts[i].y = y[i];
         modelParts[i].z = z[i];
         modelParts[i].xRot = xRots[i];
         modelParts[i].yRot = yRots[i];
         modelParts[i].zRot = zRots[i];
      }
   }
}
