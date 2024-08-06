package iskallia.vault.container.slot;

import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ChangeListenerSlot extends FilteredSlot {
   private Runnable slotChangeListener = () -> {};

   public ChangeListenerSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Predicate<ItemStack> stackFilter) {
      super(itemHandler, index, xPosition, yPosition, stackFilter);
   }

   public void addListener(Runnable run) {
      Runnable existing = this.slotChangeListener;
      this.slotChangeListener = () -> {
         existing.run();
         run.run();
      };
   }

   public void setChanged() {
      super.setChanged();
      this.slotChangeListener.run();
   }
}
