package iskallia.vault.client.gui.screen.block;

import iskallia.vault.client.gui.framework.element.VerticalScrollClipContainer;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import net.minecraft.world.item.ItemStack;

public class TooltipContainerElement extends VerticalScrollClipContainer<TooltipContainerElement> {
   TooltipListElement<?> tooltip;
   ItemStack stack;

   public TooltipContainerElement(ISpatial spatial, ItemStack stack) {
      super(spatial, Padding.of(2, 0));
      this.stack = stack;
      this.tooltip = this.addElement(
         new TooltipListElement(Spatials.positionY(3).width(spatial.width()), stack).layout((screen, gui, parent, world) -> world.width(this.innerWidth()))
      );
   }

   public void refresh(ItemStack stack) {
      if (this.stack != stack) {
         this.tooltip.refresh(stack);
         this.stack = stack;
      }
   }
}
