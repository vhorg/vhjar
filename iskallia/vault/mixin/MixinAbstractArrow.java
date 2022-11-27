package iskallia.vault.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({AbstractArrow.class})
public class MixinAbstractArrow {
   @Redirect(
      method = {"setEnchantmentEffectsFromEntity"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/entity/LivingEntity;)I"
      )
   )
   public int getPowerEnchantmentLevel(Enchantment ench, LivingEntity entity) {
      return ench != Enchantments.POWER_ARROWS && ench != Enchantments.FLAMING_ARROWS ? EnchantmentHelper.getEnchantmentLevel(ench, entity) : 0;
   }
}
