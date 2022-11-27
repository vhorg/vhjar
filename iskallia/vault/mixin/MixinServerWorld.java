package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerLevel.class})
public abstract class MixinServerWorld extends Level {
   @Shadow
   public abstract ServerChunkCache getChunkSource();

   @Shadow
   @Nonnull
   public abstract MinecraftServer getServer();

   protected MixinServerWorld(
      WritableLevelData p_204149_,
      ResourceKey<Level> p_204150_,
      Holder<DimensionType> p_204151_,
      Supplier<ProfilerFiller> p_204152_,
      boolean p_204153_,
      boolean p_204154_,
      long p_204155_
   ) {
      super(p_204149_, p_204150_, p_204151_, p_204152_, p_204153_, p_204154_, p_204155_);
   }

   @Inject(
      method = {"tickChunk"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void tickEnvironment(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
      if (ServerVaults.isVaultWorld(this)) {
         ci.cancel();
      }
   }
}
