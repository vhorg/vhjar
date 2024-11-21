package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;

public class VaultLevelsConfig extends Config {
   @Expose
   private int maxLevel;
   @Expose
   private float expMultiplier;
   @Expose
   private final List<VaultLevelsConfig.VaultLevelMeta> levelMetas = new ArrayList<>();

   @Override
   public String getName() {
      return "vault_levels";
   }

   public VaultLevelsConfig.VaultLevelMeta getLevelMeta(int level) {
      int maxLevelTNLAvailable = this.levelMetas.size() - 1;
      return level >= 0 && level <= maxLevelTNLAvailable ? this.levelMetas.get(level) : this.levelMetas.get(maxLevelTNLAvailable);
   }

   public int getMaxLevel() {
      return this.maxLevel;
   }

   public float getExpMultiplier() {
      return this.expMultiplier;
   }

   @Override
   protected void reset() {
      this.maxLevel = 100;
      this.expMultiplier = 1.0F;
      this.levelMetas.clear();

      for (int x = 0; x < this.maxLevel; x++) {
         VaultLevelsConfig.VaultLevelMeta vaultLevel = new VaultLevelsConfig.VaultLevelMeta();
         vaultLevel.level = x;
         if (x < 6) {
            vaultLevel.tnl = 1000 * x + 1000;
         } else if (x < 8) {
            vaultLevel.tnl = 2000 * x - 5000;
         } else if (x < 10) {
            vaultLevel.tnl = 1500 * x - 1000;
         } else if (x < 40) {
            vaultLevel.tnl = 400 * x + 10000;
         } else if (x < 50) {
            vaultLevel.tnl = 1200 * x - 22000;
         } else if (x < 65) {
            vaultLevel.tnl = 98700;
         } else if (x < 80) {
            vaultLevel.tnl = 246750;
         } else if (x < 90) {
            vaultLevel.tnl = 493500;
         } else {
            vaultLevel.tnl = 1110375;
         }

         this.levelMetas.add(vaultLevel);
      }
   }

   public long defaultTNLFunction(int level) {
      return 10000L + 400L * level + Math.round(Math.pow(level / 59.0F, 28.0));
   }

   public static class VaultLevelMeta {
      @Expose
      public int level;
      @Expose
      public int tnl;
   }
}
