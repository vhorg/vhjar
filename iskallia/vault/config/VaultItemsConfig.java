package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class VaultItemsConfig extends Config {
   @Expose
   public VaultItemsConfig.PercentageExpFood VAULT_BURGER;
   @Expose
   public VaultItemsConfig.PercentageExpFood VAULT_PIZZA;
   @Expose
   public VaultItemsConfig.FlatExpFood VAULT_COOKIE;

   @Override
   public String getName() {
      return "vault_items";
   }

   @Override
   protected void reset() {
      this.VAULT_BURGER = new VaultItemsConfig.PercentageExpFood(0.0F, 0.03F);
      this.VAULT_PIZZA = new VaultItemsConfig.PercentageExpFood(0.0F, 0.01F);
      this.VAULT_COOKIE = new VaultItemsConfig.FlatExpFood(0, 100);
   }

   public static class FlatExpFood {
      @Expose
      public int minExp;
      @Expose
      public int maxExp;

      public FlatExpFood(int minExp, int maxExp) {
         this.minExp = minExp;
         this.maxExp = maxExp;
      }
   }

   public static class PercentageExpFood {
      @Expose
      public float minExpPercent;
      @Expose
      public float maxExpPercent;

      public PercentageExpFood(float minExpPercent, float maxExpPercent) {
         this.minExpPercent = minExpPercent;
         this.maxExpPercent = maxExpPercent;
      }
   }
}
