package iskallia.vault.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.vertex.VertexBuffer;

public class VBOUtil {
   public static VertexBuffer batch(EntityModel<?> model, RenderType renderType, int packedLight, int packedOverlay) {
      BufferBuilder buf = Tessellator.func_178181_a().func_178180_c();
      VertexBuffer vbo = new VertexBuffer(renderType.func_228663_p_());
      buf.func_181668_a(renderType.func_228664_q_(), renderType.func_228663_p_());
      model.func_225598_a_(new MatrixStack(), buf, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      buf.func_178977_d();
      vbo.func_227875_a_(buf);
      return vbo;
   }
}
