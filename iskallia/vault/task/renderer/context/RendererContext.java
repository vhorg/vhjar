package iskallia.vault.task.renderer.context;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;

public class RendererContext {
   private PoseStack matrices;
   private float tickDelta;
   private BufferSource bufferSource;
   private Font font;

   public RendererContext(PoseStack matrices, float tickDelta, BufferSource bufferSource, Font font) {
      this.matrices = matrices;
      this.tickDelta = tickDelta;
      this.bufferSource = bufferSource;
      this.font = font;
   }

   public PoseStack getMatrices() {
      return this.matrices;
   }

   public float getTickDelta() {
      return this.tickDelta;
   }

   public void push() {
      this.matrices.pushPose();
   }

   public void pop() {
      this.matrices.popPose();
   }

   public void translate(double x, double y, double z) {
      this.matrices.translate(x, y, z);
   }

   public void translate(Vec3 vector) {
      this.translate(vector.x, vector.y, vector.z);
   }

   public void translate(Vector3d vector) {
      this.translate(vector.x, vector.y, vector.z);
   }

   public void scale(double x, double y, double z) {
      this.matrices.scale((float)x, (float)y, (float)z);
   }

   public void scale(Vec3 vector) {
      this.scale(vector.x, vector.y, vector.z);
   }

   public void scale(Vector3d vector) {
      this.scale(vector.x, vector.y, vector.z);
   }

   public int setShaderTexture(ResourceLocation texture) {
      int previous = RenderSystem.getShaderTexture(0);
      RenderSystem.setShaderTexture(0, texture);
      return previous;
   }

   public int setShaderTexture(int texture) {
      int previous = RenderSystem.getShaderTexture(0);
      RenderSystem.setShaderTexture(0, texture);
      return previous;
   }

   public void setShaderColor(float alpha, float red, float green, float blue) {
      RenderSystem.setShaderColor(red, green, blue, alpha);
   }

   public void setShaderColor(int alpha, int red, int green, int blue) {
      this.setShaderColor(alpha / 255.0F, red / 255.0F, green / 255.0F, blue / 255.0F);
   }

   public void setShaderColor(int color) {
      this.setShaderColor(color >> 24 & 0xFF, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF);
   }

   public void setShaderColor(ChatFormatting formatting) {
      this.setShaderColor(formatting.getColor() == null ? 16777215 : formatting.getColor());
   }

   public void blit(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
      GuiComponent.blit(this.matrices, x, y, u, v, width, height, textureWidth, textureHeight);
   }

   public void renderText(Component text, float x, float y, boolean centeredX, boolean centeredY) {
      FormattedCharSequence formatted = (FormattedCharSequence)this.font.split(text, 9000).get(0);
      float offsetX = centeredX ? (float)(-this.font.width(formatted) / 2.0) : 0.0F;
      float offsetY = centeredY ? (float)(-9 / 2.0) : 0.0F;
      this.font.drawInBatch(formatted, x + offsetX, y + offsetY, 16777215, false, this.matrices.last().pose(), this.bufferSource, true, 0, 15728880);
      RenderSystem.enableDepthTest();
      this.bufferSource.endBatch();
   }

   public void renderTextRight(Component text, float x, float y, boolean centeredY) {
      FormattedCharSequence formatted = (FormattedCharSequence)this.font.split(text, 9000).get(0);
      float offsetX = -this.font.width(formatted);
      float offsetY = centeredY ? (float)(-9 / 2.0) : 0.0F;
      this.font.drawInBatch(formatted, x + offsetX, y + offsetY, 16777215, false, this.matrices.last().pose(), this.bufferSource, true, 0, 15728880);
      RenderSystem.enableDepthTest();
      this.bufferSource.endBatch();
   }
}
