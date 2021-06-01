package iskallia.vault.mixin;

import iskallia.vault.Vault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({PlayerInventory.class})
public class MixinPlayerInventory {
   @Shadow
   @Final
   public PlayerEntity field_70458_d;

   @ModifyArg(
      method = {"func_234563_a_"},
      index = 0,
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/item/ItemStack;damageItem(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"
      )
   )
   public int limitMaxArmorDamage(int damageAmount) {
      return this.field_70458_d.field_70170_p.func_234923_W_() == Vault.VAULT_KEY ? Math.min(damageAmount, 5) : damageAmount;
   }
}
