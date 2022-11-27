package iskallia.vault.mixin;

import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DamageEnchantment.class})
public class MixinDamageEnchantment {
   @Inject(
      method = {"canEnchant"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void canNotEnchant(ItemStack pStack, CallbackInfoReturnable<Boolean> cir) {
      cir.setReturnValue(false);
   }

   @Overwrite
   public float getDamageBonus(int pLevel, MobType pCreatureType) {
      return 0.0F;
   }
}
