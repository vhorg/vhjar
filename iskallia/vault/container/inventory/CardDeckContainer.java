package iskallia.vault.container.inventory;

import iskallia.vault.core.card.Card;
import iskallia.vault.core.card.CardDeck;
import iskallia.vault.core.card.CardEntry;
import iskallia.vault.core.card.CardPos;
import iskallia.vault.item.CardDeckItem;
import iskallia.vault.item.CardItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class CardDeckContainer extends SimpleContainer {
   private final ItemStack delegate;
   private final CardDeck deck;
   private final Map<Integer, CardPos> slotMapping;
   private final int deckHeight;
   private final int deckWidth;

   public CardDeckContainer(ItemStack delegate) {
      super(54);
      this.delegate = delegate;
      this.deck = CardDeckItem.getCardDeck(this.delegate).orElse(new CardDeck());
      this.slotMapping = new HashMap<>();

      for (CardPos pos : this.deck.getSlots()) {
         this.slotMapping.put(pos.x + pos.y * 9, pos);
      }

      if (this.delegate.getTag() != null && this.delegate.getTag().contains("inventory")) {
         ListTag inventoryNbt = this.delegate.getTag().getList("inventory", 10);

         for (int slotId = 0; slotId < inventoryNbt.size(); slotId++) {
            CompoundTag entry = inventoryNbt.getCompound(slotId);
            int slot = entry.getByte("Slot") & 255;
            if (slot < this.getContainerSize()) {
               this.setItem(slot, ItemStack.of(entry));
            }
         }
      }

      if (!this.deck.getCards().isEmpty()) {
         CardPos min = this.deck.getMinSlot();
         CardPos max = this.deck.getMaxSlot();
         this.deckWidth = max.x - min.x + 1;
         this.deckHeight = max.y - min.y + 1;
      } else {
         this.deckWidth = 0;
         this.deckHeight = 0;
      }
   }

   public Map<Integer, CardPos> getSlotMapping() {
      return this.slotMapping;
   }

   public int getDeckWidth() {
      return this.deckWidth;
   }

   public int getDeckHeight() {
      return this.deckHeight;
   }

   public void setChanged() {
      super.setChanged();
      ListTag inventoryNbt = new ListTag();

      for (int i = 0; i < this.getContainerSize(); i++) {
         ItemStack stack = this.getItem(i);
         if (!stack.isEmpty()) {
            CompoundTag entry = new CompoundTag();
            entry.putByte("Slot", (byte)i);
            stack.save(entry);
            inventoryNbt.add(entry);
         }
      }

      this.delegate.getOrCreateTag().put("inventory", inventoryNbt);

      for (int ix = 0; ix < this.getContainerSize(); ix++) {
         if (this.slotMapping.containsKey(ix)) {
            this.deck.setCard(this.slotMapping.get(ix), CardItem.getCard(this.getItem(ix)));
         }
      }

      CardDeckItem.setCardDeck(this.delegate, this.deck);
   }

   public List<CardDeckContainer.SlotColor> getMatchingNeighbors(int slotIndex) {
      CardPos pos = this.slotMapping.get(slotIndex);
      return this.deck.getCard(pos).map(card -> this.getMatchingNeighborSlots(pos, card)).orElse(Collections.emptyList());
   }

   private List<CardDeckContainer.SlotColor> getMatchingNeighborSlots(CardPos cardPos, Card card) {
      List<CardDeckContainer.SlotColor> matchingSlots = new ArrayList<>();

      for (CardEntry entry : card.getEntries()) {
         if (entry.getScaler() != null) {
            entry.getScaler().getMatchingNeighbors(card.getTier(), cardPos, this.deck).forEach(neighbor -> {
               int slotIndex = neighbor.x + neighbor.y * 9;
               matchingSlots.add(new CardDeckContainer.SlotColor(slotIndex, entry.getModifier().getHighlightColor() & 16777215 | 1996488704));
            });
         }
      }

      return matchingSlots;
   }

   public record SlotColor(int slotIndex, int color) {
   }
}
