package iskallia.vault.config.bounty.task.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.util.data.WeightedList;
import java.util.Map;

public class TaskEntry<T> {
   @Expose
   private WeightedList<GenericEntry<T>> pool;

   public TaskEntry(GenericEntry<T> entry, int weight) {
      this.pool = new WeightedList<>(Map.of(entry, weight));
   }

   public TaskEntry<T> addEntry(GenericEntry<T> entry, int weight) {
      this.pool.add(entry, weight);
      return this;
   }

   public GenericEntry<T> getRandom() {
      return this.pool.getRandom(Config.rand);
   }
}
