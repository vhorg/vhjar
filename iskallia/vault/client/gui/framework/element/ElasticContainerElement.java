package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.element.spi.ISpatialElement;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;

public class ElasticContainerElement<E extends ElasticContainerElement<E>> extends ContainerElement<E> {
   protected ElasticContainerElement(ISpatial spatial) {
      super(spatial);
   }

   @Override
   protected boolean layoutSelfPost(ISize screen, ISpatial gui, ISpatial parent) {
      this.layoutIncludeChildren();
      return super.layoutSelfPost(screen, gui, parent);
   }

   protected void layoutIncludeChildren() {
      for (ISpatialElement element : this.elementStore.getSpatialElementList()) {
         this.worldSpatial.include(element);
         this.layoutDebugLogger
            .out("[{}: world.include(child.world)] world = {}, child.world={}", this.getClass().getSimpleName(), this.worldSpatial, element.getWorldSpatial());
      }
   }
}
