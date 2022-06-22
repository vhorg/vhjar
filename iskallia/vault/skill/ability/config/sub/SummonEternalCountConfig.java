package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.SummonEternalConfig;

public class SummonEternalCountConfig extends SummonEternalConfig {
   @Expose
   private final int additionalCount;

   public SummonEternalCountConfig(
      int cost, int cooldown, int numberOfEternals, int summonedEternalsCap, int despawnTime, boolean vaultOnly, float ancientChance, int additionalCount
   ) {
      super(cost, cooldown, numberOfEternals, summonedEternalsCap, despawnTime, ancientChance, vaultOnly);
      this.additionalCount = additionalCount;
   }

   public int getAdditionalCount() {
      return this.additionalCount;
   }
}
