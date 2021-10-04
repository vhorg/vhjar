package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.config.CleanseConfig;

public class CleanseImmuneConfig extends CleanseConfig {
   @Expose
   private final int immunityDuration;

   public CleanseImmuneConfig(int learningCost, AbilityConfig.Behavior behavior, int cooldown, int duration) {
      super(learningCost, behavior, cooldown);
      this.immunityDuration = duration;
   }

   public int getImmunityDuration() {
      return this.immunityDuration;
   }
}
