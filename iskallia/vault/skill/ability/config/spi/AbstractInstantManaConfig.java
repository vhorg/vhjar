package iskallia.vault.skill.ability.config.spi;

import com.google.gson.annotations.Expose;

public abstract class AbstractInstantManaConfig extends AbstractAbilityConfig implements IInstantManaConfig {
   @Expose
   private final float manaCost;

   public AbstractInstantManaConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement);
      this.manaCost = manaCost;
   }

   @Override
   public float getManaCost() {
      return this.manaCost;
   }
}
