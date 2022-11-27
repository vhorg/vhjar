package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractInstantManaConfig;

public class HealConfig extends AbstractInstantManaConfig {
   @Expose
   private final float flatLifeHealed;

   public HealConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, float flatLifeHealed) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost);
      this.flatLifeHealed = flatLifeHealed;
   }

   public float getFlatLifeHealed() {
      return this.flatLifeHealed;
   }
}
