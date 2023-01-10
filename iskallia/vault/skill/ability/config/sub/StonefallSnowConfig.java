package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.StonefallConfig;

public class StonefallSnowConfig extends StonefallConfig {
   @Expose
   private final float radius;

   public StonefallSnowConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, float radius) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost, 0);
      this.radius = radius;
   }

   public float getRadius() {
      return this.radius;
   }
}
