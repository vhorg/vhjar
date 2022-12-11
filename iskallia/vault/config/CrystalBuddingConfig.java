package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class CrystalBuddingConfig extends Config {
   @Expose
   private float maxSecondsBetweenGrowthUpdates;
   @Expose
   private float minSecondsBetweenGrowthUpdates;
   @Expose
   private boolean showParticles;

   public CrystalBuddingConfig() {
      this.reset();
   }

   @Override
   public String getName() {
      return "crystal_budding";
   }

   @Override
   protected void reset() {
      int average = 340;
      int variance = 60;
      this.maxSecondsBetweenGrowthUpdates = average + variance;
      this.minSecondsBetweenGrowthUpdates = average - variance;
      this.showParticles = true;
   }

   public float getMaxSecondsBetweenGrowthUpdates() {
      return this.maxSecondsBetweenGrowthUpdates;
   }

   public float getMinSecondsBetweenGrowthUpdates() {
      return this.minSecondsBetweenGrowthUpdates;
   }

   public boolean showParticles() {
      return this.showParticles;
   }
}
