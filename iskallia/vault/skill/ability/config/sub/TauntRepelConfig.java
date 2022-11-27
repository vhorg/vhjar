package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.TauntConfig;

public class TauntRepelConfig extends TauntConfig {
   @Expose
   private final float repelForce;

   public TauntRepelConfig(
      int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, float radius, int durationTicks, float repelForce
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost, radius, durationTicks);
      this.repelForce = repelForce;
   }

   public float getRepelForce() {
      return this.repelForce;
   }
}
