package iskallia.vault.client.gui.screen.summary.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.init.ModShaders;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class HeadTextureElement<E extends HeadTextureElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   private final ResourceLocation skin;
   private boolean visible;

   public HeadTextureElement(ResourceLocation skin) {
      this(IPosition.ZERO, skin);
   }

   public HeadTextureElement(IPosition position, ResourceLocation skin) {
      this(position, Spatials.size(16, 16), skin);
   }

   public HeadTextureElement(IPosition position, ISize size, ResourceLocation skin) {
      super(Spatials.positionXYZ(position).size(size));
      this.skin = skin;
      this.setVisible(true);
   }

   @Override
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Override
   public boolean isVisible() {
      return this.visible;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      poseStack.pushPose();
      poseStack.translate(this.worldSpatial.x(), this.worldSpatial.y(), this.worldSpatial.z());
      render2DHead(poseStack, this.skin, 16, false);
      poseStack.popPose();
   }

   private static void render2DHead(PoseStack matrixStack, ResourceLocation skin, int size, boolean grayscaled) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, skin);
      int u1 = 8;
      int v1 = 8;
      int u2 = 40;
      int v2 = 8;
      int w = 8;
      int h = 8;
      if (grayscaled) {
         ModShaders.getGrayscalePositionTexShader().withGrayscale(1.0F).withBrightness(1.0F).enable();
      } else {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
      }

      GuiComponent.blit(matrixStack, 0, 0, size, size, u1, v1, w, h, 64, 64);
      GuiComponent.blit(matrixStack, 0, 0, size, size, u2, v2, w, h, 64, 64);
   }
}
