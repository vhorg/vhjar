package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.block.base.FillableAltarTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

public class FillableAltarRenderer extends TileEntityRenderer<FillableAltarTileEntity> {
   private static final Vector3f FLUID_LOWER_POS = new Vector3f(2.25F, 2.0F, 2.25F);
   private static final Vector3f FLUID_UPPER_POS = new Vector3f(13.75F, 11.0F, 13.75F);

   public FillableAltarRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
      super(rendererDispatcherIn);
   }

   public void render(
      FillableAltarTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlayIn
   ) {
      if (tileEntity.initialized()) {
         IVertexBuilder builder = buffer.getBuffer(RenderType.func_228645_f_());
         float progressPercentage = tileEntity.progressPercentage();
         if (progressPercentage > 0.0F) {
            float fluidMaxHeight = FLUID_UPPER_POS.func_195900_b() - FLUID_LOWER_POS.func_195900_b();
            Vector3f upperPos = new Vector3f(
               FLUID_UPPER_POS.func_195899_a(), FLUID_LOWER_POS.func_195900_b() + fluidMaxHeight * progressPercentage, FLUID_UPPER_POS.func_195902_c()
            );
            this.renderCuboid(builder, matrixStack, FLUID_LOWER_POS, upperPos, tileEntity.getFillColor());
            if (buffer instanceof Impl) {
               ((Impl)buffer).func_228462_a_(RenderType.func_228645_f_());
            }
         }

         Minecraft minecraft = Minecraft.func_71410_x();
         if (minecraft.field_71476_x != null && minecraft.field_71476_x.func_216346_c() == Type.BLOCK) {
            BlockRayTraceResult result = (BlockRayTraceResult)minecraft.field_71476_x;
            if (tileEntity.func_174877_v().equals(result.func_216350_a())) {
               var progressText = (StringTextComponent & StringTextComponent)(
                  tileEntity.isMaxedOut()
                     ? new StringTextComponent("Right Click to Loot!").func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(-1313364)))
                     : new StringTextComponent(tileEntity.getCurrentProgress() + " / " + tileEntity.getMaxProgress() + " ")
                        .func_230529_a_(tileEntity.getRequirementUnit())
               );
               this.renderLabel(matrixStack, 0.5F, 2.3F, 0.5F, buffer, combinedLight, tileEntity.getRequirementName());
               this.renderLabel(matrixStack, 0.5F, 2.1F, 0.5F, buffer, combinedLight, progressText);
            }
         }
      }
   }

   public void renderLabel(MatrixStack matrixStack, float x, float y, float z, IRenderTypeBuffer buffer, int lightLevel, ITextComponent text) {
      Minecraft minecraft = Minecraft.func_71410_x();
      FontRenderer fontRenderer = minecraft.field_71466_p;
      matrixStack.func_227860_a_();
      float scale = 0.02F;
      int opacity = 1711276032;
      float offset = -fontRenderer.func_238414_a_(text) / 2;
      Matrix4f matrix4f = matrixStack.func_227866_c_().func_227870_a_();
      matrixStack.func_227861_a_(x, y, z);
      matrixStack.func_227862_a_(scale, scale, scale);
      matrixStack.func_227863_a_(minecraft.func_175598_ae().func_229098_b_());
      matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(180.0F));
      fontRenderer.func_243247_a(text, offset, 0.0F, -1, false, matrix4f, buffer, true, opacity, lightLevel);
      fontRenderer.func_243247_a(text, offset, 0.0F, -1, false, matrix4f, buffer, false, 0, lightLevel);
      matrixStack.func_227865_b_();
   }

   public void renderCuboid(IVertexBuilder builder, MatrixStack matrixStack, Vector3f v1, Vector3f v2, java.awt.Color tint) {
      TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.func_71410_x()
         .func_228015_a_(PlayerContainer.field_226615_c_)
         .apply(Fluids.field_204546_a.getAttributes().getStillTexture());
      float minU = sprite.func_94214_a(0.0);
      float maxU = sprite.func_94214_a(16.0);
      float minV = sprite.func_94207_b(0.0);
      float maxV = sprite.func_94207_b(16.0);
      matrixStack.func_227860_a_();
      this.addVertex(builder, matrixStack, v1.func_195899_a(), v2.func_195900_b(), v1.func_195902_c(), tint, minU, maxV);
      this.addVertex(builder, matrixStack, v1.func_195899_a(), v2.func_195900_b(), v2.func_195902_c(), tint, minU, minV);
      this.addVertex(builder, matrixStack, v2.func_195899_a(), v2.func_195900_b(), v2.func_195902_c(), tint, maxU, minV);
      this.addVertex(builder, matrixStack, v2.func_195899_a(), v2.func_195900_b(), v1.func_195902_c(), tint, maxU, maxV);
      this.addVertex(builder, matrixStack, v1.func_195899_a(), v1.func_195900_b(), v1.func_195902_c(), tint, minU, maxV);
      this.addVertex(builder, matrixStack, v1.func_195899_a(), v2.func_195900_b(), v1.func_195902_c(), tint, minU, minV);
      this.addVertex(builder, matrixStack, v2.func_195899_a(), v2.func_195900_b(), v1.func_195902_c(), tint, maxU, minV);
      this.addVertex(builder, matrixStack, v2.func_195899_a(), v1.func_195900_b(), v1.func_195902_c(), tint, maxU, maxV);
      this.addVertex(builder, matrixStack, v2.func_195899_a(), v1.func_195900_b(), v1.func_195902_c(), tint, minU, maxV);
      this.addVertex(builder, matrixStack, v2.func_195899_a(), v2.func_195900_b(), v1.func_195902_c(), tint, minU, minV);
      this.addVertex(builder, matrixStack, v2.func_195899_a(), v2.func_195900_b(), v2.func_195902_c(), tint, maxU, minV);
      this.addVertex(builder, matrixStack, v2.func_195899_a(), v1.func_195900_b(), v2.func_195902_c(), tint, maxU, maxV);
      this.addVertex(builder, matrixStack, v1.func_195899_a(), v1.func_195900_b(), v2.func_195902_c(), tint, minU, maxV);
      this.addVertex(builder, matrixStack, v1.func_195899_a(), v2.func_195900_b(), v2.func_195902_c(), tint, minU, minV);
      this.addVertex(builder, matrixStack, v1.func_195899_a(), v2.func_195900_b(), v1.func_195902_c(), tint, maxU, minV);
      this.addVertex(builder, matrixStack, v1.func_195899_a(), v1.func_195900_b(), v1.func_195902_c(), tint, maxU, maxV);
      this.addVertex(builder, matrixStack, v2.func_195899_a(), v1.func_195900_b(), v2.func_195902_c(), tint, minU, maxV);
      this.addVertex(builder, matrixStack, v2.func_195899_a(), v2.func_195900_b(), v2.func_195902_c(), tint, minU, minV);
      this.addVertex(builder, matrixStack, v1.func_195899_a(), v2.func_195900_b(), v2.func_195902_c(), tint, maxU, minV);
      this.addVertex(builder, matrixStack, v1.func_195899_a(), v1.func_195900_b(), v2.func_195902_c(), tint, maxU, maxV);
      matrixStack.func_227865_b_();
   }

   public void addVertex(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, java.awt.Color tint, float u, float v) {
      builder.func_227888_a_(matrixStack.func_227866_c_().func_227870_a_(), x / 16.0F, y / 16.0F, z / 16.0F)
         .func_227885_a_(tint.getRed() / 255.0F, tint.getGreen() / 255.0F, tint.getBlue() / 255.0F, 0.8F)
         .func_225583_a_(u, v)
         .func_225587_b_(0, 240)
         .func_225584_a_(1.0F, 0.0F, 0.0F)
         .func_181675_d();
   }
}
