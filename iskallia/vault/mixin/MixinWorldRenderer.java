package iskallia.vault.mixin;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Set;
import java.util.SortedSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public abstract class MixinWorldRenderer {
   @Shadow
   @Final
   private Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;

   @Inject(
      method = {"destroyBlockProgress"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void destroyBlockProgress(int breakerId, BlockPos pos, int progress, CallbackInfo ci) {
      if (breakerId == -1) {
         if (progress >= 0 && progress < 10) {
            SortedSet<BlockDestructionProgress> set = (SortedSet<BlockDestructionProgress>)this.destructionProgress
               .computeIfAbsent(pos.asLong(), key -> Sets.newTreeSet());
            if (set.isEmpty() || set.last().getProgress() < progress) {
               set.clear();
               BlockDestructionProgress result = new BlockDestructionProgress(breakerId, pos);
               result.setProgress(progress);
               set.add(result);
            }
         } else {
            this.destructionProgress.remove(pos.asLong());
         }

         ci.cancel();
      }
   }

   @Inject(
      method = {"removeProgress"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void removeProgress(BlockDestructionProgress progress, CallbackInfo ci) {
      long i = progress.getPos().asLong();
      Set<BlockDestructionProgress> set = (Set<BlockDestructionProgress>)this.destructionProgress.get(i);
      if (set == null) {
         ci.cancel();
      }
   }
}
