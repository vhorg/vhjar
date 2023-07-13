package iskallia.vault.client.gui.framework.element.spi;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

public interface IGuiEventElement extends IElement, GuiEventListener {
   default void onMouseMoved(double mouseX, double mouseY) {
   }

   default void mouseMoved(double mouseX, double mouseY) {
      if (this.isEnabled()) {
         this.onMouseMoved(mouseX, mouseY);
      }
   }

   default boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
      return true;
   }

   default boolean mouseClicked(double mouseX, double mouseY, int buttonIndex) {
      return this.isEnabled() && this.containsMouse(mouseX, mouseY) && this.onMouseClicked(mouseX, mouseY, buttonIndex);
   }

   default boolean onMouseReleased(double mouseX, double mouseY, int buttonIndex) {
      return true;
   }

   default boolean mouseReleased(double mouseX, double mouseY, int buttonIndex) {
      return this.isEnabled() && this.containsMouse(mouseX, mouseY) && this.onMouseReleased(mouseX, mouseY, buttonIndex);
   }

   default boolean onMouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      return true;
   }

   default boolean mouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      return this.isEnabled() && this.containsMouse(mouseX, mouseY) && this.onMouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
   }

   default boolean onMouseScrolled(double mouseX, double mouseY, double delta) {
      return true;
   }

   default boolean mouseScrolled(double mouseX, double mouseY, double delta) {
      return this.isEnabled() && this.containsMouse(mouseX, mouseY) && this.onMouseScrolled(mouseX, mouseY, delta);
   }

   default boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
      return true;
   }

   default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.isEnabled() && this.onKeyPressed(keyCode, scanCode, modifiers);
   }

   default boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
      return true;
   }

   default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      return this.isEnabled() && this.onKeyReleased(keyCode, scanCode, modifiers);
   }

   default boolean onCharTyped(char codePoint, int modifiers) {
      return true;
   }

   default boolean charTyped(char codePoint, int modifiers) {
      return this.isEnabled() && this.onCharTyped(codePoint, modifiers);
   }

   default boolean onChangeFocus(boolean focus) {
      return true;
   }

   default boolean changeFocus(boolean focus) {
      return this.isEnabled() && this.onChangeFocus(focus);
   }

   default boolean isMouseOver(double mouseX, double mouseY) {
      return this.containsMouse(mouseX, mouseY);
   }

   default boolean containsMouse(double x, double y) {
      return this instanceof ISpatialElement spatialElement ? spatialElement.contains(x, y) : false;
   }

   default void playDownSound(SoundManager soundManager) {
      soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }
}
