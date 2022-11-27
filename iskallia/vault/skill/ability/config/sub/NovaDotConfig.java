package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.NovaConfig;

public class NovaDotConfig extends NovaConfig {
   @Expose
   private final int durationSeconds;

   public NovaDotConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCost,
      float radius,
      float percentAttackDamageDealt,
      int durationSeconds
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost, radius, percentAttackDamageDealt, 0.0F);
      this.durationSeconds = durationSeconds;
   }

   public int getDurationSeconds() {
      return this.durationSeconds;
   }
}
