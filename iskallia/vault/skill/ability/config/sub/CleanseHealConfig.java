package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.config.CleanseConfig;

public class CleanseHealConfig extends CleanseConfig {
   @Expose
   private final float healthPerEffectRemoved;

   public CleanseHealConfig(int learningCost, AbilityConfig.Behavior behavior, int cooldown, float healthPerEffectRemoved) {
      super(learningCost, behavior, cooldown);
      this.healthPerEffectRemoved = healthPerEffectRemoved;
   }

   public float getHealthPerEffectRemoved() {
      return this.healthPerEffectRemoved;
   }
}
