package iskallia.vault.mixin;

import iskallia.vault.util.EnchantmentUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Enchantment.class})
public abstract class MixinEnchantment {
   @Inject(
      method = {"canEnchant"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void canApply(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
      Enchantment thisEnchantment = (Enchantment)this;
      if (EnchantmentUtil.isEnchantmentBlocked(thisEnchantment, stack)) {
         ci.setReturnValue(false);
      }
   }

   @Inject(
      method = {"canApplyAtEnchantingTable"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void canGetAtEnchantingTable(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
      Enchantment thisEnchantment = (Enchantment)this;
      if (EnchantmentUtil.isEnchantmentBlocked(thisEnchantment, stack)) {
         ci.setReturnValue(false);
      }
   }

   @Inject(
      method = {"isAllowedOnBooks"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void preventBooks(CallbackInfoReturnable<Boolean> ci) {
      Enchantment thisEnchantment = (Enchantment)this;
      if (EnchantmentUtil.isEnchantmentBlocked(thisEnchantment, ItemStack.EMPTY)) {
         ci.setReturnValue(false);
      }
   }

   @Inject(
      method = {"isTradeable"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void preventTrading(CallbackInfoReturnable<Boolean> ci) {
      Enchantment thisEnchantment = (Enchantment)this;
      if (EnchantmentUtil.isEnchantmentBlocked(thisEnchantment, ItemStack.EMPTY)) {
         ci.setReturnValue(false);
      }
   }
}
