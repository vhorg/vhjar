package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.screen.layout.ScreenLayout;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public abstract class ScrollableListSelectorElement<E extends ScrollableListSelectorElement<E, V, S>, V, S extends SelectableElement<S>>
   extends VerticalScrollClipContainer<E> {
   protected final ScrollableListSelectorElement.SelectorModel<S, V> selectorModel;
   private final ScrollableListSelectorElement<E, V, S>.SelectorContainer<?> elementContainer;

   public ScrollableListSelectorElement(ISpatial spatial, ScrollableListSelectorElement.SelectorModel<S, V> selectorModel) {
      super(Spatials.copy(spatial).width(spatial.width() + 17));
      this.selectorModel = selectorModel;
      this.addElement(this.elementContainer = new ScrollableListSelectorElement.SelectorContainer(spatial.width()));
   }

   public void refreshElements() {
      this.elementContainer.removeAllElements();
      this.elementContainer.buildElements();
      ScreenLayout.requestLayout();
   }

   protected ScrollableListSelectorElement.SelectorModel<S, V> getSelectorModel() {
      return this.selectorModel;
   }

   private class SelectorContainer<T extends ScrollableListSelectorElement<E, V, S>.SelectorContainer<T>> extends ElasticContainerElement<T> {
      protected final List<S> selectables = new ArrayList();

      private SelectorContainer(int width) {
         super(Spatials.positionXY(0, 0).width(width));
         this.buildElements();
      }

      public void buildElements() {
         this.selectables.clear();
         ScrollableListSelectorElement.SelectorModel<S, V> selectorModel = ScrollableListSelectorElement.this.getSelectorModel();
         int offsetY = 0;

         for (V entry : selectorModel.getEntries()) {
            ISpatial position = Spatials.positionXY(0, offsetY);
            S selectable = selectorModel.createSelectable(position, entry);
            selectable.onSelect(selectorModel::select);
            selectable.onSelect(this::updateSelection);
            this.addElement((T)selectable);
            this.selectables.add(selectable);
            offsetY += selectable.height();
         }
      }

      private void updateSelection(S selected) {
         this.selectables.forEach(element -> element.setSelected(false));
         selected.setSelected(true);
      }
   }

   public abstract static class SelectorModel<S extends SelectableElement<S>, V> {
      private Consumer<S> onSelect = selected -> {};
      private S selectedElement = (S)null;

      public abstract List<V> getEntries();

      public abstract S createSelectable(ISpatial var1, V var2);

      public void whenSelected(Consumer<S> onSelect) {
         this.onSelect = this.onSelect.andThen(onSelect);
      }

      public void select(S entry) {
         this.selectedElement = entry;
         this.onSelect.accept(entry);
      }

      @Nullable
      public S getSelectedElement() {
         return this.selectedElement;
      }
   }
}
