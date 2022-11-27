package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.HashMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class VaultItemsConfig extends Config {
   @Expose
   public VaultItemsConfig.FlatExpFood VAULT_COOKIE;
   @Expose
   public VaultItemsConfig.FlatExpFood PLAIN_BURGER;
   @Expose
   public VaultItemsConfig.FlatExpFood CHEESE_BURGER;
   @Expose
   public VaultItemsConfig.FlatExpFood DOUBLE_CHEESE_BURGER;
   @Expose
   public VaultItemsConfig.FlatExpFood DELUXE_CHEESE_BURGER;
   @Expose
   public VaultItemsConfig.FlatExpFood CRISPY_DELUXE_CHEESE_BURGER;
   @Expose
   public VaultItemsConfig.FlatExpFood SALTY_DELUXE_CHEESE_BURGER;
   @Expose
   public VaultItemsConfig.FlatExpFood CHEESE_BURGER_FEAST;
   @Expose
   public VaultItemsConfig.FlatExpFood SPICY_HEARTY_BURGER;
   @Expose
   public VaultItemsConfig.VaultDoll VAULT_DOLL;

   @Override
   public String getName() {
      return "vault_items";
   }

   @Override
   protected void reset() {
      this.VAULT_COOKIE = new VaultItemsConfig.FlatExpFood(0, 100);
      this.PLAIN_BURGER = new VaultItemsConfig.FlatExpFood(0, 100);
      this.CHEESE_BURGER = new VaultItemsConfig.FlatExpFood(0, 100);
      this.DOUBLE_CHEESE_BURGER = new VaultItemsConfig.FlatExpFood(0, 100);
      this.DELUXE_CHEESE_BURGER = new VaultItemsConfig.FlatExpFood(0, 100);
      this.CRISPY_DELUXE_CHEESE_BURGER = new VaultItemsConfig.FlatExpFood(0, 100);
      this.SALTY_DELUXE_CHEESE_BURGER = new VaultItemsConfig.FlatExpFood(0, 100);
      this.CHEESE_BURGER_FEAST = new VaultItemsConfig.FlatExpFood(0, 100);
      this.SPICY_HEARTY_BURGER = new VaultItemsConfig.FlatExpFood(0, 100);
      this.VAULT_DOLL = new VaultItemsConfig.VaultDoll(0.1F, 0.7F, 0.2F, 0.8F);
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

   public static class VaultDoll {
      @Expose
      public float lootPercentageMin;
      @Expose
      public float lootPercentageMax;
      @Expose
      public float xpPercentageMin;
      @Expose
      public float xpPercentageMax;
      @Expose
      private HashMap<Item, Float> helmetOffsets;

      public VaultDoll(float lootPercentageMin, float lootPercentageMax, float xpPercentageMin, float xpPercentageMax) {
         this.lootPercentageMin = lootPercentageMin;
         this.lootPercentageMax = lootPercentageMax;
         this.xpPercentageMin = xpPercentageMin;
         this.xpPercentageMax = xpPercentageMax;
         this.helmetOffsets = new HashMap<>();
         this.helmetOffsets.put(Items.DIAMOND_HELMET, 0.08F);
         this.helmetOffsets.put(Items.NETHERITE_HELMET, 0.08F);
      }

      public float getHelmetOffset(Item helmet) {
         return this.helmetOffsets.getOrDefault(helmet, 0.08F);
      }
   }
}
