package iskallia.vault.block.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public class ScavengerChestModel extends Model {
   public ScavengerChestModel() {
      super(RenderType::entityCutout);
   }

   private void setRotationAngle(ModelPart model, float x, float y, float z) {
      model.xRot = x;
      model.yRot = y;
      model.zRot = z;
   }

   public void renderToBuffer(PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
   }

   public void setLidAngle(float lidAngle) {
   }
}
