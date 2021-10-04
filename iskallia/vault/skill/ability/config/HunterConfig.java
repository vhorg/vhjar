package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;

public class HunterConfig extends AbilityConfig {
   @Expose
   private final double searchRadius;
   @Expose
   private final int color;
   @Expose
   private final int tickDuration;

   public HunterConfig(int learningCost, double searchRadius, int color, int tickDuration) {
      super(learningCost, AbilityConfig.Behavior.RELEASE_TO_PERFORM);
      this.searchRadius = searchRadius;
      this.color = color;
      this.tickDuration = tickDuration;
   }

   public double getSearchRadius() {
      return this.searchRadius;
   }

   public int getColor() {
      return this.color;
   }

   public int getTickDuration() {
      return this.tickDuration;
   }
}
