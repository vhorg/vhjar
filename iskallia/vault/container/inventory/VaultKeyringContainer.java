package iskallia.vault.container.inventory;

import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.slot.ConditionalReadSlot;
import iskallia.vault.container.slot.TreasureKeySlot;
import iskallia.vault.init.ModContainers;
import iskallia.vault.item.ItemVaultKey;
import iskallia.vault.item.ItemVaultKeyring;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class VaultKeyringContainer extends OverSizedSlotContainer {
   private final Inventory inventory;
   private final int keyringSlot;
   private final List<Runnable> slotListeners = new ArrayList<>();
   private final List<TreasureKeySlot> keySlots = new ArrayList<>();

   public VaultKeyringContainer(int id, Inventory playerInventory, int slot) {
      super(ModContainers.VAULT_KEYRING_CONTAINER, id, playerInventory.player);
      this.inventory = playerInventory;
      this.keyringSlot = slot;
      if (!playerInventory.player.level.isClientSide) {
         this.slotListeners.add(this::updateTreasureKeys);
      }

      this.getKeyringStack().ifPresent(keyringStack -> playerInventory.player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
         this.initPlayerSlots(inv);
         this.initKeySlots(keyringStack);
      }));
   }

   public List<TreasureKeySlot> getKeySlots() {
      return this.keySlots;
   }

   private void initPlayerSlots(IItemHandler playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new ConditionalReadSlot(playerInventory, column + row * 9 + 9, 8 + column * 18, 82 + row * 18, this::canAccess));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new ConditionalReadSlot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 140, this::canAccess));
      }
   }

   private void initKeySlots(ItemStack keyringStack) {
      List<OverSizedItemStack> keyringStacks = ItemVaultKeyring.getStoredStacks(keyringStack);
      List<Item> keyItems = ForgeRegistries.ITEMS
         .getValues()
         .stream()
         .filter(item -> item instanceof ItemVaultKey)
         .map(item -> (Item)item)
         .filter(ItemVaultKey::isActive)
         .sorted(Comparator.comparing(ForgeRegistryEntry::getRegistryName))
         .collect(Collectors.toList());
      int slots = keyItems.size();
      OverSizedInventory buffer = new OverSizedInventory(slots, stacks -> {}, player -> true);

      for (int index = 0; index < slots; index++) {
         Item keyItem = keyItems.get(index);
         OverSizedItemStack keyStack = keyringStacks.stream().filter(stack -> stack.stack().is(keyItem)).findFirst().orElse(OverSizedItemStack.EMPTY);
         buffer.setOverSizedStack(index, keyStack);
         this.addKeySlot(buffer, new ItemStack(keyItem), index, index * 18, 0);
      }
   }

   private void addKeySlot(Container container, ItemStack keyType, int index, int x, int y) {
      TreasureKeySlot slot = new TreasureKeySlot(container, index, x, y, keyType);
      slot.addListener(() -> this.slotListeners.forEach(Runnable::run));
      this.keySlots.add(slot);
      this.addSlot(slot);
   }

   public boolean stillValid(@Nonnull Player player) {
      return this.hasKeyring();
   }

   public boolean canAccess(int slot, ItemStack slotStack) {
      return this.hasKeyring() && !(slotStack.getItem() instanceof ItemVaultKeyring);
   }

   public boolean hasKeyring() {
      ItemStack slotStack = this.inventory.getItem(this.keyringSlot);
      return !slotStack.isEmpty() && slotStack.getItem() instanceof ItemVaultKeyring;
   }

   public Optional<ItemStack> getKeyringStack() {
      return !this.hasKeyring() ? Optional.empty() : Optional.of(this.inventory.getItem(this.keyringSlot));
   }

   private void updateTreasureKeys() {
      this.getKeyringStack().ifPresent(keyring -> {
         List<OverSizedItemStack> stacks = new ArrayList<>();

         for (TreasureKeySlot slot : this.keySlots) {
            ItemStack stored = slot.getItem();
            stacks.add(OverSizedItemStack.of(stored));
         }

         ItemVaultKeyring.setStoredStacks(keyring, stacks);
      });
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot != null && slot.hasItem()) {
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

         slot.onTake(playerIn, slotStack);
      }

      return itemstack;
   }
}
