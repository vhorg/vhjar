package iskallia.vault.item.paxel;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemTier;
import net.minecraft.item.crafting.Ingredient;

public class PaxelItemTier implements IItemTier {
   public static final PaxelItemTier INSTANCE = new PaxelItemTier();

   public int func_200926_a() {
      return 6000;
   }

   public float func_200928_b() {
      return ItemTier.NETHERITE.func_200928_b() + 1.0F;
   }

   public float func_200929_c() {
      return ItemTier.NETHERITE.func_200929_c() + 1.0F;
   }

   public int func_200925_d() {
      return ItemTier.NETHERITE.func_200925_d();
   }

   public int func_200927_e() {
      return ItemTier.NETHERITE.func_200927_e() + 3;
   }

   public Ingredient func_200924_f() {
      return Ingredient.field_193370_a;
   }
}
