package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class VaultUtilitiesConfig extends Config {
   @Expose
   private int vaultPearlMaxUses;

   @Override
   public String getName() {
      return "vault_utilities";
   }

   @Override
   protected void reset() {
      this.vaultPearlMaxUses = 10;
   }

   public int getVaultPearlMaxUses() {
      return this.vaultPearlMaxUses;
   }
}
