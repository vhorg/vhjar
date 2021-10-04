package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.block.StatueCauldronBlock;
import iskallia.vault.block.entity.StatueCauldronTileEntity;
import java.awt.Color;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;

public class StatueCauldronRenderer extends TileEntityRenderer<StatueCauldronTileEntity> {
   public StatueCauldronRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      super(rendererDispatcherIn);
   }

   public void render(
      StatueCauldronTileEntity tileEntity,
      float partialTicks,
      MatrixStack matrixStackIn,
      IRenderTypeBuffer bufferIn,
      int combinedLightIn,
      int combinedOverlayIn
   ) {
      BlockState state = tileEntity.func_195044_w();
      int level = (Integer)state.func_177229_b(StatueCauldronBlock.field_176591_a);
      float percentage = (float)tileEntity.getStatueCount() / tileEntity.getRequiredAmount();
      int height = 14;
      if (level < 3) {
         if (level == 1) {
            this.renderLiquid(matrixStackIn, bufferIn, 0.0F, percentage, 1.0F - percentage, height - 5);
         } else if (level == 2) {
            this.renderLiquid(matrixStackIn, bufferIn, 0.0F, percentage, 1.0F - percentage, height - 2);
         }
      } else {
         this.renderLiquid(matrixStackIn, bufferIn, 0.0F, percentage, 1.0F - percentage, height);
      }
   }

   private void renderLiquid(MatrixStack matrixStack, IRenderTypeBuffer buffer, float r, float g, float b, int height) {
      IVertexBuilder builder = buffer.getBuffer(RenderType.func_228645_f_());
      TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.func_71410_x()
         .func_228015_a_(PlayerContainer.field_226615_c_)
         .apply(Fluids.field_204546_a.getAttributes().getStillTexture());
      matrixStack.func_227860_a_();
      this.addVertex(builder, matrixStack, this.p2f(1), this.p2f(height), this.p2f(1), sprite.func_94209_e(), sprite.func_94206_g(), r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(1), this.p2f(height), this.p2f(15), sprite.func_94212_f(), sprite.func_94206_g(), r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(15), this.p2f(height), this.p2f(15), sprite.func_94212_f(), sprite.func_94210_h(), r, g, b, 1.0F);
      this.addVertex(builder, matrixStack, this.p2f(15), this.p2f(height), this.p2f(1), sprite.func_94209_e(), sprite.func_94210_h(), r, g, b, 1.0F);
      matrixStack.func_227865_b_();
   }

   private void addVertex(IVertexBuilder renderer, MatrixStack stack, float x, float y, float z, float u, float v, float r, float g, float b, float a) {
      renderer.func_227888_a_(stack.func_227866_c_().func_227870_a_(), x, y, z)
         .func_227885_a_(r, g, b, 0.5F)
         .func_225583_a_(u, v)
         .func_225587_b_(0, 240)
         .func_225584_a_(1.0F, 0.0F, 0.0F)
         .func_181675_d();
   }

   private float p2f(int pixel) {
      return 0.0625F * pixel;
   }

   public static Color getBlendedColor(float percentage) {
      float green = ensureRange(percentage);
      float blue = ensureRange(1.0F - percentage);
      return new Color(0.0F, green, blue);
   }

   private static float ensureRange(float value) {
      return Math.min(Math.max(value, 0.0F), 1.0F);
   }
}
