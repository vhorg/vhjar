package iskallia.vault.container;

import com.mojang.blaze3d.platform.InputConstants;
import iskallia.vault.block.entity.WardrobeTileEntity;
import iskallia.vault.container.slot.ArmorTabSlot;
import iskallia.vault.container.slot.CurioTabSlot;
import iskallia.vault.container.slot.OffhandTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ServerboundWardrobeSwapMessage;
import iskallia.vault.network.message.ServerboundWardrobeToggleSolidRenderMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public abstract class WardrobeContainer extends AbstractElementContainer {
   private static final int PLAYER_INVENTORY_TOP_Y = 122;
   protected final WardrobeTileEntity tileEntity;
   protected AbstractElementContainer.SlotIndexRange hotbarSlotIndexRange;
   protected AbstractElementContainer.SlotIndexRange inventorySlotIndexRange;
   protected AbstractElementContainer.SlotIndexRange hotbarInventorySlotIndexRange;
   private final BlockPos pos;
   private Runnable slotChangeListener = () -> {};

   public WardrobeContainer(MenuType<?> menuType, int id, Inventory playerInventory, BlockPos pos) {
      super(menuType, id, playerInventory.player);
      this.pos = pos;
      if (this.player.level.getBlockEntity(pos) instanceof WardrobeTileEntity spiritExtractorTile) {
         this.tileEntity = spiritExtractorTile;
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   public void setSlotChangeListener(Runnable slotChangeListener) {
      this.slotChangeListener = slotChangeListener;
   }

   public boolean stillValid(Player player) {
      return player.distanceToSqr(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   protected int initSlots(Inventory playerInventory) {
      int nextSlotIndex = 0;

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, 8 + column * 18, 122 + row * 18));
            nextSlotIndex++;
         }
      }

      this.inventorySlotIndexRange = new AbstractElementContainer.SlotIndexRange(0, nextSlotIndex);

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 180));
         nextSlotIndex++;
      }

      this.hotbarSlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.inventorySlotIndexRange.end(), nextSlotIndex);
      this.hotbarInventorySlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.inventorySlotIndexRange.start(), this.hotbarSlotIndexRange.end());
      return nextSlotIndex;
   }

   public ItemStack getStoredEquipmentBySlot(EquipmentSlot equipmentSlot) {
      return this.tileEntity.getEquipment(equipmentSlot);
   }

   public Map<String, List<Tuple<ItemStack, Integer>>> getStoredCurios() {
      Map<String, List<Tuple<ItemStack, Integer>>> ret = new HashMap<>();
      this.tileEntity
         .getCuriosItems()
         .forEach((slotKey, stacks) -> stacks.forEach((slot, stack) -> ret.computeIfAbsent(slotKey, sk -> new ArrayList<>()).add(new Tuple(stack, slot))));
      return ret;
   }

   public ItemStackHandler getHotbarItems() {
      return this.tileEntity.getHotbarItems();
   }

   public boolean isOwner() {
      return this.tileEntity.isOwner(this.player);
   }

   public void swap() {
      if (this.player.level.isClientSide()) {
         ModNetwork.CHANNEL.sendToServer(new ServerboundWardrobeSwapMessage(this.pos, this.isHoldingShift()));
      }
   }

   public boolean isHoldingShift() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340)
         || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
   }

   public ItemStack getStoredCurio(String slotKey, int slot) {
      return this.tileEntity.getCurio(slotKey, slot);
   }

   public void setItem(int pSlotId, int pStateId, ItemStack pStack) {
      super.setItem(pSlotId, pStateId, pStack);
      this.slotChangeListener.run();
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public static class Gear extends WardrobeContainer {
      private CurioContainerHandler curioContainerHandler;
      protected AbstractElementContainer.SlotIndexRange curioSlotIndexRange;
      protected AbstractElementContainer.SlotIndexRange armorSlotIndexRange;
      protected AbstractElementContainer.SlotIndexRange offhandSlotIndexRange;
      private CurioContainerHandler storedCurioContainerHandler;
      private AbstractElementContainer.SlotIndexRange storedCurioSlotIndexRange;
      private AbstractElementContainer.SlotIndexRange storedArmorSlotIndexRange;
      private AbstractElementContainer.SlotIndexRange storedOffhandSlotIndexRange;

      public Gear(int id, Inventory playerInventory, BlockPos pos) {
         super(ModContainers.WARDROBE_GEAR_CONTAINER, id, playerInventory, pos);
      }

      @Override
      protected int initSlots(Inventory playerInventory) {
         int nextSlotIndex = super.initSlots(playerInventory);
         int slotIdx = 36;

         for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.getType() == Type.ARMOR) {
               this.addSlot(new ArmorTabSlot(playerInventory, slotIdx++, 8, 72 - equipmentSlot.getIndex() * 18, equipmentSlot, this.player));
               nextSlotIndex++;
            }
         }

         this.armorSlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.hotbarSlotIndexRange.end(), nextSlotIndex);
         this.addSlot(new OffhandTabSlot(playerInventory, 40, 8, 90));
         this.offhandSlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.armorSlotIndexRange.end(), ++nextSlotIndex);
         this.curioContainerHandler = new CurioContainerHandler(CurioContainerHandler.TabContainerAccessDecorator.of(this), nextSlotIndex, -20, 18, 8);
         this.curioContainerHandler.scrollToIndex(0);
         this.curioSlotIndexRange = new AbstractElementContainer.SlotIndexRange(
            this.offhandSlotIndexRange.end(), this.offhandSlotIndexRange.end() + this.curioContainerHandler.getVisibleSlotCount()
         );
         nextSlotIndex += this.curioContainerHandler.getVisibleSlotCount();
         slotIdx = 0;

         for (EquipmentSlot equipmentSlotx : EquipmentSlot.values()) {
            if (equipmentSlotx.getType() == Type.ARMOR) {
               this.addSlot(
                  new ArmorTabSlot(
                     new WardrobeContainer.WardrobeEquipmentContainer(this.tileEntity, equipmentSlotx),
                     slotIdx++,
                     152,
                     72 - equipmentSlotx.getIndex() * 18,
                     equipmentSlotx,
                     this.player
                  ) {
                     @Override
                     public boolean mayPlace(ItemStack itemStack) {
                        return Gear.this.isOwner() && super.mayPlace(itemStack);
                     }

                     @Override
                     public boolean mayPickup(@NotNull Player player) {
                        return Gear.this.isOwner() && super.mayPickup(player);
                     }
                  }
               );
               nextSlotIndex++;
            }
         }

         this.storedArmorSlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.curioSlotIndexRange.end(), nextSlotIndex);
         this.addSlot(new OffhandTabSlot(new WardrobeContainer.WardrobeEquipmentContainer(this.tileEntity, EquipmentSlot.OFFHAND), 40, 152, 90) {
            public boolean mayPlace(ItemStack pStack) {
               return Gear.this.isOwner() && super.mayPlace(pStack);
            }

            public boolean mayPickup(Player pPlayer) {
               return Gear.this.isOwner() && super.mayPickup(pPlayer);
            }
         });
         this.storedOffhandSlotIndexRange = new AbstractElementContainer.SlotIndexRange(this.storedArmorSlotIndexRange.end(), ++nextSlotIndex);
         this.storedCurioContainerHandler = new WardrobeContainer.StoredCurioContainerHandler(
            this.player, this.tileEntity, CurioContainerHandler.TabContainerAccessDecorator.of(this), nextSlotIndex, 180, 18, 8
         );
         this.storedCurioContainerHandler.scrollToIndex(0);
         this.storedCurioSlotIndexRange = new AbstractElementContainer.SlotIndexRange(
            this.storedOffhandSlotIndexRange.end(), this.storedOffhandSlotIndexRange.end() + this.storedCurioContainerHandler.getVisibleSlotCount()
         );
         return nextSlotIndex;
      }

      public CurioContainerHandler getCurioContainerHandler() {
         return this.curioContainerHandler;
      }

      public Set<ItemStack> getPlayerEquipment() {
         Set<ItemStack> ret = new HashSet<>();

         for (int i = this.armorSlotIndexRange.start(); i < this.armorSlotIndexRange.end(); i++) {
            ret.add(this.getSlot(i).getItem());
         }

         ret.add(this.getSlot(this.armorSlotIndexRange.start()).getItem());
         return ret;
      }

      public Collection<ItemStack> getStoredEquipment() {
         Set<ItemStack> ret = new HashSet<>();

         for (int i = this.storedArmorSlotIndexRange.start(); i < this.storedArmorSlotIndexRange.end(); i++) {
            ret.add(this.getSlot(i).getItem());
         }

         ret.add(this.getSlot(this.storedOffhandSlotIndexRange.start()).getItem());
         return ret;
      }

      public ItemStack quickMoveStack(Player player, int index) {
         ItemStack originalStack = ItemStack.EMPTY;
         Slot slot = (Slot)this.slots.get(index);
         if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            originalStack = slotStack.copy();
            boolean didNotMoveAnything;
            if (this.curioSlotIndexRange.contains(index)) {
               didNotMoveAnything = !this.moveItemStackTo(slotStack, this.storedCurioSlotIndexRange, false)
                  && !this.moveItemStackTo(slotStack, this.hotbarInventorySlotIndexRange, false);
            } else if (this.armorSlotIndexRange.contains(index)) {
               didNotMoveAnything = !this.moveItemStackTo(slotStack, this.storedArmorSlotIndexRange, false)
                  && !this.moveItemStackTo(slotStack, this.hotbarInventorySlotIndexRange, false);
            } else if (this.offhandSlotIndexRange.contains(index)) {
               didNotMoveAnything = !this.moveItemStackTo(slotStack, this.storedOffhandSlotIndexRange, false)
                  && !this.moveItemStackTo(slotStack, this.hotbarInventorySlotIndexRange, false);
            } else if (this.storedCurioSlotIndexRange.contains(index)) {
               didNotMoveAnything = !this.moveItemStackTo(slotStack, this.curioSlotIndexRange, false)
                  && !this.moveItemStackTo(slotStack, this.hotbarInventorySlotIndexRange, false);
            } else if (this.storedArmorSlotIndexRange.contains(index)) {
               didNotMoveAnything = !this.moveItemStackTo(slotStack, this.armorSlotIndexRange, false)
                  && !this.moveItemStackTo(slotStack, this.hotbarInventorySlotIndexRange, false);
            } else if (this.storedOffhandSlotIndexRange.contains(index)) {
               didNotMoveAnything = !this.moveItemStackTo(slotStack, this.offhandSlotIndexRange, false)
                  && !this.moveItemStackTo(slotStack, this.hotbarInventorySlotIndexRange, false);
            } else {
               didNotMoveAnything = this.hotbarInventorySlotIndexRange.contains(index)
                  && !this.moveItemStackTo(slotStack, this.storedCurioSlotIndexRange, false)
                  && !this.moveItemStackTo(slotStack, this.storedArmorSlotIndexRange, false)
                  && !this.moveItemStackTo(slotStack, this.curioSlotIndexRange, false)
                  && !this.moveItemStackTo(slotStack, this.armorSlotIndexRange, false)
                  && !this.moveItemStackTo(slotStack, this.storedOffhandSlotIndexRange, false)
                  && !this.moveItemStackTo(slotStack, this.offhandSlotIndexRange, false);
            }

            if (didNotMoveAnything) {
               return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
               slot.set(ItemStack.EMPTY);
            } else {
               slot.setChanged();
            }

            if (slotStack.getCount() == originalStack.getCount()) {
               return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
         }

         return originalStack;
      }

      public boolean shouldRenderSolid() {
         return this.tileEntity.shouldRenderSolid();
      }

      public void toggleSolidRender() {
         ModNetwork.CHANNEL.sendToServer(new ServerboundWardrobeToggleSolidRenderMessage(this.tileEntity.getBlockPos()));
      }

      public Player getDummyRenderPlayer() {
         return this.tileEntity.getDummyRenderPlayer();
      }

      public BlockPos getBlockPos() {
         return this.tileEntity.getBlockPos();
      }
   }

   public static class Hotbar extends WardrobeContainer {
      private AbstractElementContainer.SlotIndexRange storedHotbarSlotIndexRange;

      public Hotbar(int id, Inventory playerInventory, BlockPos pos) {
         super(ModContainers.WARDROBE_HOTBAR_CONTAINER, id, playerInventory, pos);
      }

      @Override
      protected int initSlots(Inventory playerInventory) {
         int nextSlotIndex = super.initSlots(playerInventory);
         ItemStackHandler hotbarItems = this.tileEntity.getHotbarItems();

         for (int slot = 0; slot < hotbarItems.getSlots(); slot++) {
            this.addSlot(new SlotItemHandler(hotbarItems, slot, 8 + 18 * slot, 90));
         }

         this.storedHotbarSlotIndexRange = new AbstractElementContainer.SlotIndexRange(
            this.hotbarInventorySlotIndexRange.end(), this.hotbarInventorySlotIndexRange.end() + hotbarItems.getSlots()
         );
         return nextSlotIndex;
      }

      public ItemStack quickMoveStack(Player player, int index) {
         ItemStack originalStack = ItemStack.EMPTY;
         Slot slot = (Slot)this.slots.get(index);
         if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            originalStack = slotStack.copy();
            boolean didNotMoveStack = false;
            if (this.storedHotbarSlotIndexRange.contains(index)) {
               didNotMoveStack = !this.moveItemStackTo(slotStack, this.hotbarInventorySlotIndexRange, false);
            } else if (this.hotbarInventorySlotIndexRange.contains(index)) {
               didNotMoveStack = !this.moveItemStackTo(slotStack, this.storedHotbarSlotIndexRange, false);
            }

            if (didNotMoveStack) {
               return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
               slot.set(ItemStack.EMPTY);
            } else {
               slot.setChanged();
            }
         }

         return originalStack;
      }

      @Override
      protected boolean moveItemStackTo(@NotNull ItemStack itemStack, AbstractElementContainer.SlotIndexRange slotIndexRange, boolean pReverseDirection) {
         return super.moveItemStackTo(itemStack, slotIndexRange, pReverseDirection);
      }
   }

   private static class StoredCurioContainerHandler extends CurioContainerHandler {
      private Player player;
      private final WardrobeTileEntity wardrobeTile;

      public StoredCurioContainerHandler(
         Player player,
         WardrobeTileEntity wardrobeTile,
         CurioContainerHandler.IContainerAccess containerAccess,
         int slotStartIndex,
         int offsetX,
         int offsetY,
         int maxSlotsDisplayed
      ) {
         super(containerAccess, slotStartIndex, offsetX, offsetY, maxSlotsDisplayed);
         this.player = player;
         this.wardrobeTile = wardrobeTile;
      }

      @Override
      protected void addSlots(Map<String, ICurioStacksHandler> curioMap, int startingIndex, int offsetY) {
         int index = 0;
         int slotCount = 0;

         for (Entry<String, ICurioStacksHandler> entry : curioMap.entrySet()) {
            ICurioStacksHandler curioStacksHandler = entry.getValue();
            if (curioStacksHandler.isVisible()) {
               IDynamicStackHandler dynamicStackHandler = curioStacksHandler.getStacks();
               WardrobeTileEntity.CuriosDynamicStackHandler storedCurioDynamicStackHandler = new WardrobeTileEntity.CuriosDynamicStackHandler(
                  this.wardrobeTile, entry.getKey(), dynamicStackHandler.getSlots()
               );

               for (int slotIndex = 0; slotIndex < dynamicStackHandler.getSlots() && slotCount < this.maxSlotsDisplayed; slotIndex++) {
                  if (index >= startingIndex) {
                     this.containerAccess
                        .addSlot(
                           new CurioTabSlot(
                              this.containerAccess.getPlayer(),
                              storedCurioDynamicStackHandler,
                              slotIndex,
                              entry.getKey(),
                              this.offsetX,
                              offsetY,
                              curioStacksHandler.getRenders()
                           ) {
                              public boolean mayPlace(@NotNull ItemStack stack) {
                                 return StoredCurioContainerHandler.this.wardrobeTile.isOwner(StoredCurioContainerHandler.this.player) && super.mayPlace(stack);
                              }

                              public boolean mayPickup(Player playerIn) {
                                 return StoredCurioContainerHandler.this.wardrobeTile.isOwner(StoredCurioContainerHandler.this.player)
                                    && super.mayPickup(playerIn);
                              }
                           }
                        );
                     slotCount++;
                     offsetY += 18;
                  }

                  index++;
               }
            }
         }
      }
   }

   private static class WardrobeEquipmentContainer implements Container {
      private final WardrobeTileEntity wardrobe;
      private final EquipmentSlot equipmentSlot;

      public WardrobeEquipmentContainer(WardrobeTileEntity wardrobe, EquipmentSlot equipmentSlot) {
         this.wardrobe = wardrobe;
         this.equipmentSlot = equipmentSlot;
      }

      public int getContainerSize() {
         return 1;
      }

      public boolean isEmpty() {
         return this.getItem().isEmpty();
      }

      public ItemStack getItem(int pIndex) {
         return this.getItem();
      }

      private ItemStack getItem() {
         return this.wardrobe.getEquipment(this.equipmentSlot);
      }

      public ItemStack removeItem(int pIndex, int pCount) {
         return pCount < 1 ? ItemStack.EMPTY : this.removeItemNoUpdate(pIndex);
      }

      public ItemStack removeItemNoUpdate(int pIndex) {
         ItemStack result = this.getItem();
         if (!result.isEmpty()) {
            this.wardrobe.setEquipmentSlot(this.equipmentSlot, ItemStack.EMPTY);
         }

         return result;
      }

      public void setItem(int pIndex, ItemStack pStack) {
         this.wardrobe.setEquipmentSlot(this.equipmentSlot, pStack);
      }

      public void setChanged() {
         this.wardrobe.setChanged();
      }

      public boolean stillValid(Player pPlayer) {
         return true;
      }

      public void clearContent() {
         this.wardrobe.setEquipmentSlot(this.equipmentSlot, ItemStack.EMPTY);
      }

      public int getMaxStackSize() {
         return 1;
      }
   }
}
