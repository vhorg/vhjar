package iskallia.vault.container.slot;

import iskallia.vault.container.slot.spi.IMovableSlot;
import iskallia.vault.mixin.AccessorSlot;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class TabSlot extends Slot implements IMovableSlot {
   private final int originX;
   private final int originY;
   private boolean isActive = true;

   public TabSlot(Container container, int index, int x, int y) {
      super(container, index, x, y);
      this.originX = x;
      this.originY = y;
   }

   @Override
   public void setPositionOffset(int x, int y) {
      ((AccessorSlot)this).setX(this.originX + x);
      ((AccessorSlot)this).setY(this.originY + y);
   }

   public void setActive(boolean active) {
      this.isActive = active;
   }

   public boolean isActive() {
      return this.isActive;
   }
}
