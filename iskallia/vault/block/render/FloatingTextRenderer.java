package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.block.entity.FloatingTextTileEntity;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent.Serializer;

public class FloatingTextRenderer extends TileEntityRenderer<FloatingTextTileEntity> {
   public FloatingTextRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      super(rendererDispatcherIn);
   }

   public void render(
      @Nonnull FloatingTextTileEntity tileEntity,
      float partialTicks,
      @Nonnull MatrixStack matrixStack,
      @Nonnull IRenderTypeBuffer buffer,
      int combinedLightIn,
      int combinedOverlayIn
   ) {
      List<String> lines = tileEntity.getLines();
      int length = lines.size();
      Minecraft minecraft = Minecraft.func_71410_x();
      FontRenderer fontRenderer = minecraft.field_71466_p;

      for (int i = length - 1; i >= 0; i--) {
         String line = lines.get(i);
         IFormattableTextComponent text = this.parseTextComponent(line);
         if (text != null) {
            float scale = 0.02F;
            int color = -1;
            int opacity = 1711276032;
            matrixStack.func_227860_a_();
            Matrix4f matrix4f = matrixStack.func_227866_c_().func_227870_a_();
            float offset = -fontRenderer.func_238414_a_(text) / 2;
            matrixStack.func_227861_a_(0.5, 1.7F + 0.25F * (length - i), 0.5);
            matrixStack.func_227862_a_(scale, scale, scale);
            matrixStack.func_227863_a_(minecraft.func_175598_ae().func_229098_b_());
            matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
            fontRenderer.func_243247_a(text, offset, 0.0F, color, false, matrix4f, buffer, false, opacity, combinedLightIn);
            fontRenderer.func_243247_a(text, offset, 0.0F, -1, false, matrix4f, buffer, false, 0, combinedLightIn);
            matrixStack.func_227865_b_();
         }
      }
   }

   public IFormattableTextComponent parseTextComponent(String line) {
      try {
         return Serializer.func_240644_b_(line);
      } catch (Exception var3) {
         return new StringTextComponent("#!Parse Error!#").func_240699_a_(TextFormatting.RED);
      }
   }
}
