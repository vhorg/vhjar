package iskallia.vault.config.card;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.task.Task;
import java.io.File;
import java.util.Map;
import java.util.Optional;

public class CardTasksConfig extends Config {
   @Expose
   private Map<String, Task> values;
   @Expose
   private Map<String, WeightedList<String>> pools;

   @Override
   public String getName() {
      return "card%stasks".formatted(File.separator);
   }

   public Optional<Task> getRandom(String id, RandomSource random) {
      if (id.startsWith("@")) {
         WeightedList<String> pool = this.pools.get(id.substring(1));
         return pool == null ? Optional.empty() : pool.getRandom(random).flatMap(s -> this.getRandom(s, random));
      } else {
         return Optional.ofNullable(this.values.get(id));
      }
   }

   @Override
   protected void reset() {
   }
}
