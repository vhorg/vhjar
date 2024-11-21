package iskallia.vault.config.card;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.config.entry.IntRangeEntry;
import iskallia.vault.core.card.CardDeck;
import iskallia.vault.core.card.CardPos;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CardDeckConfig extends Config {
   @Expose
   private Map<String, CardDeckConfig.Entry> values;

   @Override
   public String getName() {
      return "card%sdecks".formatted(File.separator);
   }

   public Optional<String> getModel(String id) {
      return Optional.ofNullable(this.values.get(id)).map(entry -> entry.model);
   }

   public Optional<String> getName(String id) {
      return Optional.ofNullable(this.values.get(id)).map(entry -> entry.name);
   }

   public Optional<Integer> getEssence(String id) {
      return Optional.ofNullable(this.values.get(id)).filter(entry -> entry.essence != null).map(entry -> entry.essence.getRandom());
   }

   public Set<String> getModels() {
      return this.values.values().stream().map(pack -> pack.model).collect(Collectors.toSet());
   }

   public Set<String> getIds() {
      return this.values.keySet();
   }

   public boolean has(String id) {
      return this.values.containsKey(id);
   }

   public String getFirst() {
      return this.values.keySet().iterator().next();
   }

   public Optional<CardDeck> generate(String id, RandomSource random) {
      CardDeckConfig.Entry entry = this.values.get(id);
      if (entry == null) {
         return Optional.empty();
      } else {
         CardDeck deck = new CardDeck();
         String[] layout = entry.layout.getRandom(random).orElse(null);
         if (layout == null) {
            return Optional.empty();
         } else {
            for (int row = 0; row < layout.length; row++) {
               String line = layout[row];

               for (int column = 0; column < line.length(); column++) {
                  if (line.charAt(column) == 'O') {
                     deck.setCard(new CardPos(column, row), null);
                  }
               }
            }

            return Optional.of(deck);
         }
      }
   }

   @Override
   protected void reset() {
      this.values = new LinkedHashMap<>();
      this.values
         .put(
            "default",
            new CardDeckConfig.Entry(
               "the_vault:deck/starter_deck#inventory", "Deck Name", "OOOOOOOOO", "OOOOOOOOO", "OOOOOOOOO", "OOOOOOOOO", "OOOOOOOOO", "OOOOOOOOO"
            )
         );
   }

   public static class Entry {
      @Expose
      private String model;
      @Expose
      private String name;
      @Expose
      private IntRangeEntry essence;
      @Expose
      private WeightedList<String[]> layout;

      public Entry(String model, String name, String... layout) {
         this.model = model;
         this.name = name;
         this.essence = new IntRangeEntry(1, 1);
         this.layout = new WeightedList<>();
         this.layout.put(layout, 1.0);
      }
   }
}
