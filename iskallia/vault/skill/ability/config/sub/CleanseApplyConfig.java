package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.config.CleanseConfig;

public class CleanseApplyConfig extends CleanseConfig {
   @Expose
   private final int applyRadius;

   public CleanseApplyConfig(int learningCost, AbilityConfig.Behavior behavior, int cooldown, int applyRadius) {
      super(learningCost, behavior, cooldown);
      this.applyRadius = applyRadius;
   }

   public int getApplyRadius() {
      return this.applyRadius;
   }
}
