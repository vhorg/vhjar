package iskallia.vault.mixin;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.dankstorage.inventory.DankSlot;
import tfar.dankstorage.item.DankItem;

@Mixin({SlotItemHandler.class})
public class MixinSlotItemHandler {
   @Inject(
      method = {"isItemValid"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void itemValid(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
      SlotItemHandler itemHandler = (SlotItemHandler)this;
      if (itemHandler instanceof DankSlot && stack.func_77973_b() instanceof DankItem) {
         cir.setReturnValue(false);
      }
   }
}
