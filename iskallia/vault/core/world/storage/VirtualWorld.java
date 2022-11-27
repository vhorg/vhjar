package iskallia.vault.core.world.storage;

import iskallia.vault.core.world.DummyProgressListener;
import iskallia.vault.core.world.generator.DummyChunkGenerator;
import iskallia.vault.mixin.AccessorMinecraftServer;
import iskallia.vault.mixin.AccessorServerChunkCache;
import iskallia.vault.mixin.AccessorWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraftforge.server.ServerLifecycleHooks;

public class VirtualWorld extends ServerLevel {
   protected ThreadingMode threadingMode;
   protected int tickCount;
   protected int randomTickSpeed;
   protected boolean delete;

   protected VirtualWorld(
      MinecraftServer server,
      Executor executor,
      LevelStorageAccess session,
      ServerLevelData properties,
      ResourceKey<Level> worldKey,
      Holder<DimensionType> registryEntry,
      ChunkProgressListener progressListener,
      ChunkGenerator chunkGenerator,
      boolean isDebug,
      long hashedSeed,
      List<CustomSpawner> spawners,
      boolean shouldTickTime
   ) {
      super(server, executor, session, properties, worldKey, registryEntry, progressListener, chunkGenerator, isDebug, hashedSeed, spawners, shouldTickTime);
   }

   public ThreadingMode getThreadingMode() {
      return this.threadingMode;
   }

   public int getTickCount() {
      return this.tickCount;
   }

   public boolean isMarkedForDeletion() {
      return this.delete;
   }

   public VirtualWorld setThreadingMode(ThreadingMode threadingMode) {
      this.threadingMode = threadingMode;
      return this;
   }

   public void setRandomTickSpeed(int randomTickSpeed) {
      this.randomTickSpeed = randomTickSpeed;
   }

   public void markForDeletion() {
      this.delete = true;
   }

   public void swapThreadsAndRun(Thread thread, Runnable runnable) {
      String oldName = thread.getName();
      Thread[] old = new Thread[]{((AccessorWorld)this).getThread(), ((AccessorServerChunkCache)this.getChunkSource()).getThread()};
      thread.setName(this.dimension().location().toString());
      ((AccessorWorld)this).setThread(thread);
      ((AccessorServerChunkCache)this.getChunkSource()).setThread(thread);
      runnable.run();
      thread.setName(oldName);
      ((AccessorWorld)this).setThread(old[0]);
      ((AccessorServerChunkCache)this.getChunkSource()).setThread(old[1]);
   }

   public void tickChunk(LevelChunk chunk, int randomTickSpeed) {
      super.tickChunk(chunk, this.randomTickSpeed);
   }

   public void tick(BooleanSupplier hasTimeLeft) {
      super.tick(hasTimeLeft);
      this.tickCount++;
   }

   public static VirtualWorld create(ResourceLocation id, ThreadingMode mode) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      Executor executor = ((AccessorMinecraftServer)server).getWorkerExecutor();
      LevelStorageAccess session = ((AccessorMinecraftServer)server).getSession();
      ServerLevelData properties = new DerivedLevelData(server.getWorldData(), server.getWorldData().overworldData());
      ResourceKey<Level> worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, id);
      ChunkProgressListener progressListener = new DummyProgressListener();
      DimensionType dimension = DimensionType.create(
         OptionalLong.of(15000L),
         false,
         false,
         false,
         false,
         1.0,
         false,
         true,
         false,
         false,
         false,
         0,
         64,
         64,
         BlockTags.INFINIBURN_OVERWORLD,
         DimensionType.NETHER_EFFECTS,
         0.05F
      );
      ChunkGenerator chunkGenerator = new DummyChunkGenerator(
         server.registryAccess().registryOrThrow(Registry.STRUCTURE_SET_REGISTRY), server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
      );
      boolean isDebugWorld = false;
      long hashedSeed = 0L;
      List<CustomSpawner> spawners = new ArrayList<>();
      boolean shouldTickTime = false;
      VirtualWorld world = new VirtualWorld(
         server,
         executor,
         session,
         properties,
         worldKey,
         Holder.direct(dimension),
         progressListener,
         chunkGenerator,
         isDebugWorld,
         hashedSeed,
         spawners,
         shouldTickTime
      );
      world.setThreadingMode(mode);
      return world;
   }
}