package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class ArenaGeneralConfig extends Config {
   @Expose
   public int BOSS_COUNT;
   @Expose
   public int TICK_COUNTER;

   @Override
   public String getName() {
      return "arena_general";
   }

   @Override
   protected void reset() {
      this.BOSS_COUNT = 3;
      this.TICK_COUNTER = 3600;
   }
}
