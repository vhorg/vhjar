package iskallia.vault.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ItemStack.class})
public abstract class MixinItemStackClient {
   @Redirect(
      method = {"getTooltipLines"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;hasCustomHoverName()Z",
         ordinal = 0
      )
   )
   public boolean doDisplayNameItalic(ItemStack itemStack) {
      return false;
   }
}
