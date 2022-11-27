package iskallia.vault.skill.ability.config.spi;

import com.google.gson.annotations.Expose;

public abstract class AbstractHoldManaConfig extends AbstractAbilityConfig implements IPerSecondManaConfig {
   @Expose
   private final float manaCostPerSecond;

   public AbstractHoldManaConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCostPerSecond) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement);
      this.manaCostPerSecond = manaCostPerSecond;
   }

   @Override
   public float getManaCostPerSecond() {
      return this.manaCostPerSecond;
   }
}
