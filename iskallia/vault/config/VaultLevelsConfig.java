package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;

public class VaultLevelsConfig extends Config {
   @Expose
   private int maxLevel;
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

   @Override
   protected void reset() {
      this.maxLevel = 100;
      this.levelMetas.clear();

      for (int i = 0; i < this.maxLevel; i++) {
         VaultLevelsConfig.VaultLevelMeta vaultLevel = new VaultLevelsConfig.VaultLevelMeta();
         vaultLevel.level = i;
         vaultLevel.tnl = (int)this.defaultTNLFunction(i);
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
