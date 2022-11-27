package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class FighterConfig extends Config {
   @Expose
   public double knockback;
   @Expose
   public int chancerPerTick;

   @Override
   public String getName() {
      return "fighter";
   }

   @Override
   protected void reset() {
      this.knockback = 0.5;
      this.chancerPerTick = 20;
   }
}
