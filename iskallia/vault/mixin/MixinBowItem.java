package iskallia.vault.mixin;

import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({BowItem.class})
public class MixinBowItem {
   @Redirect(
      method = {"releaseUsing"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"
      )
   )
   public int getPowerEnchantmentLevel(Enchantment ench, ItemStack stack) {
      return ench != Enchantments.POWER_ARROWS && ench != Enchantments.FLAMING_ARROWS ? EnchantmentHelper.getItemEnchantmentLevel(ench, stack) : 0;
   }
}
