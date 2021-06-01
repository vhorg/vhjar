package iskallia.vault.mixin;

import iskallia.vault.item.gear.VaultGear;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Enchantment.class})
public abstract class MixinEnchantment {
   @Inject(
      method = {"canApply"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void canApply(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
      if (stack.func_77973_b() instanceof VaultGear && !((VaultGear)stack.func_77973_b()).canApply(stack, (Enchantment)this)) {
         ci.setReturnValue(false);
      }
   }
}
