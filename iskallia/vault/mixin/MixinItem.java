package iskallia.vault.mixin;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.OverlevelEnchantHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Item.class})
public abstract class MixinItem {
   @Inject(
      method = {"getName"},
      cancellable = true,
      at = {@At("RETURN")}
   )
   public void appendOverlevelPrefix(ItemStack stack, CallbackInfoReturnable<Component> info) {
      if (stack.getItem() == Items.ENCHANTED_BOOK) {
         int overLevels = OverlevelEnchantHelper.getOverlevels(stack);
         if (overLevels != -1) {
            MutableComponent formatted = ModConfigs.OVERLEVEL_ENCHANT.format((Component)info.getReturnValue(), overLevels);
            if (formatted != null) {
               info.setReturnValue(formatted);
               info.cancel();
            }
         }
      }
   }
}
