package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractInstantManaConfig;

public class GhostWalkConfig extends AbstractInstantManaConfig {
   @Expose
   private final int durationTicks;

   public GhostWalkConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, int durationTicks) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost);
      this.durationTicks = durationTicks;
   }

   public int getDurationTicks() {
      return this.durationTicks;
   }
}
