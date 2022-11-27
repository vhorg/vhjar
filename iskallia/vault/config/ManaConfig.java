package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class ManaConfig extends Config {
   @Expose
   private int MANA_MAX = 100;
   @Expose
   private float MANA_REGEN_PER_SECOND = 1.0F;

   @Override
   public String getName() {
      return "mana";
   }

   @Override
   protected void reset() {
      this.MANA_MAX = 100;
      this.MANA_REGEN_PER_SECOND = 1.0F;
   }

   public int getManaMax() {
      return this.MANA_MAX;
   }

   public float getManaRegenPerSecond() {
      return this.MANA_REGEN_PER_SECOND;
   }
}
