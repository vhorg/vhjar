package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.HealConfig;

public class HealGroupConfig extends HealConfig {
   @Expose
   private final float radius;

   public HealGroupConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, float flatLifeHealed, float radius) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost, flatLifeHealed);
      this.radius = radius;
   }

   public float getRadius() {
      return this.radius;
   }
}
