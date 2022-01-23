package iskallia.vault.mixin;

import iskallia.vault.Vault;
import iskallia.vault.util.IBiomeUpdate;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.SaveFormat.LevelSave;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerWorld.class})
public abstract class MixinServerWorld extends World {
   @Shadow
   public abstract ServerChunkProvider func_72863_F();

   @Shadow
   @Nonnull
   public abstract MinecraftServer func_73046_m();

   protected MixinServerWorld(
      ISpawnWorldInfo worldInfo,
      RegistryKey<World> dimension,
      DimensionType dimensionType,
      Supplier<IProfiler> profiler,
      boolean isRemote,
      boolean isDebug,
      long seed
   ) {
      super(worldInfo, dimension, dimensionType, profiler, isRemote, isDebug, seed);
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   public void ctor(
      MinecraftServer server,
      Executor executor,
      LevelSave save,
      IServerWorldInfo info,
      RegistryKey<World> key,
      DimensionType type,
      IChunkStatusListener listener,
      ChunkGenerator gen,
      boolean p_i241885_9_,
      long p_i241885_10_,
      List<ISpecialSpawner> spawners,
      boolean p_i241885_13_,
      CallbackInfo ci
   ) {
      if (key == Vault.OTHER_SIDE_KEY) {
         ((IBiomeUpdate)this.func_72863_F().func_201711_g())
            .update(this.func_73046_m().func_71218_a(World.field_234918_g_).func_72863_F().func_201711_g().func_202090_b());
      }
   }

   @Inject(
      method = {"tickEnvironment"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void tickEnvironment(Chunk chunk, int randomTickSpeed, CallbackInfo ci) {
      if (this.func_234923_W_() == Vault.VAULT_KEY) {
         ci.cancel();
      }
   }
}
