package iskallia.vault.container;

import iskallia.vault.block.entity.VaultCrateTileEntity;
import iskallia.vault.init.ModContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class VaultCrateContainer extends AbstractContainerMenu {
   public IItemHandler crateInventory;
   private Player playerEntity;
   private IItemHandler playerInventory;
   private BlockPos tilePos;

   public VaultCrateContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
      super(ModContainers.VAULT_CRATE_CONTAINER, windowId);
      this.playerEntity = player;
      this.playerInventory = new InvWrapper(playerInventory);
      this.tilePos = pos;
      BlockEntity tileEntity = world.getBlockEntity(pos);
      if (tileEntity != null) {
         tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            this.crateInventory = h;
            int i = 36;

            for (int j = 0; j < 6; j++) {
               for (int k = 0; k < 9; k++) {
                  this.addSlot(new SlotItemHandler(h, k + j * 9, 8 + k * 18, 18 + j * 18));
               }
            }

            for (int l = 0; l < 3; l++) {
               for (int j1 = 0; j1 < 9; j1++) {
                  this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
               }
            }

            for (int i1 = 0; i1 < 9; i1++) {
               this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
            }
         });
      }
   }

   public BlockPos getTilePos() {
      return this.tilePos;
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      ItemStack stack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack stackInSlot = slot.getItem();
         stack = stackInSlot.copy();
         if (index < this.crateInventory.getSlots()) {
            if (!this.moveItemStackTo(stackInSlot, this.crateInventory.getSlots(), this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(stackInSlot, 0, this.crateInventory.getSlots(), false)) {
            return ItemStack.EMPTY;
         }

         if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }
      }

      return stack;
   }

   public boolean stillValid(Player player) {
      Level world = player.getCommandSenderWorld();
      return !(world.getBlockEntity(this.tilePos) instanceof VaultCrateTileEntity)
         ? false
         : player.distanceToSqr(this.tilePos.getX() + 0.5, this.tilePos.getY() + 0.5, this.tilePos.getZ() + 0.5) <= 64.0;
   }

   private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
      for (int j = 0; j < verAmount; j++) {
         index = this.addSlotRange(handler, index, x, y, horAmount, dx);
         y += dy;
      }

      return index;
   }

   private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
      for (int i = 0; i < amount; i++) {
         this.addSlot(new SlotItemHandler(handler, index, x, y));
         x += dx;
         index++;
      }

      return index;
   }

   public void removed(Player player) {
      super.removed(player);
      player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
   }
}
