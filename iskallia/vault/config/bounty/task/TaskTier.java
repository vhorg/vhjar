package iskallia.vault.config.bounty.task;

import com.google.gson.annotations.Expose;

public class TaskTier<T> {
   @Expose
   private int minLevel;
   @Expose
   private T entry;

   public TaskTier(int minLevel, T entry) {
      this.minLevel = minLevel;
      this.entry = entry;
   }

   public int getMinLevel() {
      return this.minLevel;
   }

   public T getEntry() {
      return this.entry;
   }
}
