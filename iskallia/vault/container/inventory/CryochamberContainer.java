package iskallia.vault.container.inventory;

import iskallia.vault.block.CryoChamberBlock;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.container.slot.ArmorSlot;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModContainers;
import iskallia.vault.world.data.EternalsData;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CryochamberContainer extends AbstractContainerMenu {
   private final BlockPos tilePos;

   public CryochamberContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.CRYOCHAMBER_CONTAINER, windowId);
      this.tilePos = pos;
      CryoChamberTileEntity cryoChamber = this.getCryoChamber(world);
      Container equipmentInventory;
      if (world instanceof ServerLevel && cryoChamber != null) {
         equipmentInventory = EternalsData.get((ServerLevel)world).getEternalEquipmentInventory(cryoChamber.getEternalId(), cryoChamber::sendUpdates);
         if (equipmentInventory == null) {
            return;
         }
      } else {
         equipmentInventory = new SimpleContainer(5);
      }

      this.initSlots(equipmentInventory, playerInventory);
   }

   private void initSlots(Container equipmentInventory, Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 129 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 187));
      }

      this.addSlot(new ArmorSlot(equipmentInventory, EquipmentSlot.MAINHAND, 0, 151, 26));
      int offsetY = 98;
      int index = 1;

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         if (slot.getType() != Type.HAND) {
            this.addSlot(new ArmorSlot(equipmentInventory, slot, index, 151, offsetY));
            offsetY -= 18;
            index++;
         }
      }
   }

   public boolean stillValid(Player player) {
      return this.getCryoChamber(player.getCommandSenderWorld()) == null ? false : player.distanceToSqr(Vec3.atCenterOf(this.tilePos)) <= 64.0;
   }

   @Nullable
   public CryoChamberTileEntity getCryoChamber(Level world) {
      BlockState state = world.getBlockState(this.tilePos);
      return !state.is(ModBlocks.CRYO_CHAMBER) ? null : CryoChamberBlock.getCryoChamberTileEntity(world, this.tilePos, state);
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveItemStackTo(slotStack, 36, 41, false)) {
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

         slot.onTake(playerIn, slotStack);
      }

      return itemstack;
   }

   public BlockPos getTilePos() {
      return this.tilePos;
   }
}
