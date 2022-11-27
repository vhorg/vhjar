package iskallia.vault.gear.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;

public class VaultGearToolTier implements Tier {
   public static final VaultGearToolTier INSTANCE = new VaultGearToolTier();

   private VaultGearToolTier() {
   }

   public int getUses() {
      return 0;
   }

   public float getSpeed() {
      return 0.0F;
   }

   public float getAttackDamageBonus() {
      return 0.0F;
   }

   public int getLevel() {
      return Tiers.DIAMOND.getLevel();
   }

   public int getEnchantmentValue() {
      return Tiers.DIAMOND.getEnchantmentValue();
   }

   public Ingredient getRepairIngredient() {
      return Ingredient.EMPTY;
   }
}
