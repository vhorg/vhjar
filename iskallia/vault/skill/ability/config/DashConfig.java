package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractInstantManaConfig;

public class DashConfig extends AbstractInstantManaConfig {
   @Expose
   private final int extraDistance;

   public DashConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, int extraDistance) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost);
      this.extraDistance = extraDistance;
   }

   public int getExtraDistance() {
      return this.extraDistance;
   }
}
