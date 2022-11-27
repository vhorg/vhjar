package iskallia.vault.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SlotItemHandler.class})
public class MixinSlotItemHandler {
   @Inject(
      method = {"mayPlace"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void itemValid(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
      SlotItemHandler itemHandler = (SlotItemHandler)this;
   }
}
