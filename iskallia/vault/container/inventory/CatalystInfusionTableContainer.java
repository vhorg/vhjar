package iskallia.vault.container.inventory;

import iskallia.vault.block.entity.CatalystInfusionTableTileEntity;
import iskallia.vault.container.slot.FilteredSlot;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModContainers;
import iskallia.vault.item.VaultCatalystItem;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CatalystInfusionTableContainer extends AbstractContainerMenu {
   private final CatalystInfusionTableTileEntity blockEntity;
   private final BlockPos tilePos;

   public CatalystInfusionTableContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.CATALYST_INFUSION_TABLE_CONTAINER, windowId);
      this.tilePos = pos;
      if (world.getBlockEntity(pos) instanceof CatalystInfusionTableTileEntity tileEntity) {
         this.initSlots(tileEntity.getCatalystStackHandler(), tileEntity.getInfuserStackHandler(), tileEntity.getOutputStackHandler(), playerInventory);
         this.blockEntity = tileEntity;
      } else {
         this.blockEntity = null;
      }
   }

   private void initSlots(IItemHandler catalystStackHandler, IItemHandler infuserItemHandler, IItemHandler outputItemHandler, Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 142));
      }

      Predicate<ItemStack> catalystFilter = stack -> stack.getItem() instanceof VaultCatalystItem;
      this.addSlot(new FilteredSlot(catalystStackHandler, 0, 56, 35, catalystFilter));
      Predicate<ItemStack> infuserFilter = stack -> ItemStack.isSameItemSameTags(stack, ModConfigs.CATALYST_INFUSION_TABLE.getInfusionItem());
      this.addSlot(new FilteredSlot(infuserItemHandler, 0, 34, 35, infuserFilter));
      this.addSlot(new CatalystInfusionTableContainer.OutputSlot(outputItemHandler, 0, 116, 35));
   }

   public boolean isActive() {
      return this.blockEntity != null && this.blockEntity.isActive();
   }

   public float getProgress() {
      return this.blockEntity == null ? 0.0F : this.blockEntity.getProgress();
   }

   public boolean stillValid(Player player) {
      Level world = player.getCommandSenderWorld();
      return !(world.getBlockEntity(this.tilePos) instanceof CatalystInfusionTableTileEntity)
         ? false
         : player.distanceToSqr(this.tilePos.getX() + 0.5, this.tilePos.getY() + 0.5, this.tilePos.getZ() + 0.5) <= 64.0;
   }

   @Nonnull
   public ItemStack quickMoveStack(@Nonnull Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveItemStackTo(slotStack, 36, 38, false)) {
            return itemstack;
         }

         if (index >= 0 && index < 27) {
            if (!this.moveItemStackTo(slotStack, 27, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 27 && index < 36) {
            if (!this.moveItemStackTo(slotStack, 0, 27, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(slotStack, 0, 36, false)) {
            return ItemStack.EMPTY;
         }

         if (slotStack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (slotStack.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, slotStack);
      }

      return itemstack;
   }

   private static class OutputSlot extends SlotItemHandler {
      public OutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
         super(itemHandler, index, xPosition, yPosition);
      }

      public boolean mayPlace(@NotNull ItemStack stack) {
         return false;
      }
   }
}
