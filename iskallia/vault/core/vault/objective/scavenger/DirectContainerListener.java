package iskallia.vault.core.vault.objective.scavenger;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class DirectContainerListener implements ContainerListener {
   private final DirectContainerListener.SlotChanged slotChanged;
   private final DirectContainerListener.DataChanged dataChanged;

   public DirectContainerListener(DirectContainerListener.SlotChanged slotChanged, DirectContainerListener.DataChanged dataChanged) {
      this.slotChanged = slotChanged;
      this.dataChanged = dataChanged;
   }

   public static DirectContainerListener ofSlot(DirectContainerListener.SlotChanged slotChanged) {
      return new DirectContainerListener(slotChanged, (container, slotIndex, value) -> {});
   }

   public static DirectContainerListener ofData(DirectContainerListener.DataChanged dataChanged) {
      return new DirectContainerListener((container, slotIndex, stack) -> {}, dataChanged);
   }

   public void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack stack) {
      this.slotChanged.slotChanged(container, slotIndex, stack);
   }

   public void dataChanged(AbstractContainerMenu container, int slotIndex, int value) {
      this.dataChanged.dataChanged(container, slotIndex, value);
   }

   @FunctionalInterface
   public interface DataChanged {
      void dataChanged(AbstractContainerMenu var1, int var2, int var3);
   }

   @FunctionalInterface
   public interface SlotChanged {
      void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3);
   }
}
