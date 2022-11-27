package iskallia.vault.mixin;

import iskallia.vault.item.gear.TrinketItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.common.CuriosHelper;

@Mixin({CuriosHelper.class})
public class MixinCuriosHelper {
   @Inject(
      method = {"isStackValid"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   public void trinketIsValid(SlotContext slotContext, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
      if (stack.getItem() instanceof TrinketItem) {
         TrinketItem.getSlotIdentifier(stack).ifPresent(slot -> {
            if (slotContext.identifier().equals(slot)) {
               cir.setReturnValue(true);
            }
         });
      }
   }
}
