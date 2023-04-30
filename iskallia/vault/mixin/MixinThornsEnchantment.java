package iskallia.vault.mixin;

import iskallia.vault.util.EnchantmentUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ThornsEnchantment.class})
public class MixinThornsEnchantment extends Enchantment {
   protected MixinThornsEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots) {
      super(pRarity, pCategory, pApplicableSlots);
   }

   @Inject(
      method = {"canEnchant"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void canApply(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
      Enchantment thisEnchantment = this;
      if (EnchantmentUtil.isEnchantmentBlocked(thisEnchantment, stack)) {
         ci.setReturnValue(false);
      }
   }
}
