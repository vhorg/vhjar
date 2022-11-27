package iskallia.vault.client.atlas;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import java.util.function.Supplier;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public record TextureAtlasRegion(Supplier<ITextureAtlas> atlas, ResourceLocation resourceLocation) implements ISize {
   public static TextureAtlasRegion of(Supplier<ITextureAtlas> atlasSupplier, ResourceLocation resourceLocation) {
      return new TextureAtlasRegion(atlasSupplier, resourceLocation);
   }

   public void blit(PoseStack poseStack, IPosition position) {
      this.blit(poseStack, position.x(), position.y(), position.z());
   }

   public void blit(PoseStack poseStack, IPosition position, ISize size) {
      this.blit(poseStack, position, size.width(), size.height());
   }

   public void blit(PoseStack poseStack, IPosition position, int width, int height) {
      this.blit(poseStack, position.x(), position.y(), position.z(), width, height);
   }

   public void blit(PoseStack poseStack, int x, int y) {
      this.blit(poseStack, x, y, 0);
   }

   public void blit(PoseStack poseStack, int x, int y, int z) {
      TextureAtlasSprite sprite = this.atlas.get().getSprite(this.resourceLocation);
      this.blit(poseStack, x, y, z, sprite.getWidth(), sprite.getHeight(), sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
   }

   public void blit(PoseStack poseStack, int x, int y, int z, int width, int height) {
      TextureAtlasSprite sprite = this.atlas.get().getSprite(this.resourceLocation);
      this.blit(poseStack, x, y, z, width, height, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
   }

   public void blit(PoseStack poseStack, int x, int y, int z, int width, int height, float u0, float u1, float v0, float v1) {
      TextureAtlasSprite sprite = this.getSprite();
      u0 = Mth.clamp(u0, sprite.getU0(), sprite.getU1());
      u1 = Mth.clamp(u1, sprite.getU0(), sprite.getU1());
      v0 = Mth.clamp(v0, sprite.getV0(), sprite.getV1());
      v1 = Mth.clamp(v1, sprite.getV0(), sprite.getV1());
      this.bindTexture();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
      bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      this.buffer(bufferbuilder, poseStack, x, y, z, width, height, u0, u1, v0, v1);
      bufferbuilder.end();
      BufferUploader.end(bufferbuilder);
   }

   public void buffer(IMultiBuffer buffer, PoseStack poseStack, IPosition position) {
      this.buffer(buffer.getFor(this.atlas().get().getAtlasResourceLocation()), poseStack, position.x(), position.y(), position.z());
   }

   public void buffer(IMultiBuffer buffer, PoseStack poseStack, IPosition position, ISize size) {
      this.buffer(buffer, poseStack, position, size.width(), size.height());
   }

   public void buffer(IMultiBuffer buffer, PoseStack poseStack, IPosition position, int width, int height) {
      this.buffer(buffer.getFor(this.atlas().get().getAtlasResourceLocation()), poseStack, position.x(), position.y(), position.z(), width, height);
   }

   public void buffer(IMultiBuffer buffer, PoseStack poseStack, int x, int y) {
      this.buffer(buffer.getFor(this.atlas().get().getAtlasResourceLocation()), poseStack, x, y);
   }

   public void buffer(IMultiBuffer buffer, PoseStack poseStack, int x, int y, int z) {
      this.buffer(buffer.getFor(this.atlas().get().getAtlasResourceLocation()), poseStack, x, y, z);
   }

   public void buffer(IMultiBuffer buffer, PoseStack poseStack, int x, int y, int z, int width, int height) {
      this.buffer(buffer.getFor(this.atlas().get().getAtlasResourceLocation()), poseStack, x, y, z, width, height);
   }

   public void buffer(IMultiBuffer buffer, PoseStack poseStack, int x, int y, int z, int width, int height, float u0, float u1, float v0, float v1) {
      this.buffer(buffer.getFor(this.atlas().get().getAtlasResourceLocation()), poseStack, x, y, z, width, height, u0, u1, v0, v1);
   }

   public void buffer(VertexConsumer vertexConsumer, PoseStack poseStack, int x, int y) {
      TextureAtlasSprite sprite = this.getSprite();
      this.buffer(vertexConsumer, poseStack, x, y, 0, sprite.getWidth(), sprite.getHeight(), sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
   }

   public void buffer(VertexConsumer vertexConsumer, PoseStack poseStack, int x, int y, int z) {
      TextureAtlasSprite sprite = this.getSprite();
      this.buffer(vertexConsumer, poseStack, x, y, z, sprite.getWidth(), sprite.getHeight(), sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
   }

   public void buffer(VertexConsumer vertexConsumer, PoseStack poseStack, int x, int y, int z, int width, int height) {
      TextureAtlasSprite sprite = this.getSprite();
      this.buffer(vertexConsumer, poseStack, x, y, z, width, height, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
   }

   public void buffer(VertexConsumer vertexConsumer, PoseStack poseStack, int x, int y, int z, int width, int height, float u0, float u1, float v0, float v1) {
      TextureAtlasSprite sprite = this.getSprite();
      u0 = Mth.clamp(u0, sprite.getU0(), sprite.getU1());
      u1 = Mth.clamp(u1, sprite.getU0(), sprite.getU1());
      v0 = Mth.clamp(v0, sprite.getV0(), sprite.getV1());
      v1 = Mth.clamp(v1, sprite.getV0(), sprite.getV1());
      Matrix4f matrix = poseStack.last().pose();
      vertexConsumer.vertex(matrix, x, y + height, z).uv(u0, v1).endVertex();
      vertexConsumer.vertex(matrix, x + width, y + height, z).uv(u1, v1).endVertex();
      vertexConsumer.vertex(matrix, x + width, y, z).uv(u1, v0).endVertex();
      vertexConsumer.vertex(matrix, x, y, z).uv(u0, v0).endVertex();
   }

   public TextureAtlasSprite getSprite() {
      return this.atlas.get().getSprite(this.resourceLocation);
   }

   public float getU0(float percentage) {
      TextureAtlasSprite sprite = this.getSprite();
      float u0 = sprite.getU0();
      float u1 = sprite.getU1();
      return Mth.clamp(u1 - (u1 - u0) * percentage, u0, u1);
   }

   public float getU1(float percentage) {
      TextureAtlasSprite sprite = this.getSprite();
      float u0 = sprite.getU0();
      float u1 = sprite.getU1();
      return Mth.clamp(u0 + (u1 - u0) * percentage, u0, u1);
   }

   public float getV0(float percentage) {
      TextureAtlasSprite sprite = this.getSprite();
      float v0 = sprite.getV0();
      float v1 = sprite.getV1();
      return Mth.clamp(v1 - (v1 - v0) * percentage, v0, v1);
   }

   public float getV1(float percentage) {
      TextureAtlasSprite sprite = this.getSprite();
      float v0 = sprite.getV0();
      float v1 = sprite.getV1();
      return Mth.clamp(v0 + (v1 - v0) * percentage, v0, v1);
   }

   public void bindTexture() {
      RenderSystem.setShaderTexture(0, this.atlas.get().getAtlasResourceLocation());
   }

   @Override
   public String toString() {
      return "TextureAtlasRegion{" + this.atlas.get().getAtlasResourceLocation() + ", " + this.resourceLocation + "}";
   }

   @Override
   public int width() {
      return this.getSprite().getWidth();
   }

   @Override
   public int height() {
      return this.getSprite().getHeight();
   }

   public ISize size() {
      return this;
   }
}
