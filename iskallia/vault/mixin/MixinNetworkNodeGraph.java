package iskallia.vault.mixin;

import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.network.NetworkNodeGraph;
import iskallia.vault.VaultMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetworkNodeGraph.class})
public class MixinNetworkNodeGraph {
   private long timeMs = 0L;

   @Inject(
      method = {"invalidate"},
      at = {@At("HEAD")},
      remap = false
   )
   public void onStartInvalidate(Action action, Level level, BlockPos origin, CallbackInfo ci) {
      this.timeMs = System.currentTimeMillis();
   }

   @Inject(
      method = {"invalidate"},
      at = {@At("RETURN")},
      remap = false
   )
   public void onEndInvalidate(Action action, Level level, BlockPos origin, CallbackInfo ci) {
      long timeMs = System.currentTimeMillis() - this.timeMs;
      if (timeMs > 100L) {
         VaultMod.LOGGER.info("NetworkNodeGraph#invalidate took " + timeMs + "ms");
         VaultMod.LOGGER.info("Action: " + action + ", Level: " + level.dimension() + ", Origin: " + origin);
      }
   }
}
