package iskallia.vault.core.card;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.network.chat.TextComponent;

public enum CardNeighborType {
   ROW("Row", (origin, deck) -> {
      Set<CardPos> neighbors = new HashSet<>();
      int min = deck.getMinSlot().x;
      int max = deck.getMaxSlot().x;

      for (int x = min; x <= max; x++) {
         CardPos pos = new CardPos(x, origin.y);
         if (deck.getSlots().contains(pos)) {
            neighbors.add(pos);
         }
      }

      return neighbors;
   }),
   COLUMN("Column", (origin, deck) -> {
      Set<CardPos> neighbors = new HashSet<>();
      int min = deck.getMinSlot().y;
      int max = deck.getMaxSlot().y;

      for (int y = min; y <= max; y++) {
         CardPos pos = new CardPos(origin.x, y);
         if (deck.getSlots().contains(pos)) {
            neighbors.add(pos);
         }
      }

      return neighbors;
   }),
   DIAGONAL("Diagonal", (origin, deck) -> {
      Set<CardPos> neighbors = new HashSet<>();
      int i = 0;

      while (true) {
         List<CardPos> offsets = Arrays.asList(origin.add(i, i), origin.add(-i, i), origin.add(i, -i), origin.add(-i, -i));
         int done = 0;

         for (CardPos offset : offsets) {
            if (offset.x >= deck.getMinSlot().x && offset.x <= deck.getMaxSlot().x && offset.y >= deck.getMinSlot().y && offset.y <= deck.getMaxSlot().y) {
               neighbors.add(offset);
            } else {
               done++;
            }
         }

         if (done == 4) {
            return neighbors;
         }

         i++;
      }
   }),
   ADJACENT("Adjacent", (origin, deck) -> {
      Set<CardPos> neighbors = new HashSet<>();
      if (deck.getSlots().contains(origin.add(1, 0))) {
         neighbors.add(origin.add(1, 0));
      }

      if (deck.getSlots().contains(origin.add(-1, 0))) {
         neighbors.add(origin.add(-1, 0));
      }

      if (deck.getSlots().contains(origin.add(0, 1))) {
         neighbors.add(origin.add(0, 1));
      }

      if (deck.getSlots().contains(origin.add(0, -1))) {
         neighbors.add(origin.add(0, -1));
      }

      return neighbors;
   }),
   SURROUNDING("Surrounding", (origin, deck) -> {
      Set<CardPos> neighbors = new HashSet<>(ADJACENT.get(origin, deck));
      if (deck.getSlots().contains(origin.add(1, 1))) {
         neighbors.add(origin.add(1, 1));
      }

      if (deck.getSlots().contains(origin.add(-1, 1))) {
         neighbors.add(origin.add(-1, 1));
      }

      if (deck.getSlots().contains(origin.add(1, -1))) {
         neighbors.add(origin.add(1, -1));
      }

      if (deck.getSlots().contains(origin.add(-1, -1))) {
         neighbors.add(origin.add(-1, -1));
      }

      return neighbors;
   });

   private String name;
   private BiFunction<CardPos, CardDeck, Set<CardPos>> supplier;

   private CardNeighborType(String name, BiFunction<CardPos, CardDeck, Set<CardPos>> supplier) {
      this.name = name;
      this.supplier = supplier;
   }

   Set<CardPos> get(CardPos origin, CardDeck deck) {
      return this.supplier.apply(origin, deck);
   }

   public TextComponent getText() {
      return new TextComponent(this.name);
   }
}
