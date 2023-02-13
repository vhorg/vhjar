package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.screen.layout.ScreenLayout;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;

public class ScrollableItemStackSelectorElement<E extends ScrollableItemStackSelectorElement<E, S>, S extends ScrollableItemStackSelectorElement.ItemSelectorEntry>
   extends VerticalScrollClipContainer<E> {
   private final int slotColumns;
   protected final ScrollableItemStackSelectorElement.SelectorModel<S> selectorModel;
   protected final TextureAtlasRegion slotTexture;
   protected final TextureAtlasRegion disabledSlotTexture;
   private final ScrollableItemStackSelectorElement<E, S>.SelectorContainer<?> elementCt;

   public ScrollableItemStackSelectorElement(ISpatial spatial, int slotColumns, ScrollableItemStackSelectorElement.SelectorModel<S> selectorModel) {
      this(spatial, slotColumns, selectorModel, ScreenTextures.INSET_ITEM_SLOT_BACKGROUND, ScreenTextures.INSET_DISABLED_ITEM_SLOT_BACKGROUND);
   }

   public ScrollableItemStackSelectorElement(
      ISpatial spatial,
      int slotColumns,
      ScrollableItemStackSelectorElement.SelectorModel<S> selectorModel,
      TextureAtlasRegion slotTexture,
      TextureAtlasRegion disabledSlotTexture
   ) {
      super(Spatials.copy(spatial).width(slotColumns * slotTexture.width() + 17));
      this.slotColumns = slotColumns;
      this.selectorModel = selectorModel;
      this.slotTexture = slotTexture;
      this.disabledSlotTexture = disabledSlotTexture;
      this.addElement(this.elementCt = new ScrollableItemStackSelectorElement.SelectorContainer(spatial.width()));
   }

   public void refreshElements() {
      this.elementCt.removeAllElements();
      this.elementCt.buildElements();
      ScreenLayout.requestLayout();
   }

   protected ScrollableItemStackSelectorElement.SelectorModel<S> getSelectorModel() {
      return this.selectorModel;
   }

   protected List<FakeItemSlotElement<?>> getSelectorElements() {
      return Collections.unmodifiableList(this.elementCt.slots);
   }

   protected FakeItemSlotElement<?> makeElementSlot(
      ISpatial spatial, Supplier<ItemStack> itemStack, TextureAtlasRegion slotTexture, TextureAtlasRegion disabledSlotTexture, Supplier<Boolean> disabled
   ) {
      return new FakeItemSlotElement(spatial, itemStack, disabled, slotTexture, disabledSlotTexture);
   }

   public static class ItemSelectorEntry {
      private final ItemStack displayStack;
      private final boolean isDisabled;

      public ItemSelectorEntry(ItemStack displayStack, boolean isDisabled) {
         this.displayStack = displayStack;
         this.isDisabled = isDisabled;
      }

      public ItemStack getDisplayStack() {
         return this.displayStack;
      }

      public boolean isDisabled() {
         return this.isDisabled;
      }

      public void adjustSlot(FakeItemSlotElement<?> slot) {
      }
   }

   private class SelectorContainer<T extends ScrollableItemStackSelectorElement<E, S>.SelectorContainer<T>> extends ElasticContainerElement<T> {
      private final List<FakeItemSlotElement<?>> slots = new ArrayList<>();

      private SelectorContainer(int inheritedWidth) {
         super(Spatials.positionXY(0, 0).width(inheritedWidth));
         this.buildElements();
      }

      private void buildElements() {
         this.slots.clear();
         List<S> entries = ScrollableItemStackSelectorElement.this.selectorModel.getEntries();

         for (int i = 0; i < entries.size(); i++) {
            S entry = entries.get(i);
            int column = i % ScrollableItemStackSelectorElement.this.slotColumns;
            int row = i / ScrollableItemStackSelectorElement.this.slotColumns;
            ItemStack stack = entry.getDisplayStack();
            boolean disabled = entry.isDisabled();
            FakeItemSlotElement<?> fakeSlot = ScrollableItemStackSelectorElement.this.makeElementSlot(
               Spatials.positionXY(0, 0)
                  .translateX(column * ScrollableItemStackSelectorElement.this.slotTexture.width())
                  .translateY(row * ScrollableItemStackSelectorElement.this.slotTexture.height()),
               () -> stack,
               ScrollableItemStackSelectorElement.this.slotTexture,
               ScrollableItemStackSelectorElement.this.disabledSlotTexture,
               () -> disabled
            );
            fakeSlot.whenClicked(() -> ScrollableItemStackSelectorElement.this.selectorModel.onSelect(fakeSlot, entry));
            entry.adjustSlot(fakeSlot);
            this.addElement((T)fakeSlot);
            this.slots.add(fakeSlot);
         }
      }
   }

   public abstract static class SelectorModel<E extends ScrollableItemStackSelectorElement.ItemSelectorEntry> {
      private Consumer<FakeItemSlotElement<?>> onSlotSelect = slot -> {};
      private E selectedElement = (E)null;

      protected void onSlotSelect(Consumer<FakeItemSlotElement<?>> onSlotSelect) {
         this.onSlotSelect = onSlotSelect;
      }

      public abstract List<E> getEntries();

      public void onSelect(FakeItemSlotElement<?> slot, E entry) {
         this.selectedElement = entry;
         this.onSlotSelect.accept(slot);
      }

      @Nullable
      public E getSelectedElement() {
         return this.selectedElement;
      }
   }
}
