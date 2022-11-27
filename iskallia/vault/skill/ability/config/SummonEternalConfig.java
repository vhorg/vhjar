package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.spi.AbstractInstantManaConfig;

public class SummonEternalConfig extends AbstractInstantManaConfig {
   @Expose
   private final int numberOfEternals;
   @Expose
   private final int despawnTime;
   @Expose
   private final float ancientChance;
   @Expose
   private final boolean vaultOnly;

   public SummonEternalConfig(
      int learningCost,
      int regretCost,
      int cooldownTicks,
      int levelRequirement,
      float manaCost,
      int numberOfEternals,
      int despawnTime,
      float ancientChance,
      boolean vaultOnly
   ) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost);
      this.numberOfEternals = numberOfEternals;
      this.despawnTime = despawnTime;
      this.ancientChance = ancientChance;
      this.vaultOnly = vaultOnly;
   }

   public int getNumberOfEternals() {
      return this.numberOfEternals;
   }

   public int getDespawnTime() {
      return this.despawnTime;
   }

   public float getAncientChance() {
      return this.ancientChance;
   }

   public boolean isVaultOnly() {
      return this.vaultOnly;
   }
}
