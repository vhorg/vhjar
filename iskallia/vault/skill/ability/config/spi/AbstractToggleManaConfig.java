package iskallia.vault.skill.ability.config.spi;

import com.google.gson.annotations.Expose;

public abstract class AbstractToggleManaConfig extends AbstractAbilityConfig implements IPerSecondManaConfig {
   @Expose
   private final float manaCostPerSecond;

   public AbstractToggleManaConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCostPerSecond) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement);
      this.manaCostPerSecond = manaCostPerSecond;
   }

   @Override
   public float getManaCostPerSecond() {
      return this.manaCostPerSecond;
   }
}
