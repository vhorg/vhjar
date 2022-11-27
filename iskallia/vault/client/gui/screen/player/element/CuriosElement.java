package iskallia.vault.client.gui.screen.player.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.VerticalScrollbarElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CuriosElement<E extends CuriosElement<E>> extends ElasticContainerElement<E> {
   private final Supplier<ISpatial> tabContentSpatialSupplier;
   private final VerticalScrollbarElement<?> scrollbarElement;
   private final boolean canScroll;
   private final int visibleSlotCount;

   public CuriosElement(Supplier<ISpatial> tabContentSpatialSupplier, boolean canScroll, int visibleSlotCount, Consumer<Float> scrollbarValueChangedAction) {
      super(Spatials.positionXYZ(-28, -4, -20));
      this.tabContentSpatialSupplier = tabContentSpatialSupplier;
      this.canScroll = canScroll;
      this.visibleSlotCount = visibleSlotCount;
      this.addElement((NineSliceElement)new NineSliceElement(Spatials.width(32), ScreenTextures.DEFAULT_WINDOW_BACKGROUND).layout(this::layoutBackground));
      this.scrollbarElement = this.addElement(
         new VerticalScrollbarElement(Spatials.positionXYZ(6, 8, 10), 0, scrollbarValueChangedAction).layout(this::layoutScrollbar)
      );
      this.scrollbarElement.setEnabled(this.canScroll);
      this.scrollbarElement.setVisible(this.canScroll);
   }

   private void layoutBackground(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world) {
      ISpatial tabContentSpatial = this.tabContentSpatialSupplier.get();
      if (this.canScroll) {
         world.translateX(gui.left() - this.scrollbarElement.width());
         world.width(world.width() + this.scrollbarElement.width());
      } else {
         world.translateX(gui);
      }

      world.translateY(tabContentSpatial.bottom());
      world.height(this.visibleSlotCount * 18 + 16);
   }

   private void layoutScrollbar(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world) {
      ISpatial tabContentSpatial = this.tabContentSpatialSupplier.get();
      world.translateX(gui.left() - world.width());
      world.translateY(tabContentSpatial.bottom());
      world.height(this.visibleSlotCount * 18);
   }
}
