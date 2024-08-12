package iskallia.vault.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MultiPlayerGameMode.class})
public class MixinMultiPlayerGameMode {
   @Shadow
   private BlockPos destroyBlockPos;

   @Inject(
      method = {"destroyBlock"},
      at = {@At("HEAD")}
   )
   public void destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
      if (pos.equals(this.destroyBlockPos)) {
         this.destroyBlockPos = new BlockPos(-1, -1, -1);
      }
   }
}
