package iskallia.vault.client.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;

public class VBOUtil {
   public static VertexBuffer batch(EntityModel<?> model, RenderType renderType, int packedLight, int packedOverlay) {
      BufferBuilder buf = Tesselator.getInstance().getBuilder();
      VertexBuffer vbo = new VertexBuffer();
      buf.begin(renderType.mode(), renderType.format());
      model.renderToBuffer(new PoseStack(), buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      buf.end();
      vbo.upload(buf);
      return vbo;
   }
}
