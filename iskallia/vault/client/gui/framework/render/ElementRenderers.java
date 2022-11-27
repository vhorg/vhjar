package iskallia.vault.client.gui.framework.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import iskallia.vault.client.atlas.IMultiBuffer;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.render.spi.IClipRegionStrategy;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ElementRenderers {
   public static IElementRenderer immediate(final IClipRegionStrategy clipRegionStrategy) {
      return new IElementRenderer() {
         @Override
         public void render(Item item, PoseStack poseStack, IPosition position) {
            RenderSystem.disableDepthTest();
            Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(item), position.x(), position.y());
            RenderSystem.enableDepthTest();
         }

         @Override
         public void render(TextureAtlasRegion region, PoseStack poseStack, IPosition position) {
            region.blit(poseStack, position);
         }

         @Override
         public void render(TextureAtlasRegion region, PoseStack poseStack, IPosition position, ISize size) {
            region.blit(poseStack, position, size);
         }

         @Override
         public void render(TextureAtlasRegion region, PoseStack poseStack, int x, int y, int z) {
            region.blit(poseStack, x, y, z);
         }

         @Override
         public void render(TextureAtlasRegion region, PoseStack poseStack, int x, int y, int z, int width, int height) {
            region.blit(poseStack, x, y, z, width, height);
         }

         @Override
         public void render(TextureAtlasRegion region, PoseStack poseStack, int x, int y, int z, int width, int height, float u0, float u1, float v0, float v1) {
            region.blit(poseStack, x, y, z, width, height, u0, u1, v0, v1);
         }

         @Override
         public void render(NineSlice.TextureRegion region, PoseStack poseStack, ISpatial spatial) {
            region.blit(poseStack, spatial.x(), spatial.y(), spatial.z(), spatial.width(), spatial.height());
         }

         @Override
         public void render(NineSlice.TextureRegion region, PoseStack poseStack, int x, int y, int z, int width, int height) {
            region.blit(poseStack, x, y, z, width, height);
         }

         @Override
         public void renderColoredQuad(PoseStack poseStack, int color, int x, int y, int z, int width, int height) {
            ScreenDrawHelper.draw(
               Mode.QUADS,
               DefaultVertexFormat.POSITION_COLOR,
               buf -> ScreenDrawHelper.rect(buf, poseStack).at(x, y).zLevel(z).dim(width, height).color(color).drawColored()
            );
         }

         @Override
         public void renderPlain(FormattedCharSequence text, Font font, PoseStack poseStack, int x, int y, int z, int color) {
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            font.draw(poseStack, text, 0.0F, 0.0F, color);
            poseStack.popPose();
            RenderSystem.enableDepthTest();
         }

         @Override
         public void renderShadow(
            FormattedCharSequence text, FormattedCharSequence shadow, Font font, PoseStack poseStack, int x, int y, int z, int color, int shadowColor
         ) {
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            Matrix4f matrix = poseStack.last().pose();
            poseStack.pushPose();
            poseStack.translate(1.0, 1.0, -0.1);
            Matrix4f shadowMatrix = poseStack.last().pose();
            BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(text, 0.0F, 0.0F, color, false, matrix, bufferSource, false, 0, 15728880);
            font.drawInBatch(shadow, 0.0F, 0.0F, shadowColor, false, shadowMatrix, bufferSource, false, 0, 15728880);
            bufferSource.endBatch();
            poseStack.popPose();
            poseStack.popPose();
            RenderSystem.enableDepthTest();
         }

         @Override
         public void renderBorder4(
            FormattedCharSequence text, FormattedCharSequence border, Font font, PoseStack poseStack, int x, int y, int z, int color, int borderColor
         ) {
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, z);
            Matrix4f matrix = poseStack.last().pose();
            BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(border, x - 1, y, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            font.drawInBatch(border, x + 1, y, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            font.drawInBatch(border, x, y - 1, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            font.drawInBatch(border, x, y + 1, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            bufferSource.endBatch();
            font.draw(poseStack, text, x, y, color);
            poseStack.popPose();
            RenderSystem.enableDepthTest();
         }

         @Override
         public void renderBorder8(
            FormattedCharSequence text, FormattedCharSequence border, Font font, PoseStack poseStack, int x, int y, int z, int color, int borderColor
         ) {
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, z);
            Matrix4f matrix = poseStack.last().pose();
            BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(border, x - 1, y, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            font.drawInBatch(border, x + 1, y, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            font.drawInBatch(border, x, y - 1, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            font.drawInBatch(border, x, y + 1, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            font.drawInBatch(border, x - 1, y - 1, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            font.drawInBatch(border, x + 1, y + 1, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            font.drawInBatch(border, x + 1, y - 1, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            font.drawInBatch(border, x - 1, y + 1, borderColor, false, matrix, bufferSource, false, 0, 15728880);
            bufferSource.endBatch();
            font.draw(poseStack, text, x, y, color);
            poseStack.popPose();
            RenderSystem.enableDepthTest();
         }

         @Override
         public void beginFrame() {
            clipRegionStrategy.beginFrame();
         }

         @Override
         public void endFrame() {
            clipRegionStrategy.endFrame();
         }

         @Override
         public void beginClipRegion(ISpatial spatial) {
            clipRegionStrategy.beginClipRegion(spatial);
         }

         @Override
         public void endClipRegion() {
            clipRegionStrategy.endClipRegion();
         }
      };
   }

   @Nonnull
   public static IElementRenderer bufferedPosTex(final IMultiBuffer buffer, final IClipRegionStrategy clipRegionStrategy) {
      return new IElementRenderer() {
         private final BufferSource fontBuffer = MultiBufferSource.immediate(new BufferBuilder(256));

         @Override
         public void render(Item item, PoseStack poseStack, IPosition position) {
            RenderSystem.disableDepthTest();
            Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(item), position.x(), position.y());
            RenderSystem.enableDepthTest();
         }

         @Override
         public void render(TextureAtlasRegion region, PoseStack poseStack, IPosition position) {
            region.buffer(buffer, poseStack, position);
         }

         @Override
         public void render(TextureAtlasRegion region, PoseStack poseStack, IPosition position, ISize size) {
            region.buffer(buffer, poseStack, position, size);
         }

         @Override
         public void render(TextureAtlasRegion region, PoseStack poseStack, int x, int y, int z) {
            region.buffer(buffer, poseStack, x, y, z);
         }

         @Override
         public void render(TextureAtlasRegion region, PoseStack poseStack, int x, int y, int z, int width, int height) {
            region.buffer(buffer, poseStack, x, y, z, width, height);
         }

         @Override
         public void render(NineSlice.TextureRegion region, PoseStack poseStack, ISpatial spatial) {
            region.buffer(buffer, poseStack, spatial);
         }

         @Override
         public void render(NineSlice.TextureRegion region, PoseStack poseStack, int x, int y, int z, int width, int height) {
            region.buffer(buffer, poseStack, x, y, z, width, height);
         }

         @Override
         public void render(TextureAtlasRegion region, PoseStack poseStack, int x, int y, int z, int width, int height, float u0, float u1, float v0, float v1) {
            region.buffer(buffer, poseStack, x, y, z, width, height, u0, u1, v0, v1);
         }

         @Override
         public void renderColoredQuad(PoseStack poseStack, int color, int x, int y, int z, int width, int height) {
            throw new UnsupportedOperationException("Colored quads are not supported by buffered drawing (yet).");
         }

         @Override
         public void renderPlain(FormattedCharSequence text, Font font, PoseStack poseStack, int x, int y, int z, int color) {
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            font.drawInBatch(text, 0.0F, 0.0F, color, false, poseStack.last().pose(), this.fontBuffer, false, 0, 15728880);
            poseStack.popPose();
            RenderSystem.enableDepthTest();
         }

         @Override
         public void renderShadow(
            FormattedCharSequence text, FormattedCharSequence shadow, Font font, PoseStack poseStack, int x, int y, int z, int color, int shadowColor
         ) {
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            Matrix4f matrix = poseStack.last().pose();
            poseStack.pushPose();
            poseStack.translate(1.0, 1.0, -0.1);
            Matrix4f shadowMatrix = poseStack.last().pose();
            font.drawInBatch(text, 0.0F, 0.0F, color, false, matrix, this.fontBuffer, false, 0, 15728880);
            font.drawInBatch(shadow, 0.0F, 0.0F, shadowColor, false, shadowMatrix, this.fontBuffer, false, 0, 15728880);
            poseStack.popPose();
            poseStack.popPose();
            RenderSystem.enableDepthTest();
         }

         @Override
         public void renderBorder4(
            FormattedCharSequence text, FormattedCharSequence border, Font font, PoseStack poseStack, int x, int y, int z, int color, int borderColor
         ) {
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, z);
            Matrix4f matrix = poseStack.last().pose();
            font.drawInBatch(border, x - 1, y, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            font.drawInBatch(border, x + 1, y, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            font.drawInBatch(border, x, y - 1, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            font.drawInBatch(border, x, y + 1, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 0.1);
            font.drawInBatch(text, x, y, color, false, poseStack.last().pose(), this.fontBuffer, false, 0, 15728880);
            poseStack.popPose();
            poseStack.popPose();
            RenderSystem.enableDepthTest();
         }

         @Override
         public void renderBorder8(
            FormattedCharSequence text, FormattedCharSequence border, Font font, PoseStack poseStack, int x, int y, int z, int color, int borderColor
         ) {
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, z);
            Matrix4f matrix = poseStack.last().pose();
            font.drawInBatch(border, x - 1, y, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            font.drawInBatch(border, x + 1, y, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            font.drawInBatch(border, x, y - 1, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            font.drawInBatch(border, x, y + 1, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            font.drawInBatch(border, x - 1, y - 1, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            font.drawInBatch(border, x + 1, y + 1, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            font.drawInBatch(border, x + 1, y - 1, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            font.drawInBatch(border, x - 1, y + 1, borderColor, false, matrix, this.fontBuffer, false, 0, 15728880);
            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 0.1);
            font.drawInBatch(text, x, y, color, false, poseStack.last().pose(), this.fontBuffer, false, 0, 15728880);
            poseStack.popPose();
            poseStack.popPose();
            RenderSystem.enableDepthTest();
         }

         @Override
         public void beginFrame() {
            clipRegionStrategy.beginFrame();
         }

         @Override
         public void endFrame() {
            clipRegionStrategy.endFrame();
         }

         @Override
         public void begin() {
            buffer.begin();
         }

         @Override
         public void end() {
            buffer.end(bufferx -> GameRenderer::getPositionTexShader);
            this.fontBuffer.endBatch();
            RenderSystem.enableDepthTest();
         }

         @Override
         public void beginClipRegion(ISpatial spatial) {
            this.end();
            clipRegionStrategy.beginClipRegion(spatial);
            this.begin();
         }

         @Override
         public void endClipRegion() {
            this.end();
            clipRegionStrategy.endClipRegion();
            this.begin();
         }
      };
   }

   private ElementRenderers() {
   }
}
