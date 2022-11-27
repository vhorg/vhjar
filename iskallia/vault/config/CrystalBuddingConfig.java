package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class CrystalBuddingConfig extends Config {
   @Expose
   private float growthChancePerRandomTick;

   public CrystalBuddingConfig() {
      this.reset();
   }

   @Override
   public String getName() {
      return "crystal_budding";
   }

   @Override
   protected void reset() {
      this.growthChancePerRandomTick = 0.25F;
   }

   public float getGrowthChancePerRandomTick() {
      return this.growthChancePerRandomTick;
   }
}
