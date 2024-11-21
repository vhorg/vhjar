package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SliderElement extends AbstractSpatialElement<SliderElement> implements IRenderedElement, IGuiEventElement {
   protected boolean visible = true;
   protected Supplier<Component> message;
   protected LabelTextStyle textStyle;
   protected Supplier<Float> value;
   protected boolean clickHeld;
   protected Consumer<Float> onValueChanged;

   public SliderElement(ISpatial spatial, Supplier<Component> message, Supplier<Float> value, Consumer<Float> onValueChanged) {
      this(spatial, message, LabelTextStyle.shadow().center(), value, onValueChanged);
   }

   public SliderElement(ISpatial spatial, Supplier<Component> message, LabelTextStyle.Builder style, Supplier<Float> value, Consumer<Float> onValueChanged) {
      super(spatial);
      this.message = message;
      this.textStyle = style.build();
      this.value = value;
      this.onValueChanged = onValueChanged;
   }

   public void setValue(float value) {
      this.value = () -> (float)Mth.clamp(value, 0.0, 1.0);
      this.onValueChanged.accept(this.value.get());
   }

   private void setValueFromMouse(double mouseX) {
      this.setValue(((float)mouseX - (this.getWorldSpatial().x() + 4)) / (this.getWorldSpatial().width() - 8.0F));
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int buttonIndex) {
      if (this.containsMouse(mouseX, mouseY)) {
         this.clickHeld = true;
         int minX = this.x() + 4;
         int maxX = this.x() + this.width() - 4;
         this.setValueFromMouse((float)Mth.clamp(mouseX, minX, maxX));
      }

      return IGuiEventElement.super.mouseClicked(mouseX, mouseY, buttonIndex);
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int buttonIndex) {
      if (this.clickHeld) {
         int minX = this.x() + 4;
         int maxX = this.x() + this.width() - 4;
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
         int minX = this.x() + 4;
         int maxX = this.x() + this.width() - 4;
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

   public Component getComponent() {
      return this.message.get();
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      ISpatial spatial = this.getWorldSpatial();
      renderer.render(ScreenTextures.SLIDER_BACKGROUND, poseStack, spatial.x(), spatial.y(), spatial.z(), spatial.width(), spatial.height());
      int minX = this.x();
      int maxX = this.x() + this.width() - 8;
      int x = this.clickHeld && this.isMouseOver(mouseX, mouseY)
         ? Mth.clamp(mouseX - 4, minX, maxX)
         : (int)(spatial.x() + this.value.get() * (this.width() - 8));
      if (!this.isMouseOver(mouseX, mouseY) && !this.clickHeld) {
         renderer.render(ScreenTextures.SLIDER, poseStack, x, spatial.y(), spatial.z(), 8, spatial.height());
      } else {
         renderer.render(ScreenTextures.SLIDER_HOVER, poseStack, x, spatial.y(), spatial.z(), 8, spatial.height());
      }

      this.textStyle
         .textBorder()
         .render(
            renderer,
            poseStack,
            this.getComponent().copy().append(String.format(": %s%s", (int)(this.value.get() * 100.0F), "%")),
            this.textStyle.textWrap(),
            this.textStyle.textAlign(),
            this.getWorldSpatial().x(),
            this.getWorldSpatial().y() + spatial.height() / 2 - 9 / 2,
            this.getWorldSpatial().z() + 1,
            this.getWorldSpatial().width()
         );
   }
}
