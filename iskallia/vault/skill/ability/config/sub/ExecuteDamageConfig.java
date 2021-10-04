package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.AbilityConfig;
import iskallia.vault.skill.ability.config.ExecuteConfig;

public class ExecuteDamageConfig extends ExecuteConfig {
   @Expose
   private final float damagePercentMissingHealth;

   public ExecuteDamageConfig(int cost, AbilityConfig.Behavior behavior, int effectDuration, float damagePercentMissingHealth) {
      super(cost, behavior, 0.0F, effectDuration);
      this.damagePercentMissingHealth = damagePercentMissingHealth;
   }

   public float getDamagePercentPerMissingHealthPercent() {
      return this.damagePercentMissingHealth;
   }
}
