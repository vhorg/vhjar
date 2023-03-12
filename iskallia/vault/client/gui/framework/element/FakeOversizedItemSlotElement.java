package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.container.oversized.OverSizedItemStack;
import java.util.function.Supplier;
import net.minecraft.network.chat.TextComponent;

public class FakeOversizedItemSlotElement extends FakeItemSlotElement<FakeOversizedItemSlotElement> {
   private final Supplier<OverSizedItemStack> oversizedItemStack;

   public FakeOversizedItemSlotElement(ISpatial spatial, Supplier<OverSizedItemStack> itemStack, Supplier<Boolean> disabled) {
      super(spatial);
      this.oversizedItemStack = itemStack;
      this.itemStack = () -> this.oversizedItemStack.get().stack();
      this.disabled = disabled;
   }

   @Override
   public FakeItemSlotElement<FakeOversizedItemSlotElement> setLabelStackCount() {
      this.labelSupplier = () -> {
         OverSizedItemStack stack = this.getOversizedItemStack();
         return stack.amount() <= 1 ? null : new TextComponent(String.valueOf(stack.amount()));
      };
      return this;
   }

   public OverSizedItemStack getOversizedItemStack() {
      return this.oversizedItemStack.get();
   }
}
