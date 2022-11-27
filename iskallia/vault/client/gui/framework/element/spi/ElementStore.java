package iskallia.vault.client.gui.framework.element.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElementStore {
   private final List<IGuiEventElement> guiEventListenerList = new ArrayList<>(0);
   private final List<IRenderedElement> renderedElementList = new ArrayList<>(0);
   private final List<ISpatialElement> spatialElementList = new ArrayList<>(0);
   private final List<ILayoutElement<?>> layoutElementList = new ArrayList<>(0);
   private final List<ITooltipElement> tooltipElementList = new ArrayList<>(0);
   private final List<IGuiEventElement> unmodifiableGuiEventElementList = Collections.unmodifiableList(this.guiEventListenerList);
   private final List<IRenderedElement> unmodifiableRenderedElementList = Collections.unmodifiableList(this.renderedElementList);
   private final List<ISpatialElement> unmodifiableSpatialElementList = Collections.unmodifiableList(this.spatialElementList);
   private final List<ILayoutElement<?>> unmodifiableLayoutElementList = Collections.unmodifiableList(this.layoutElementList);
   private final List<ITooltipElement> unmodifiableTooltipElementList = Collections.unmodifiableList(this.tooltipElementList);

   public <E extends IElement> E addElement(E element) {
      if (element instanceof IGuiEventElement guiEventListener) {
         this.guiEventListenerList.add(guiEventListener);
      }

      if (element instanceof IRenderedElement renderedElement) {
         this.renderedElementList.add(renderedElement);
      }

      if (element instanceof ISpatialElement spatialElement) {
         this.spatialElementList.add(spatialElement);
      }

      if (element instanceof ILayoutElement layoutElement) {
         this.layoutElementList.add(layoutElement);
      }

      if (element instanceof ITooltipElement tooltipElement) {
         this.tooltipElementList.add(tooltipElement);
      }

      return element;
   }

   public void removeElement(IElement element) {
      if (element instanceof IGuiEventElement guiEventListener) {
         this.guiEventListenerList.remove(guiEventListener);
      }

      if (element instanceof IRenderedElement renderedElement) {
         this.renderedElementList.remove(renderedElement);
      }

      if (element instanceof ISpatialElement spatialElement) {
         this.spatialElementList.remove(spatialElement);
      }

      if (element instanceof ILayoutElement layoutElement) {
         this.layoutElementList.remove(layoutElement);
      }

      if (element instanceof ITooltipElement tooltipElement) {
         this.tooltipElementList.remove(tooltipElement);
      }
   }

   public void removeAllElements() {
      this.guiEventListenerList.clear();
      this.renderedElementList.clear();
      this.spatialElementList.clear();
      this.layoutElementList.clear();
      this.tooltipElementList.clear();
   }

   public List<IGuiEventElement> getGuiEventElementList() {
      return this.unmodifiableGuiEventElementList;
   }

   public List<IRenderedElement> getRenderedElementList() {
      return this.unmodifiableRenderedElementList;
   }

   public List<ISpatialElement> getSpatialElementList() {
      return this.unmodifiableSpatialElementList;
   }

   public List<ILayoutElement<?>> getLayoutElementList() {
      return this.unmodifiableLayoutElementList;
   }

   public List<ITooltipElement> getTooltipElementList() {
      return this.unmodifiableTooltipElementList;
   }
}
