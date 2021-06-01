package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class VaultItemsConfig extends Config {
   @Expose
   public VaultItemsConfig.VaultBurger VAULT_BURGER;

   @Override
   public String getName() {
      return "vault_items";
   }

   @Override
   protected void reset() {
      this.VAULT_BURGER = new VaultItemsConfig.VaultBurger();
      this.VAULT_BURGER.minExpPercent = 0.1F;
      this.VAULT_BURGER.maxExpPercent = 0.2F;
   }

   public static class VaultBurger {
      @Expose
      public float minExpPercent;
      @Expose
      public float maxExpPercent;
   }
}
