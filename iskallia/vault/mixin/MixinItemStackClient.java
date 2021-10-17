package iskallia.vault.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ItemStack.class})
public class MixinItemStackClient {
   @Redirect(
      method = {"getTooltip"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/item/ItemStack;hasDisplayName()Z",
         ordinal = 0
      )
   )
   public boolean doDisplayNameItalic(ItemStack stack) {
      return false;
   }
}
