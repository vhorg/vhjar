package iskallia.vault.config.card;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.core.card.CardEntry;
import iskallia.vault.core.card.CardNeighborType;
import iskallia.vault.core.card.CardScaler;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CardScalersConfig extends Config {
   @Expose
   private Map<String, CardScaler> values;
   @Expose
   private Map<String, WeightedList<String>> pools;

   @Override
   public String getName() {
      return "card%sscalers".formatted(File.separator);
   }

   public Optional<CardScaler> getRandom(String id, RandomSource random) {
      if (id.startsWith("@")) {
         WeightedList<String> pool = this.pools.get(id.substring(1));
         return pool == null ? Optional.empty() : pool.getRandom(random).flatMap(s -> this.getRandom(s, random));
      } else {
         return Optional.ofNullable(this.values.get(id)).map(CardScaler::copy);
      }
   }

   @Override
   protected void reset() {
      this.values = new LinkedHashMap<>();
      this.pools = new LinkedHashMap<>();
      Map<Integer, WeightedList<List<CardScaler.Filter.Config>>> tiers = new LinkedHashMap<>();
      tiers.put(
         1,
         new WeightedList<List<CardScaler.Filter.Config>>()
            .add(
               Arrays.asList(
                  new CardScaler.Filter.Config(
                     new WeightedList<Set<CardNeighborType>>()
                        .add(null, 3.0)
                        .add(Set.of(CardNeighborType.ROW), 1.0)
                        .add(Set.of(CardNeighborType.COLUMN), 1.0)
                        .add(Set.of(CardNeighborType.DIAGONAL), 1.0)
                        .add(Set.of(CardNeighborType.ADJACENT), 1.0)
                        .add(Set.of(CardNeighborType.SURROUNDING), 1.0),
                     new WeightedList<Set<Integer>>().add(null, 1.0).add(Set.of(2), 1.0),
                     new WeightedList<Set<CardEntry.Color>>()
                        .add(null, 2.0)
                        .add(Set.of(CardEntry.Color.GREEN), 1.0)
                        .add(Set.of(CardEntry.Color.BLUE), 1.0)
                        .add(Set.of(CardEntry.Color.YELLOW), 1.0)
                        .add(Set.of(CardEntry.Color.RED), 1.0),
                     new WeightedList<Set<String>>().add(null, 1.0).add(Set.of("Foil"), 1.0).add(Set.of("Arcane"), 1.0)
                  )
               ),
               1.0
            )
      );
      this.values.put("default", new CardScaler(new CardScaler.Config(tiers)));
      this.pools.put("default", new WeightedList<String>().add("default", 1.0));
   }
}
