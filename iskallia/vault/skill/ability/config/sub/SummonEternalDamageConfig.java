package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.SummonEternalConfig;

public class SummonEternalDamageConfig extends SummonEternalConfig {
   @Expose
   private final float increasedDamagePercent;

   public SummonEternalDamageConfig(
      int cost,
      int cooldown,
      int numberOfEternals,
      int summonedEternalsCap,
      int despawnTime,
      boolean vaultOnly,
      float ancientChance,
      float increasedDamagePercent
   ) {
      super(cost, cooldown, numberOfEternals, summonedEternalsCap, despawnTime, ancientChance, vaultOnly);
      this.increasedDamagePercent = increasedDamagePercent;
   }

   public float getIncreasedDamagePercent() {
      return this.increasedDamagePercent;
   }
}
