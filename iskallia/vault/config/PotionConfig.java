package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.BottleItem;
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
      this.potions.put(BottleItem.Type.VIAL.getName(), new PotionConfig.Potion(6, 0, 6000, 150, 400, 4));
      this.potions.put(BottleItem.Type.POTION.getName(), new PotionConfig.Potion(6, 1, 6000, 150, 400, 6));
      this.potions.put(BottleItem.Type.MIXTURE.getName(), new PotionConfig.Potion(6, 2, 6000, 150, 400, 8));
      this.potions.put(BottleItem.Type.BREW.getName(), new PotionConfig.Potion(6, 3, 6000, 150, 400, 10));
   }

   public static class Potion {
      @Expose
      private int charges;
      @Expose
      private int modifiers;
      @Expose
      private int timeRecharge;
      @Expose
      private int mobRecharge;
      @Expose
      private int effectDuration;
      @Expose
      private int healing;

      public Potion(int charges, int modifiers, int timeRecharge, int mobRecharge, int effectDuration, int healing) {
         this.charges = charges;
         this.modifiers = modifiers;
         this.timeRecharge = timeRecharge;
         this.mobRecharge = mobRecharge;
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

      public int getMobRecharge() {
         return this.mobRecharge;
      }

      public int getEffectDuration() {
         return this.effectDuration;
      }

      public int getHealing() {
         return this.healing;
      }
   }
}
