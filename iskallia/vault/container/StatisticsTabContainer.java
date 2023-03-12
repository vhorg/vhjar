package iskallia.vault.container;

import iskallia.vault.container.slot.ArmorTabSlot;
import iskallia.vault.container.slot.OffhandTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.core.vault.stat.StatTotals;
import iskallia.vault.init.ModContainers;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public class StatisticsTabContainer extends NBTElementContainer<StatTotals> {
   private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private final CurioContainerHandler curioContainerHandler;
   private final AbstractElementContainer.SlotIndexRange hotbarSlotIndexRange;
   private final AbstractElementContainer.SlotIndexRange inventorySlotIndexRange;
   private final AbstractElementContainer.SlotIndexRange hotbarInventorySlotIndexRange;
   private final AbstractElementContainer.SlotIndexRange armorSlotIndexRange;
   private final AbstractElementContainer.SlotIndexRange offhandSlotIndexRange;
   private final AbstractElementContainer.SlotIndexRange curioSlotIndexRange;

   public StatisticsTabContainer(int id, Inventory playerInventory, StatTotals statTotals) {
      super(() -> ModContainers.STATISTICS_TAB_CONTAINER, id, playerInventory.player, statTotals);
      int offsetX = 0;
      int offsetY = 18;
      int nextSlotIndex = 0;

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + (row + 1) * 9, 8 + column * 18 + offsetX, 84 + row * 18 + offsetY));
            nextSlotIndex++;
         }
      }

      this.inventorySlotIndexRange = new AbstractElementContainer.SlotIndexRange(0, nextSlotIndex);

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18 + offsetX, 142 + offsetY));
         nextSlotIndex++;
      }

      this.hotbarSlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.inventorySlotIndexRange.end(), nextSlotIndex);
      this.hotbarInventorySlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.inventorySlotIndexRange.start(), this.hotbarSlotIndexRange.end());

      for (int armorSlot = 0; armorSlot < 4; armorSlot++) {
         this.addSlot(
            new ArmorTabSlot(playerInventory, 39 - armorSlot, 8 + offsetX, 8 + armorSlot * 18 + offsetY - 18, EQUIPMENT_SLOTS[armorSlot], this.player)
         );
         nextSlotIndex++;
      }

      this.armorSlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.hotbarSlotIndexRange.end(), nextSlotIndex);
      this.addSlot(new OffhandTabSlot(playerInventory, 40, 8 + offsetX, 62 + offsetY));
      this.offhandSlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.armorSlotIndexRange.end(), ++nextSlotIndex);
      this.curioContainerHandler = new CurioContainerHandler(CurioContainerHandler.TabContainerAccessDecorator.of(this), nextSlotIndex, -20, 8, 8);
      this.curioContainerHandler.scrollToIndex(0);
      this.curioSlotIndexRange = new AbstractElementContainer.SlotIndexRange(
         this.offhandSlotIndexRange.end(), this.offhandSlotIndexRange.end() + this.curioContainerHandler.getVisibleSlotCount()
      );
   }

   @Override
   public boolean stillValid(@Nonnull Player player) {
      return true;
   }

   @Nonnull
   public ItemStack quickMoveStack(@Nonnull Player player, int index) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack slotItemStack = slot.getItem();
         itemStack = slotItemStack.copy();
         EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(slotItemStack);
         if (this.armorSlotIndexRange.contains(index)) {
            if (!this.moveItemStackTo(slotItemStack, this.hotbarInventorySlotIndexRange, false)) {
               return ItemStack.EMPTY;
            }
         } else if (equipmentSlot.getType() == Type.ARMOR && !((Slot)this.slots.get(this.armorSlotIndexRange.end() - equipmentSlot.getIndex() - 1)).hasItem()) {
            int i = this.armorSlotIndexRange.end() - equipmentSlot.getIndex() - 1;
            this.moveItemStackTo(slotItemStack, i, i + 1, false);
         } else if (index < this.curioSlotIndexRange.start() && !CuriosApi.getCuriosHelper().getCurioTags(slotItemStack.getItem()).isEmpty()) {
            this.moveItemStackTo(slotItemStack, this.curioSlotIndexRange, false);
         } else if (equipmentSlot == EquipmentSlot.OFFHAND && !((Slot)this.slots.get(this.offhandSlotIndexRange.start())).hasItem()) {
            this.moveItemStackTo(slotItemStack, this.offhandSlotIndexRange, false);
         }

         if (this.inventorySlotIndexRange.contains(index)) {
            if (!this.moveItemStackTo(slotItemStack, this.hotbarSlotIndexRange, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.hotbarSlotIndexRange.contains(index)) {
            if (!this.moveItemStackTo(slotItemStack, this.inventorySlotIndexRange, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(slotItemStack, this.hotbarInventorySlotIndexRange, false)) {
            return ItemStack.EMPTY;
         }
      }

      return itemStack;
   }

   public CurioContainerHandler getCurioContainerHandler() {
      return this.curioContainerHandler;
   }
}
