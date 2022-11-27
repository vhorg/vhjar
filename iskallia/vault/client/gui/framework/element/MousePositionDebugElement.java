package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import net.minecraft.network.chat.TextComponent;

public class MousePositionDebugElement<E extends MousePositionDebugElement<E>> extends LabelElement<E> implements IGuiEventElement {
   public MousePositionDebugElement(IPosition position, LabelTextStyle.Builder labelTextStyle) {
      super(position, labelTextStyle);
   }

   @Override
   public void onMouseMoved(double mouseX, double mouseY) {
      this.set(new TextComponent((int)Math.floor(mouseX) + " " + (int)Math.floor(mouseY)));
   }
}
