package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.LinkedList;
import java.util.List;

public class VaultLevelsConfig extends Config {
   @Expose
   public List<VaultLevelsConfig.VaultLevelMeta> levelMetas;

   @Override
   public String getName() {
      return "vault_levels";
   }

   public VaultLevelsConfig.VaultLevelMeta getLevelMeta(int level) {
      int maxLevelTNLAvailable = this.levelMetas.size() - 1;
      return level >= 0 && level <= maxLevelTNLAvailable ? this.levelMetas.get(level) : this.levelMetas.get(maxLevelTNLAvailable);
   }

   @Override
   protected void reset() {
      this.levelMetas = new LinkedList<>();

      for (int i = 0; i < 80; i++) {
         VaultLevelsConfig.VaultLevelMeta vaultLevel = new VaultLevelsConfig.VaultLevelMeta();
         vaultLevel.level = i;
         vaultLevel.tnl = this.defaultTNLFunction(i);
         this.levelMetas.add(vaultLevel);
      }
   }

   public int defaultTNLFunction(int level) {
      return level * 500 + 10000;
   }

   public static class VaultLevelMeta {
      @Expose
      public int level;
      @Expose
      public int tnl;
   }
}
