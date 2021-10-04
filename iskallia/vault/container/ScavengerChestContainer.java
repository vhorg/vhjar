package iskallia.vault.container;

import iskallia.vault.container.slot.FilteredSlotWrapper;
import iskallia.vault.init.ModContainers;
import iskallia.vault.item.BasicScavengerItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.tileentity.ChestTileEntity;

public class ScavengerChestContainer extends ChestContainer {
   private final IInventory chestOwner;

   public ScavengerChestContainer(int id, PlayerInventory playerInventory, IInventory chestOwner, IInventory scavengerOwner) {
      super(ModContainers.SCAVENGER_CHEST_CONTAINER, id, playerInventory, scavengerOwner, 5);
      this.chestOwner = chestOwner;
   }

   protected Slot func_75146_a(Slot slot) {
      if (!(slot.field_75224_c instanceof PlayerInventory)) {
         slot = new FilteredSlotWrapper(slot, stack -> stack.func_77973_b() instanceof BasicScavengerItem);
      }

      return super.func_75146_a(slot);
   }

   public void func_75134_a(PlayerEntity playerIn) {
      super.func_75134_a(playerIn);
      if (!(this.func_85151_d() instanceof ChestTileEntity)) {
         this.chestOwner.func_174886_c(playerIn);
      }
   }
}
