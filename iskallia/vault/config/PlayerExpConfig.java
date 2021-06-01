package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.MathUtilities;

public class PlayerExpConfig extends Config {
   @Expose
   private int expPerVaultBoss;

   public int getRelicBoosterPackExp() {
      return (int)(this.expPerVaultBoss * MathUtilities.randomFloat(0.01F, 0.2F));
   }

   public int getExpPerVaultBoss() {
      return this.expPerVaultBoss;
   }

   @Override
   public String getName() {
      return "player_exp";
   }

   @Override
   protected void reset() {
      this.expPerVaultBoss = 10000;
   }
}
