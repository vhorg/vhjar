package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractInstantManaConfig;

public class MegaJumpConfig extends AbstractInstantManaConfig {
   @Expose
   private final int height;

   public MegaJumpConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, int height) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost);
      this.height = height;
   }

   public int getHeight() {
      return this.height;
   }
}
