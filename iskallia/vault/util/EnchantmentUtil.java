package iskallia.vault.util;

import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.item.tool.PaxelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ArrowDamageEnchantment;
import net.minecraft.world.item.enchantment.ArrowFireEnchantment;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.FireAspectEnchantment;
import net.minecraft.world.item.enchantment.KnockbackEnchantment;
import net.minecraft.world.item.enchantment.MendingEnchantment;
import net.minecraft.world.item.enchantment.MultiShotEnchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.item.enchantment.ThornsEnchantment;

public class EnchantmentUtil {
   public static boolean isEnchantmentBlocked(Enchantment ench, ItemStack stack) {
      if (isBlockedProtectionEnchantment(ench) || ench instanceof DamageEnchantment) {
         return true;
      } else {
         return !(ench instanceof SweepingEdgeEnchantment)
               && !(ench instanceof ArrowDamageEnchantment)
               && !(ench instanceof FireAspectEnchantment)
               && !(ench instanceof ArrowFireEnchantment)
               && !(ench instanceof MultiShotEnchantment)
               && !(ench instanceof ThornsEnchantment)
               && !(ench instanceof KnockbackEnchantment)
            ? ench instanceof MendingEnchantment && (stack.getItem() instanceof VaultGearItem || stack.getItem() instanceof PaxelItem)
            : true;
      }
   }

   private static boolean isBlockedProtectionEnchantment(Enchantment ench) {
      return ench instanceof ProtectionEnchantment && ench != Enchantments.FALL_PROTECTION;
   }
}
