package iskallia.vault.item.paxel;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;

public class PaxelItemTier implements Tier {
   public static final PaxelItemTier INSTANCE = new PaxelItemTier();

   public int getUses() {
      return 6000;
   }

   public float getSpeed() {
      return Tiers.NETHERITE.getSpeed() + 1.0F;
   }

   public float getAttackDamageBonus() {
      return Tiers.STONE.getAttackDamageBonus();
   }

   public int getLevel() {
      return Tiers.NETHERITE.getLevel();
   }

   public int getEnchantmentValue() {
      return Tiers.NETHERITE.getEnchantmentValue() + 3;
   }

   public Ingredient getRepairIngredient() {
      return Ingredient.EMPTY;
   }
}
