package iskallia.vault.mixin;

import iskallia.vault.init.ModBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ShearsItem.class})
public class MixinShearsItem {
   @Inject(
      method = {"getDestroySpeed"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getDestroySpeed(ItemStack pStack, BlockState pState, CallbackInfoReturnable<Float> cir) {
      if (pState.is(ModBlocks.MAGIC_SILK_BLOCK)) {
         cir.setReturnValue(5.0F);
      }

      if (pState.is(ModBlocks.VELVET_BLOCK)) {
         cir.setReturnValue(5.0F);
      }

      if (pState.is(ModBlocks.VELVET_BLOCK_CHISELED)) {
         cir.setReturnValue(5.0F);
      }

      if (pState.is(ModBlocks.VELVET_BLOCK_STRIPS)) {
         cir.setReturnValue(5.0F);
      }
   }
}
