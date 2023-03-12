package iskallia.vault.mixin;

import iskallia.vault.block.VaultLogBlock;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AxeItem.class})
public class AxeItemMixin {
   @Inject(
      method = {"getAxeStrippingState"},
      at = {@At("RETURN")},
      cancellable = true,
      remap = false
   )
   private static void getVaultLogStrippingState(BlockState original, CallbackInfoReturnable<BlockState> cir) {
      if (original.getBlock() instanceof VaultLogBlock block && block.isStrippable()) {
         cir.setReturnValue(block.getStripped(original));
      }
   }
}
