package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class BlackMarketConfig extends Config {
   @Expose
   private int resetHours;
   @Expose
   private int resetMinutes;

   @Override
   public String getName() {
      return "black_market";
   }

   @Override
   protected void reset() {
      this.resetHours = 24;
      this.resetMinutes = 0;
   }

   public int getResetHours() {
      return this.resetHours;
   }

   public int getResetMinutes() {
      return this.resetMinutes;
   }
}
