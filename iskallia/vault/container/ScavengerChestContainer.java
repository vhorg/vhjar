package iskallia.vault.container;

import iskallia.vault.container.slot.FilteredSlotWrapper;
import iskallia.vault.init.ModContainers;
import iskallia.vault.item.BasicScavengerItem;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

public class ScavengerChestContainer extends ChestMenu {
   private final Container chestOwner;

   public ScavengerChestContainer(int id, Inventory playerInventory, Container chestOwner, Container scavengerOwner) {
      super(ModContainers.SCAVENGER_CHEST_CONTAINER, id, playerInventory, scavengerOwner, 5);
      this.chestOwner = chestOwner;
   }

   protected Slot addSlot(Slot slot) {
      if (!(slot.container instanceof Inventory)) {
         slot = new FilteredSlotWrapper(slot, stack -> stack.getItem() instanceof BasicScavengerItem);
      }

      return super.addSlot(slot);
   }

   public void removed(Player playerIn) {
      super.removed(playerIn);
      if (!(this.getContainer() instanceof ChestBlockEntity)) {
         this.chestOwner.stopOpen(playerIn);
      }
   }
}
