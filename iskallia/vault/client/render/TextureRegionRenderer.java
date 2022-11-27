package iskallia.vault.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class TextureRegionRenderer {
   private static final TextureRegionRenderer INSTANCE = new TextureRegionRenderer();
   private static final PoseStack IDENTITY_MATRIX_STACK = new PoseStack();
   private PoseStack poseStack = IDENTITY_MATRIX_STACK;
   private BufferBuilder bufferBuilder;
   private ResourceLocation textureAtlas;
   private int atlasWidth;
   private int atlasHeight;

   public static TextureRegionRenderer getInstance() {
      return INSTANCE;
   }

   private TextureRegionRenderer() {
   }

   public TextureRegionRenderer begin(ResourceLocation textureAtlas) {
      return this.begin(textureAtlas, 256, 256);
   }

   public TextureRegionRenderer begin(ResourceLocation textureAtlas, int atlasWidth, int atlasHeight) {
      if (this.bufferBuilder != null) {
         throw new IllegalStateException("Already building!");
      } else {
         this.textureAtlas = textureAtlas;
         this.atlasWidth = atlasWidth;
         this.atlasHeight = atlasHeight;
         this.bufferBuilder = Tesselator.getInstance().getBuilder();
         this.bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
         return this;
      }
   }

   public TextureRegionRenderer with(PoseStack poseStack) {
      this.poseStack = poseStack;
      return this;
   }

   public TextureRegionRenderer identity() {
      return this.with(IDENTITY_MATRIX_STACK);
   }

   public TextureRegionRenderer push() {
      this.poseStack.pushPose();
      return this;
   }

   public TextureRegionRenderer pop() {
      this.poseStack.popPose();
      return this;
   }

   public TextureRegionRenderer translateX(double x) {
      return this.translate(x, 0.0, 0.0);
   }

   public TextureRegionRenderer translateY(double y) {
      return this.translate(0.0, y, 0.0);
   }

   public TextureRegionRenderer translateZ(double z) {
      return this.translate(0.0, 0.0, z);
   }

   public TextureRegionRenderer translateXY(double x, double y) {
      return this.translate(x, y, 0.0);
   }

   public TextureRegionRenderer translate(double x, double y, double z) {
      this.poseStack.translate(x, y, z);
      return this;
   }

   public TextureRegionRenderer scale(double scale) {
      return this.scale(scale, scale, scale);
   }

   public TextureRegionRenderer scaleX(double scale) {
      return this.scale(scale, 1.0, 1.0);
   }

   public TextureRegionRenderer scaleY(double scale) {
      return this.scale(1.0, scale, 1.0);
   }

   public TextureRegionRenderer scale(double x, double y, double z) {
      this.poseStack.scale((float)x, (float)y, (float)z);
      return this;
   }

   public TextureRegionRenderer draw(TextureRegion textureRegion) {
      return this.draw(0.0, 0.0, textureRegion);
   }

   public TextureRegionRenderer draw(double x, double y, TextureRegion textureRegion) {
      return this.draw(
         this.poseStack.last().pose(),
         x,
         x + textureRegion.width(),
         y,
         y + textureRegion.height(),
         0.0,
         (float)textureRegion.x() / this.atlasWidth,
         (float)(textureRegion.x() + textureRegion.width()) / this.atlasWidth,
         (float)textureRegion.y() / this.atlasHeight,
         (float)(textureRegion.y() + textureRegion.height()) / this.atlasHeight
      );
   }

   private TextureRegionRenderer draw(
      Matrix4f pMatrix, double pX1, double pX2, double pY1, double pY2, double pBlitOffset, float pMinU, float pMaxU, float pMinV, float pMaxV
   ) {
      this.bufferBuilder.vertex(pMatrix, (float)pX1, (float)pY2, (float)pBlitOffset).uv(pMinU, pMaxV).endVertex();
      this.bufferBuilder.vertex(pMatrix, (float)pX2, (float)pY2, (float)pBlitOffset).uv(pMaxU, pMaxV).endVertex();
      this.bufferBuilder.vertex(pMatrix, (float)pX2, (float)pY1, (float)pBlitOffset).uv(pMaxU, pMinV).endVertex();
      this.bufferBuilder.vertex(pMatrix, (float)pX1, (float)pY1, (float)pBlitOffset).uv(pMinU, pMinV).endVertex();
      return this;
   }

   public void end() {
      if (this.bufferBuilder == null) {
         throw new IllegalStateException("Not building!");
      } else {
         RenderSystem.setShaderTexture(0, this.textureAtlas);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         this.bufferBuilder.end();
         BufferUploader.end(this.bufferBuilder);
         this.identity();
         this.bufferBuilder = null;
         this.textureAtlas = null;
         this.atlasWidth = 0;
         this.atlasHeight = 0;
      }
   }

   static {
      IDENTITY_MATRIX_STACK.setIdentity();
   }
}
