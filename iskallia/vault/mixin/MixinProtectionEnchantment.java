package iskallia.vault.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ProtectionEnchantment.class})
public class MixinProtectionEnchantment extends Enchantment {
   protected MixinProtectionEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots) {
      super(pRarity, pCategory, pApplicableSlots);
   }

   @Inject(
      method = {"getDamageProtection(ILnet/minecraft/world/damagesource/DamageSource;)I"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getDamageProtection(int pLevel, DamageSource pSource, CallbackInfoReturnable<Integer> cir) {
      if (this != Enchantments.FALL_PROTECTION) {
         cir.setReturnValue(0);
      }
   }
}
