package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractInstantManaConfig;

public class TauntConfig extends AbstractInstantManaConfig {
   @Expose
   private final float radius;
   @Expose
   private final int durationTicks;

   public TauntConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, float radius, int durationTicks) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost);
      this.radius = radius;
      this.durationTicks = durationTicks;
   }

   public float getRadius() {
      return this.radius;
   }

   public int getDurationTicks() {
      return this.durationTicks;
   }
}
