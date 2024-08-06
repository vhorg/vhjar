package iskallia.vault.gear.data;

import iskallia.vault.core.card.CardDeck;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.CardDeckItem;
import java.util.UUID;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CardDeckGearData extends AttributeGearData {
   private final ItemStack delegate;
   private final CardDeck deck;

   protected CardDeckGearData(ItemStack delegate) {
      this.delegate = delegate;
      this.deck = CardDeckItem.getCardDeck(this.delegate).orElseGet(CardDeck::new);
      this.getAttributes().addAll(this.deck.getSnapshotAttributes());
   }

   protected CardDeckGearData(BitBuffer buf, ItemStack delegate) {
      this(delegate);
      this.read(buf);
   }

   @NotNull
   @Override
   public UUID getIdentifier() {
      return this.deck.getUuid();
   }

   @Override
   public void setIdentifier(UUID uuid) {
      this.deck.setUuid(uuid);
      CardDeckItem.setCardDeck(this.delegate, this.deck);
   }

   @Override
   protected void markChanged(ItemStack stack) {
      this.deck.setUuid(Mth.createInsecureUUID());
      CardDeckItem.setCardDeck(this.delegate, this.deck);
   }
}
