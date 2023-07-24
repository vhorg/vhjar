package iskallia.vault.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ShieldItem.class})
public class MixinShieldItem {
   @Inject(
      method = {"use"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void canUseVanillaShield(Level pLevel, Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
      ItemStack stack = pPlayer.getItemInHand(pHand);
      if (stack.is(Items.SHIELD)) {
         cir.setReturnValue(InteractionResultHolder.pass(stack));
      }
   }
}
