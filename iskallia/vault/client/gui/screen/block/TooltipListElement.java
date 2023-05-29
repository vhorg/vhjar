package iskallia.vault.client.gui.screen.block;

import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;

public class TooltipListElement<E extends TooltipListElement<E>> extends ElasticContainerElement<E> {
   private static final int LABEL_HEIGHT = 11;
   private List<TooltipElement<?>> tooltips = new ArrayList<>();

   public TooltipListElement(ISpatial spatial, ItemStack stack) {
      super(spatial);
      this.refresh(stack);
   }

   public void refresh(ItemStack stack) {
      for (TooltipElement<?> element : this.tooltips) {
         this.removeElement(element);
      }

      this.tooltips.clear();
      if (!stack.isEmpty()) {
         int lines = 0;
         int iterations = 0;

         for (Component value : stack.getTooltipLines(Minecraft.getInstance().player, Default.NORMAL)) {
            int finalLines = lines;
            int finalIterations = iterations;
            this.tooltips
               .add(
                  this.addElement(
                     (TooltipElement<?>)new TooltipElement(ISpatial.ZERO, Spatials.size(this.getWorldSpatial().width(), finalIterations == 0 ? 11 : 9), value)
                        .layout((screen, gui, parent, world) -> world.translateY(finalLines * 9 + (finalIterations == 0 ? 0 : 2)).translateZ(1).width(parent))
                  )
               );
            int fontWidth = Minecraft.getInstance().font.width(value);
            double width = (double)fontWidth / this.getWorldSpatial().width();
            if (width == 0.0) {
               lines++;
            } else {
               lines = (int)(lines + Math.ceil(width));
            }

            iterations++;
         }
      }
   }
}
