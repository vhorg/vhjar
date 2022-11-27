package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class ButtonElement<E extends ButtonElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement, IGuiEventElement {
   private final ButtonElement.ButtonTextures textures;
   private final Runnable onClick;
   protected boolean visible;
   protected Supplier<Boolean> disabled;
   private boolean clickHeld = false;

   public ButtonElement(IPosition position, ButtonElement.ButtonTextures textures, Runnable onClick) {
      super(Spatials.positionXYZ(position).size(textures.button().size()));
      this.textures = textures;
      this.onClick = onClick;
      this.setVisible(true);
      this.setDisabled(false);
   }

   @Override
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Override
   public boolean isVisible() {
      return this.visible;
   }

   public ButtonElement<E> setDisabled(boolean disabled) {
      this.setDisabled(() -> disabled);
      return this;
   }

   public ButtonElement<E> setDisabled(Supplier<Boolean> disabled) {
      this.disabled = disabled;
      return this;
   }

   public boolean isDisabled() {
      return this.disabled.get();
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
      this.clickHeld = true;
      return true;
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      boolean dragged = IGuiEventElement.super.mouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
      if (!this.containsMouse(mouseX, mouseY)) {
         this.clickHeld = false;
      }

      return dragged;
   }

   @Override
   public boolean onMouseReleased(double mouseX, double mouseY, int buttonIndex) {
      if (!this.isDisabled() && this.clickHeld) {
         this.onClick.run();
      }

      return true;
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int buttonIndex) {
      boolean release = IGuiEventElement.super.mouseReleased(mouseX, mouseY, buttonIndex);
      this.clickHeld = false;
      return release;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      TextureAtlasRegion texture = this.textures.selectTexture(this.isDisabled(), this.containsMouse(mouseX, mouseY), this.clickHeld);
      renderer.render(texture, poseStack, this.worldSpatial);
   }

   public record ButtonTextures(TextureAtlasRegion button, TextureAtlasRegion buttonHover, TextureAtlasRegion buttonHeld, TextureAtlasRegion buttonDisabled) {
      private TextureAtlasRegion selectTexture(boolean disabled, boolean hover, boolean clicked) {
         if (disabled) {
            return this.buttonDisabled();
         } else if (clicked) {
            return this.buttonHeld();
         } else {
            return hover ? this.buttonHover() : this.button();
         }
      }
   }
}
