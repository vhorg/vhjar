package iskallia.vault.gear.data;

import iskallia.vault.core.card.CardDeck;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.CardDeckItem;
import net.minecraft.world.item.ItemStack;

public class CardDeckGearData extends AttributeGearData {
   protected CardDeckGearData() {
   }

   protected CardDeckGearData(BitBuffer buf) {
      this.read(buf);
   }

   public static CardDeckGearData newDeck(ItemStack cardDeck) {
      CardDeckGearData data = new CardDeckGearData();
      CardDeckItem.getCardDeck(cardDeck).ifPresent(data::refresh);
      return data;
   }

   public void refresh(CardDeck deck) {
      this.attributes.clear();
      this.attributes.addAll(deck.getSnapshotAttributes());
   }
}
