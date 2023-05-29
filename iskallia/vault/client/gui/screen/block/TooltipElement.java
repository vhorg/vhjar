package iskallia.vault.client.gui.screen.block;

import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelAutoResize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public class TooltipElement<V> extends ElasticContainerElement<TooltipElement<V>> {
   public TooltipElement(IPosition position, ISize size, Component tooltip) {
      super(Spatials.positionXYZ(position));
      this.addElements(
         new TooltipElement.NameElement(IPosition.ZERO, size, () -> tooltip).layout((screen, gui, parent, world) -> world.width(parent)), new IElement[0]
      );
   }

   private static class NameElement extends DynamicLabelElement<Component, TooltipElement.NameElement> {
      public NameElement(IPosition position, ISize size, Supplier<Component> tooltip) {
         super(position, size, tooltip, LabelTextStyle.shadow().left().wrap());
         this.setAutoResize(LabelAutoResize.NONE);
      }

      protected void onValueChanged(Component value) {
         this.set(value);
      }
   }
}
