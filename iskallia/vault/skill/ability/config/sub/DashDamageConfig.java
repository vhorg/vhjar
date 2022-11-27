package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.DashConfig;

public class DashDamageConfig extends DashConfig {
   @Expose
   private final float attackDamagePercentPerDash;

   public DashDamageConfig(
      int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, int extraRadius, float attackDamagePercentPerDash
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost, extraRadius);
      this.attackDamagePercentPerDash = attackDamagePercentPerDash;
   }

   public float getAttackDamagePercentPerDash() {
      return this.attackDamagePercentPerDash;
   }
}
