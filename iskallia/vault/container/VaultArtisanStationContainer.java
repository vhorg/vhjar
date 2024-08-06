package iskallia.vault.container;

import iskallia.vault.block.entity.VaultArtisanStationTileEntity;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationAction;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModGearModifications;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSlotIcons;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VaultArtisanStationContainer extends OverSizedSlotContainer {
   private final List<GearModificationAction> modificationActions = new ArrayList<>();
   private final VaultArtisanStationTileEntity tileEntity;
   private final BlockPos tilePos;

   public VaultArtisanStationContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.VAULT_ARTISAN_STATION_CONTAINER, windowId, playerInventory.player);
      this.tilePos = pos;
      if (world.getBlockEntity(this.tilePos) instanceof VaultArtisanStationTileEntity craftingStationTileEntity) {
         this.tileEntity = craftingStationTileEntity;
         this.initSlots(playerInventory);
      } else {
         this.tileEntity = null;
      }
   }

   public List<GearModificationAction> getModificationActions() {
      return Collections.unmodifiableList(this.modificationActions);
   }

   public Slot getPlatingSlot() {
      return (Slot)this.slots.get(36);
   }

   public Slot getBronzeSlot() {
      return (Slot)this.slots.get(37);
   }

   public Slot getGearInputSlot() {
      return (Slot)this.slots.get(this.slots.size() - 1);
   }

   private void initSlots(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new TabSlot(playerInventory, column + row * 9 + 9, 8 + column * 18, 148 + row * 18));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new TabSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 206));
      }

      Container invContainer = this.tileEntity.getInventory();
      this.addSlot(
         new OverSizedTabSlot(invContainer, 0, 69, 20)
            .setFilter(stack -> stack.is(ModItems.VAULT_PLATING))
            .setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.PLATING_NO_ITEM)
      );
      this.addSlot(
         new OverSizedTabSlot(invContainer, 1, 89, 20)
            .setFilter(stack -> stack.is(ModBlocks.VAULT_BRONZE))
            .setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM)
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 2, 8, 20),
         VaultArtisanStationContainer.Tab.COMMON,
         ModGearModifications.REFORGE_ALL_MODIFIERS,
         VaultArtisanStationContainer.ButtonSide.RIGHT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 3, 8, 44),
         VaultArtisanStationContainer.Tab.COMMON,
         ModGearModifications.ADD_MODIFIER,
         VaultArtisanStationContainer.ButtonSide.RIGHT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 4, 8, 68),
         VaultArtisanStationContainer.Tab.COMMON,
         ModGearModifications.REMOVE_MODIFIER,
         VaultArtisanStationContainer.ButtonSide.RIGHT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 10, 8, 92),
         VaultArtisanStationContainer.Tab.COMMON,
         ModGearModifications.REFORGE_PREFIXES,
         VaultArtisanStationContainer.ButtonSide.RIGHT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 8, 150, 20),
         VaultArtisanStationContainer.Tab.COMMON,
         ModGearModifications.REFORGE_ALL_IMPLICITS,
         VaultArtisanStationContainer.ButtonSide.LEFT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 9, 150, 44),
         VaultArtisanStationContainer.Tab.COMMON,
         ModGearModifications.REFORGE_RANDOM_TIER,
         VaultArtisanStationContainer.ButtonSide.LEFT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 5, 150, 68),
         VaultArtisanStationContainer.Tab.COMMON,
         ModGearModifications.REFORGE_ALL_ADD_TAG,
         VaultArtisanStationContainer.ButtonSide.LEFT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 11, 150, 92),
         VaultArtisanStationContainer.Tab.COMMON,
         ModGearModifications.REFORGE_SUFFIXES,
         VaultArtisanStationContainer.ButtonSide.LEFT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 12, 8, 20),
         VaultArtisanStationContainer.Tab.EXOTIC,
         ModGearModifications.IMPROVE_MODIFIER,
         VaultArtisanStationContainer.ButtonSide.RIGHT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 13, 8, 44),
         VaultArtisanStationContainer.Tab.EXOTIC,
         ModGearModifications.LOCK_MODIFIER,
         VaultArtisanStationContainer.ButtonSide.RIGHT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 14, 8, 68),
         VaultArtisanStationContainer.Tab.EXOTIC,
         ModGearModifications.IMPROVE_RARITY,
         VaultArtisanStationContainer.ButtonSide.RIGHT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 6, 150, 20),
         VaultArtisanStationContainer.Tab.EXOTIC,
         ModGearModifications.RESET_POTENTIAL,
         VaultArtisanStationContainer.ButtonSide.LEFT
      );
      this.addModSlot(
         new OverSizedTabSlot(invContainer, 7, 150, 44),
         VaultArtisanStationContainer.Tab.EXOTIC,
         ModGearModifications.REFORGE_REPAIR_SLOTS,
         VaultArtisanStationContainer.ButtonSide.LEFT
      );
      Container inputContainer = this.tileEntity.getGearInput();
      this.addSlot(
         new TabSlot(inputContainer, 0, 79, 72) {
            public boolean mayPlace(ItemStack stack) {
               return stack.getItem() instanceof VaultGearItem
                  && stack.getItem() != ModItems.JEWEL
                  && GearDataCache.of(stack).getRarity() != VaultGearRarity.UNIQUE;
            }
         }
      );
   }

   private void addModSlot(
      OverSizedTabSlot slot, VaultArtisanStationContainer.Tab tab, GearModification modification, VaultArtisanStationContainer.ButtonSide side
   ) {
      this.addSlot(slot);
      slot.setActive(false);
      slot.setFilter(modification.getStackFilter());
      this.modificationActions.add(new GearModificationAction(slot.index, tab, modification, side));
   }

   public GearModificationAction getModificationAction(GearModification modification) {
      for (GearModificationAction action : this.modificationActions) {
         if (action.modification().equals(modification)) {
            return action;
         }
      }

      return null;
   }

   @Nullable
   public GearModificationAction getModificationAction(int slot) {
      for (GearModificationAction action : this.modificationActions) {
         if (action.slotIndex() == slot) {
            return action;
         }
      }

      return null;
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveOverSizedItemStackTo(slotStack, slot, 36, this.slots.size(), false)) {
            return itemstack;
         }

         if (index >= 0 && index < 27) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 27, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 27 && index < 36) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 27, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 36, false)) {
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

   public BlockPos getTilePos() {
      return this.tilePos;
   }

   public VaultArtisanStationTileEntity getTileEntity() {
      return this.tileEntity;
   }

   public boolean stillValid(Player player) {
      return this.tileEntity == null ? false : this.tileEntity.stillValid(this.player);
   }

   public static enum ButtonSide {
      LEFT(-20),
      RIGHT(20);

      private final int xShift;

      private ButtonSide(int xShift) {
         this.xShift = xShift;
      }

      public int getXShift() {
         return this.xShift;
      }
   }

   public static enum Tab {
      COMMON,
      EXOTIC;

      private final MutableComponent name = new TranslatableComponent("the_vault.gear_modification.tab." + this.name().toLowerCase());

      public MutableComponent getName() {
         return this.name;
      }
   }
}
