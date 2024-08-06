package iskallia.vault.config.card;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.core.card.CardCondition;
import iskallia.vault.core.card.CardEntry;
import iskallia.vault.core.card.CardNeighborType;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.roll.IntRoll;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CardConditionsConfig extends Config {
   @Expose
   private Map<String, CardCondition> values;
   @Expose
   private Map<String, WeightedList<String>> pools;

   @Override
   public String getName() {
      return "card%sconditions".formatted(File.separator);
   }

   public Optional<CardCondition> getRandom(String id, RandomSource random) {
      if (id.startsWith("@")) {
         WeightedList<String> pool = this.pools.get(id.substring(1));
         return pool == null ? Optional.empty() : pool.getRandom(random).flatMap(s -> this.getRandom(s, random));
      } else {
         return Optional.ofNullable(this.values.get(id)).map(CardCondition::copy);
      }
   }

   @Override
   protected void reset() {
      this.values = new LinkedHashMap<>();
      this.pools = new LinkedHashMap<>();
      Map<Integer, WeightedList<List<CardCondition.Filter.Config>>> tiers = new LinkedHashMap<>();
      tiers.put(
         1,
         new WeightedList<List<CardCondition.Filter.Config>>()
            .add(
               Arrays.asList(
                  new CardCondition.Filter.Config(
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
                     new WeightedList<Set<String>>().add(null, 1.0).add(Set.of("Foil"), 1.0).add(Set.of("Arcane"), 1.0),
                     IntRoll.ofUniform(1, 2),
                     IntRoll.ofUniform(5, 6)
                  )
               ),
               1.0
            )
      );
      this.values.put("default", new CardCondition(new CardCondition.Config(tiers)));
      this.pools.put("default", new WeightedList<String>().add("default", 1.0));
   }
}
