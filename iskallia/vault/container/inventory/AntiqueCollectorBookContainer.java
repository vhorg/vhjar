package iskallia.vault.container.inventory;

import iskallia.vault.antique.Antique;
import iskallia.vault.antique.AntiqueRegistry;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.slot.ChangeListenerSlot;
import iskallia.vault.container.slot.ConditionalReadSlot;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.AntiqueItem;
import iskallia.vault.item.AntiqueStampCollectorBook;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class AntiqueCollectorBookContainer extends OverSizedSlotContainer {
   private final Inventory inventory;
   private final int bookSlot;
   private int activePage = 0;
   private final List<Runnable> slotListeners = new ArrayList<>();
   private final List<AntiqueCollectorBookContainer.AntiqueCollectorBookSlot> antiqueSlots = new ArrayList<>();
   private final Map<Integer, List<AntiqueCollectorBookContainer.AntiqueCollectorBookSlot>> pageAntiqueSlots = new HashMap<>();

   public AntiqueCollectorBookContainer(int id, Inventory playerInventory, int bookSlot) {
      super(ModContainers.ANTIQUE_COLLECTOR_BOOK_CONTAINER, id, playerInventory.player);
      this.inventory = playerInventory;
      this.bookSlot = bookSlot;
      if (!playerInventory.player.level.isClientSide) {
         this.slotListeners.add(this::updateAntiques);
      }

      this.getBookStack().ifPresent(bookStack -> playerInventory.player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
         InvWrapper invWrapper = new InvWrapper(AntiqueStampCollectorBook.getAntiqueContainer(bookStack, this::stillValid));
         this.initPlayerSlots(inv);
         this.initAntiqueSlots(invWrapper);
      }));
      this.setActivePage(0);
   }

   private void initPlayerSlots(IItemHandler playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            this.addSlot(new ConditionalReadSlot(playerInventory, column + row * 9 + 9, 64 + column * 18, 163 + row * 18, this::canAccess));
         }
      }

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         this.addSlot(new ConditionalReadSlot(playerInventory, hotbarSlot, 64 + hotbarSlot * 18, 221, this::canAccess));
      }
   }

   private void initAntiqueSlots(IItemHandler antiqueInventory) {
      int xySlotStep = 36;
      List<Antique> sortedAntiques = AntiqueRegistry.sorted().toList();

      for (int i = 0; i < sortedAntiques.size(); i++) {
         Antique antique = sortedAntiques.get(i);
         int doublePage = i / 18;
         int doublePageSlot = i % 18;
         int pageSideSlot = doublePageSlot % 9;
         boolean leftPage = doublePageSlot < 9;
         int slotX = pageSideSlot % 3;
         int slotY = pageSideSlot / 3;
         int offsetX = leftPage ? 36 : 164;
         int offsetY = 25;
         AntiqueCollectorBookContainer.AntiqueCollectorBookSlot slot = new AntiqueCollectorBookContainer.AntiqueCollectorBookSlot(
            antiqueInventory, i, offsetX + slotX * xySlotStep, offsetY + slotY * xySlotStep, antique
         );
         slot.addListener(() -> this.slotListeners.forEach(Runnable::run));
         slot.setActive(false);
         this.antiqueSlots.add(slot);
         this.pageAntiqueSlots.computeIfAbsent(doublePage, integer -> new ArrayList<>()).add(slot);
         this.addSlot(slot);
      }
   }

   public int getActivePage() {
      return this.activePage;
   }

   public void setActivePage(int page) {
      this.activePage = page;
      this.antiqueSlots.forEach(slot -> slot.setActive(false));
      this.pageAntiqueSlots.getOrDefault(page, new ArrayList<>()).forEach(slot -> slot.setActive(true));
   }

   public int getPages() {
      return this.pageAntiqueSlots.size();
   }

   public List<AntiqueCollectorBookContainer.AntiqueCollectorBookSlot> getAntiqueSlots() {
      return this.antiqueSlots;
   }

   public boolean stillValid(@Nonnull Player player) {
      return this.hasBook();
   }

   public boolean canAccess(int slot, ItemStack slotStack) {
      return this.hasBook() && !(slotStack.getItem() instanceof AntiqueStampCollectorBook);
   }

   public boolean hasBook() {
      ItemStack book = this.inventory.getItem(this.bookSlot);
      return !book.isEmpty() && book.getItem() instanceof AntiqueStampCollectorBook;
   }

   public Optional<ItemStack> getBookStack() {
      return !this.hasBook() ? Optional.empty() : Optional.of(this.inventory.getItem(this.bookSlot));
   }

   public Supplier<AntiqueStampCollectorBook.StoredAntiques> getStoredAntiquesSupplier() {
      return () -> this.getBookStack().map(AntiqueStampCollectorBook::getStoredAntiques).orElse(new AntiqueStampCollectorBook.StoredAntiques());
   }

   private void updateAntiques() {
      this.getBookStack().ifPresent(book -> {
         AntiqueStampCollectorBook.StoredAntiques antiques = this.getStoredAntiquesSupplier().get();

         for (AntiqueCollectorBookContainer.AntiqueCollectorBookSlot slot : this.antiqueSlots) {
            Antique antique = slot.getAntique();
            ItemStack stack = slot.getItem();
            int count = stack.getCount();
            antiques.getInfo(antique).setCount(count);
         }

         AntiqueStampCollectorBook.setStoredAntiques(book, antiques);
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

   public static class AntiqueCollectorBookSlot extends ChangeListenerSlot {
      private final Antique antique;
      private boolean isActive = true;

      public AntiqueCollectorBookSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Antique antique) {
         super(
            itemHandler, index, xPosition, yPosition, stack -> !stack.isEmpty() && stack.is(ModItems.ANTIQUE) && antique.equals(AntiqueItem.getAntique(stack))
         );
         this.antique = antique;
      }

      public int getMaxStackSize() {
         return this.getItemHandler().getSlotLimit(0);
      }

      public int getMaxStackSize(@Nonnull ItemStack stack) {
         return this.getMaxStackSize();
      }

      public Antique getAntique() {
         return this.antique;
      }

      public void setActive(boolean active) {
         this.isActive = active;
      }

      public boolean isActive() {
         return this.isActive;
      }
   }
}
