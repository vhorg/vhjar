package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.bottle.BottleItem;
import java.util.HashMap;
import java.util.Map;

public class PotionConfig extends Config {
   @Expose
   private Map<String, PotionConfig.Potion> potions;

   @Override
   public String getName() {
      return "vault_potion";
   }

   public PotionConfig.Potion getPotion(BottleItem.Type type) {
      return this.potions.get(type.getName());
   }

   @Override
   protected void reset() {
      this.potions = new HashMap<>();
      this.potions
         .put(BottleItem.Type.VIAL.getName(), new PotionConfig.Potion(6, 0, 6000, Map.of(0, 100, 10, 200, 20, 400), Map.of(0, 100, 10, 200, 20, 400), 400, 4));
      this.potions
         .put(BottleItem.Type.POTION.getName(), new PotionConfig.Potion(6, 1, 6000, Map.of(0, 100, 10, 200, 20, 400), Map.of(0, 100, 10, 200, 20, 400), 400, 6));
      this.potions
         .put(
            BottleItem.Type.MIXTURE.getName(), new PotionConfig.Potion(6, 2, 6000, Map.of(0, 100, 10, 200, 20, 400), Map.of(0, 100, 10, 200, 20, 400), 400, 8)
         );
      this.potions
         .put(BottleItem.Type.BREW.getName(), new PotionConfig.Potion(6, 3, 6000, Map.of(0, 100, 10, 200, 20, 400), Map.of(0, 100, 10, 200, 20, 400), 400, 10));
   }

   public static class Potion {
      @Expose
      private int charges;
      @Expose
      private int modifiers;
      @Expose
      private int timeRecharge;
      @Expose
      private Map<Integer, Integer> vaultLevelMobRecharge;
      @Expose
      private Map<Integer, Integer> vaultLevelChestRecharge;
      @Expose
      private int effectDuration;
      @Expose
      private int healing;

      public Potion(
         int charges,
         int modifiers,
         int timeRecharge,
         Map<Integer, Integer> vaultLevelMobRecharge,
         Map<Integer, Integer> vaultLevelChestRecharge,
         int effectDuration,
         int healing
      ) {
         this.charges = charges;
         this.modifiers = modifiers;
         this.timeRecharge = timeRecharge;
         this.vaultLevelMobRecharge = vaultLevelMobRecharge;
         this.vaultLevelChestRecharge = vaultLevelChestRecharge;
         this.effectDuration = effectDuration;
         this.healing = healing;
      }

      public int getCharges() {
         return this.charges;
      }

      public int getModifiers() {
         return this.modifiers;
      }

      public int getTimeRecharge() {
         return this.timeRecharge;
      }

      public int getChestRecharge(int vaultLevel) {
         int maxLowerOrEqualVaultLevel = 0;

         for (int rechargeVaultLevel : this.vaultLevelChestRecharge.keySet()) {
            if (vaultLevel >= rechargeVaultLevel && rechargeVaultLevel > maxLowerOrEqualVaultLevel) {
               maxLowerOrEqualVaultLevel = rechargeVaultLevel;
            }
         }

         return this.vaultLevelChestRecharge.get(maxLowerOrEqualVaultLevel);
      }

      public int getMobRecharge(int vaultLevel) {
         int maxLowerOrEqualVaultLevel = 0;

         for (int rechargeVaultLevel : this.vaultLevelMobRecharge.keySet()) {
            if (vaultLevel >= rechargeVaultLevel && rechargeVaultLevel > maxLowerOrEqualVaultLevel) {
               maxLowerOrEqualVaultLevel = rechargeVaultLevel;
            }
         }

         return this.vaultLevelMobRecharge.get(maxLowerOrEqualVaultLevel);
      }

      public int getEffectDuration() {
         return this.effectDuration;
      }

      public int getHealing() {
         return this.healing;
      }
   }
}
