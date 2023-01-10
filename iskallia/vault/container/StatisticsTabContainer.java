package iskallia.vault.container;

import iskallia.vault.container.slot.ArmorTabSlot;
import iskallia.vault.container.slot.CurioTabSlot;
import iskallia.vault.container.slot.OffhandTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.core.vault.stat.StatTotals;
import iskallia.vault.init.ModContainers;
import iskallia.vault.mixin.AccessorAbstractContainerMenu;
import iskallia.vault.network.message.ClientboundCuriosScrollMessage;
import iskallia.vault.network.message.ServerboundCuriosScrollMessage;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class StatisticsTabContainer extends NBTElementContainer<StatTotals> {
   private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private final StatisticsTabContainer.CurioContainerHandler curioContainerHandler;
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
      this.curioContainerHandler = new StatisticsTabContainer.CurioContainerHandler(
         StatisticsTabContainer.TabContainerAccessDecorator.of(this), nextSlotIndex, -20, 8, 8
      );
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

   public StatisticsTabContainer.CurioContainerHandler getCurioContainerHandler() {
      return this.curioContainerHandler;
   }

   public static class CurioContainerHandler {
      private final StatisticsTabContainer.IContainerAccess containerAccess;
      private final int slotStartIndex;
      private final int offsetX;
      private final int offsetY;
      private final int maxSlotsDisplayed;
      private int lastScrollIndex;

      private CurioContainerHandler(
         StatisticsTabContainer.IContainerAccess containerAccess, int slotStartIndex, int offsetX, int offsetY, int maxSlotsDisplayed
      ) {
         this.containerAccess = containerAccess;
         this.slotStartIndex = slotStartIndex;
         this.offsetX = offsetX;
         this.offsetY = offsetY;
         this.maxSlotsDisplayed = maxSlotsDisplayed;
      }

      public void scrollToIndex(int targetIndex) {
         NonNullList<Slot> slots = this.containerAccess.getSlots();
         slots.subList(this.slotStartIndex, slots.size()).clear();
         NonNullList<ItemStack> lastSlots = this.containerAccess.getLastSlots();
         lastSlots.subList(this.slotStartIndex, lastSlots.size()).clear();
         NonNullList<ItemStack> remoteSlots = this.containerAccess.getRemoteSlots();
         remoteSlots.subList(this.slotStartIndex, remoteSlots.size()).clear();
         this.getCuriosHandler().ifPresent(itemHandler -> {
            Map<String, ICurioStacksHandler> curioMap = itemHandler.getCurios();
            int startingIndex = this.calculateScrollStartIndex(targetIndex, curioMap);
            this.addSlots(curioMap, startingIndex, this.offsetY);
         });
         if (!this.containerAccess.isClient()) {
            ClientboundCuriosScrollMessage.send((ServerPlayer)this.containerAccess.getPlayer(), this.containerAccess.getContainerId(), targetIndex);
         }

         this.lastScrollIndex = targetIndex;
      }

      private int calculateScrollStartIndex(int targetIndex, Map<String, ICurioStacksHandler> curioMap) {
         int slotCount = 0;
         int index = 0;

         for (Entry<String, ICurioStacksHandler> entry : curioMap.entrySet()) {
            ICurioStacksHandler curioStacksHandler = entry.getValue();
            if (curioStacksHandler.isVisible()) {
               IDynamicStackHandler dynamicStackHandler = curioStacksHandler.getStacks();

               for (int i = 0; i < dynamicStackHandler.getSlots() && slotCount < this.maxSlotsDisplayed; i++) {
                  if (index >= targetIndex) {
                     slotCount++;
                  }

                  index++;
               }
            }
         }

         return Mth.clamp(index - this.maxSlotsDisplayed, 0, targetIndex);
      }

      private void addSlots(Map<String, ICurioStacksHandler> curioMap, int startingIndex, int offsetY) {
         int index = 0;
         int slotCount = 0;

         for (Entry<String, ICurioStacksHandler> entry : curioMap.entrySet()) {
            ICurioStacksHandler curioStacksHandler = entry.getValue();
            if (curioStacksHandler.isVisible()) {
               IDynamicStackHandler dynamicStackHandler = curioStacksHandler.getStacks();

               for (int slotIndex = 0; slotIndex < dynamicStackHandler.getSlots() && slotCount < this.maxSlotsDisplayed; slotIndex++) {
                  if (index >= startingIndex) {
                     this.containerAccess
                        .addSlot(
                           new CurioTabSlot(
                              this.containerAccess.getPlayer(),
                              dynamicStackHandler,
                              slotIndex,
                              entry.getKey(),
                              this.offsetX,
                              offsetY,
                              curioStacksHandler.getRenders()
                           )
                        );
                     slotCount++;
                     offsetY += 18;
                  }

                  index++;
               }
            }
         }
      }

      public void scrollTo(float pos) {
         this.getCuriosHandler().ifPresent(itemHandler -> {
            int newScrollIndex = (int)(pos * (itemHandler.getVisibleSlots() - this.maxSlotsDisplayed) + 0.5);
            if (newScrollIndex < 0) {
               newScrollIndex = 0;
            }

            if (newScrollIndex != this.lastScrollIndex) {
               if (this.containerAccess.isClient()) {
                  ServerboundCuriosScrollMessage.send(this.containerAccess.getContainerId(), newScrollIndex);
               }
            }
         });
      }

      public int getVisibleSlotCount() {
         return Math.min(this.getCuriosHandler().map(ICuriosItemHandler::getVisibleSlots).orElse(0), this.maxSlotsDisplayed);
      }

      public boolean canScroll() {
         return this.getCuriosHandler().map(itemHandler -> itemHandler.getVisibleSlots() > this.maxSlotsDisplayed).orElse(false);
      }

      private LazyOptional<ICuriosItemHandler> getCuriosHandler() {
         return CuriosApi.getCuriosHelper().getCuriosHandler(this.containerAccess.getPlayer());
      }
   }

   private interface IContainerAccess {
      int getContainerId();

      void addSlot(Slot var1);

      NonNullList<Slot> getSlots();

      NonNullList<ItemStack> getLastSlots();

      NonNullList<ItemStack> getRemoteSlots();

      Player getPlayer();

      boolean isClient();
   }

   private static class TabContainerAccessDecorator<T extends AbstractElementContainer> implements StatisticsTabContainer.IContainerAccess {
      private final T container;

      public static <T extends AbstractElementContainer> StatisticsTabContainer.TabContainerAccessDecorator<T> of(T container) {
         return new StatisticsTabContainer.TabContainerAccessDecorator<>(container);
      }

      private TabContainerAccessDecorator(T container) {
         this.container = container;
      }

      @Override
      public int getContainerId() {
         return this.container.containerId;
      }

      @Override
      public void addSlot(Slot slot) {
         this.container.addSlot(slot);
      }

      @Override
      public NonNullList<Slot> getSlots() {
         return this.container.slots;
      }

      @Override
      public NonNullList<ItemStack> getLastSlots() {
         return this.container.lastSlots;
      }

      @Override
      public NonNullList<ItemStack> getRemoteSlots() {
         return ((AccessorAbstractContainerMenu)this.container).getRemoteSlots();
      }

      @Override
      public Player getPlayer() {
         return this.container.getPlayer();
      }

      @Override
      public boolean isClient() {
         return this.getPlayer().level.isClientSide;
      }
   }
}
