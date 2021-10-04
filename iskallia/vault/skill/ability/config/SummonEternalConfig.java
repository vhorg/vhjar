package iskallia.vault.skill.ability.config;

import com.google.gson.annotations.Expose;

public class SummonEternalConfig extends AbilityConfig {
   @Expose
   private final int numberOfEternals;
   @Expose
   private final int despawnTime;
   @Expose
   private final float ancientChance;
   @Expose
   private final boolean vaultOnly;

   public SummonEternalConfig(int cost, int cooldown, int numberOfEternals, int despawnTime, float ancientChance, boolean vaultOnly) {
      super(cost, AbilityConfig.Behavior.RELEASE_TO_PERFORM, cooldown);
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
