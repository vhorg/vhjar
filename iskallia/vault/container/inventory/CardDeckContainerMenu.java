package iskallia.vault.container.inventory;

import iskallia.vault.init.ModContainers;
import iskallia.vault.item.CardDeckItem;
import iskallia.vault.item.CardItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mekanism.common.integration.curios.CuriosIntegration;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CardDeckContainerMenu extends AbstractContainerMenu {
   private final List<CardDeckContainerMenu.DeckSlot> cardSlots = new ArrayList<>();
   private final List<Slot> playerSlots = new ArrayList<>();
   private final CardDeckContainer container;
   private final int cardSlot;
   private final boolean curios;
   private final int inventoryWidth;
   private final int inventoryHeight;
   private final int deckWidth;
   private final int deckHeight;
   private final int totalWidth;
   private final int totalHeight;

   public CardDeckContainerMenu(int id, Inventory playerInventory, int cardSlot, boolean curios, CardDeckContainer container) {
      super(ModContainers.CARD_DECK_CONTAINER, id);
      this.container = container;
      this.cardSlot = cardSlot;
      this.curios = curios;
      this.inventoryWidth = 176;
      this.inventoryHeight = 93;
      int cardBackgroundPaddingHeight = 20;
      int cardBackgroundPaddingWidth = 20;
      int cardBackgroundInventoryPadding = 2;
      this.deckWidth = 2 * cardBackgroundPaddingWidth + 18 * container.getDeckWidth();
      this.deckHeight = 2 * cardBackgroundPaddingHeight + 18 * container.getDeckHeight();
      this.totalWidth = Math.max(this.deckWidth, this.inventoryWidth);
      this.totalHeight = this.deckHeight + this.inventoryHeight + cardBackgroundInventoryPadding;
      this.addContainerSlots(container);
      this.addPlayerSlots(playerInventory);
   }

   private void addContainerSlots(CardDeckContainer deckCt) {
      int offsetX = (this.totalWidth - this.deckWidth) / 2 + 21;
      int offsetY = 21;

      for (int row = 0; row < 6; row++) {
         for (int column = 0; column < 9; column++) {
            int index = column + row * 9;
            CardDeckContainerMenu.DeckSlot slot = new CardDeckContainerMenu.DeckSlot(this.container, index, offsetX + column * 18, offsetY + row * 18);
            slot.setActive(deckCt.getSlotMapping().containsKey(index));
            this.addSlot(slot);
            this.cardSlots.add(slot);
         }
      }
   }

   private void addPlayerSlots(Inventory playerInventory) {
      int offsetX = (this.totalWidth - this.inventoryWidth) / 2 + 8;
      int offsetY = this.totalHeight - 83;
      int offsetHotbarY = this.totalHeight - 25;

      for (int j = 0; j < 3; j++) {
         for (int k = 0; k < 9; k++) {
            Slot slot = this.addSlot(new Slot(playerInventory, k + j * 9 + 9, offsetX + k * 18, offsetY + j * 18) {
               public boolean mayPickup(Player pPlayer) {
                  return CardDeckContainerMenu.this.curios || this.getSlotIndex() != CardDeckContainerMenu.this.cardSlot;
               }
            });
            this.playerSlots.add(slot);
         }
      }

      for (int j = 0; j < 9; j++) {
         Slot slot = this.addSlot(new Slot(playerInventory, j, offsetX + j * 18, offsetHotbarY) {
            public boolean mayPickup(Player pPlayer) {
               return CardDeckContainerMenu.this.curios || this.getSlotIndex() != CardDeckContainerMenu.this.cardSlot;
            }
         });
         this.playerSlots.add(slot);
      }
   }

   public List<Slot> getCardSlots() {
      return Collections.unmodifiableList(this.cardSlots);
   }

   public List<Slot> getPlayerSlots() {
      return Collections.unmodifiableList(this.playerSlots);
   }

   public boolean stillValid(Player player) {
      return !(this.getDeckStack(player).getItem() instanceof CardDeckItem) ? false : this.container.stillValid(player);
   }

   public ItemStack getDeckStack(Player player) {
      return this.curios ? CuriosIntegration.getCurioStack(player, "deck", 0) : player.getInventory().getItem(this.cardSlot);
   }

   public CardDeckContainer getContainer() {
      return this.container;
   }

   public int getInventoryWidth() {
      return this.inventoryWidth;
   }

   public int getInventoryHeight() {
      return this.inventoryHeight;
   }

   public int getTotalWidth() {
      return this.totalWidth;
   }

   public int getTotalHeight() {
      return this.totalHeight;
   }

   public int getDeckWidth() {
      return this.deckWidth;
   }

   public int getDeckHeight() {
      return this.deckHeight;
   }

   public ItemStack quickMoveStack(Player player, int pIndex) {
      ItemStack copy = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(pIndex);
      if (slot.hasItem()) {
         ItemStack stack = slot.getItem();
         copy = stack.copy();
         if (pIndex < 54) {
            if (!this.moveItemStackTo(stack, 54, this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(stack, 0, 54, false)) {
            return ItemStack.EMPTY;
         }

         if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }
      }

      return copy;
   }

   public record DeckContext(String inventoryName, int slotIndex) {
   }

   public static class DeckSlot extends Slot {
      private boolean isActive = true;

      public DeckSlot(Container container, int index, int x, int y) {
         super(container, index, x, y);
      }

      public void setActive(boolean active) {
         this.isActive = active;
      }

      public boolean isActive() {
         return this.isActive;
      }

      public boolean mayPlace(ItemStack stack) {
         return stack.getItem() instanceof CardItem && this.isActive();
      }
   }
}
