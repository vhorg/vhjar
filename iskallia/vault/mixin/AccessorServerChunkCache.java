package iskallia.vault.mixin;

import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ServerChunkCache.class})
public interface AccessorServerChunkCache {
   @Accessor("mainThread")
   Thread getThread();

   @Accessor("mainThread")
   @Mutable
   @Final
   void setThread(Thread var1);
}
