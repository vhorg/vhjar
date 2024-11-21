package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ScalableSliderElement extends AbstractSpatialElement<ScalableSliderElement> implements IRenderedElement, IGuiEventElement {
   protected boolean visible = true;
   protected boolean clickHeld;
   protected float percent;
   protected Consumer<Float> onValueChanged;

   public ScalableSliderElement(ISpatial spatial, Consumer<Float> onValueChanged) {
      super(spatial);
      this.onValueChanged = onValueChanged;
   }

   public void setValue(float value) {
      this.percent = Mth.clamp(value, 0.0F, 1.0F);
      this.onValueChanged.accept(this.percent);
   }

   private void setValueFromMouse(double mouseX) {
      this.setValue(((float)mouseX - (this.getWorldSpatial().x() + 2)) / (this.getWorldSpatial().width() - 4.0F));
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int buttonIndex) {
      if (this.containsMouse(mouseX, mouseY)) {
         this.clickHeld = true;
         int minX = this.x() + 2;
         int maxX = this.x() + this.width() - 2;
         this.setValueFromMouse((float)Mth.clamp(mouseX, minX, maxX));
      }

      return IGuiEventElement.super.mouseClicked(mouseX, mouseY, buttonIndex);
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int buttonIndex) {
      if (this.clickHeld) {
         int minX = this.x() + 2;
         int maxX = this.x() + this.width() - 2;
         this.setValueFromMouse((float)Mth.clamp(mouseX, minX, maxX));
         this.clickHeld = false;
         Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }

      return IGuiEventElement.super.mouseReleased(mouseX, mouseY, buttonIndex);
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      if (!this.isMouseOver(mouseX, mouseY)) {
         this.mouseReleased(mouseX, mouseY, buttonIndex);
      }

      if (this.clickHeld) {
         int minX = this.x() + 2;
         int maxX = this.x() + this.width() - 2;
         this.setValueFromMouse((float)Mth.clamp(mouseX, minX, maxX));
      }

      return IGuiEventElement.super.mouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
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
      ISpatial spatial = this.getWorldSpatial();
      ScreenTextures.SLIDER_BAR_SLICES.blit(poseStack, spatial.x(), spatial.y(), spatial.z(), spatial.width(), spatial.height());
      int yOffset = -Math.max((10 - spatial.height()) / 2, 0);
      int x = (int)(spatial.x() + this.percent * (spatial.width() - 4));
      if (!this.isMouseOver(mouseX, mouseY) && !this.clickHeld) {
         renderer.render(ScreenTextures.SLIDER_SMALL, poseStack, x, spatial.y() + yOffset, spatial.z(), 4, 10);
      } else {
         renderer.render(ScreenTextures.SLIDER_SMALL_HOVER, poseStack, x, spatial.y() + yOffset, spatial.z(), 4, 10);
      }
   }
}
