package iskallia.vault.world.data;

import iskallia.vault.VaultMod;
import iskallia.vault.core.world.storage.ThreadPool;
import iskallia.vault.core.world.storage.ThreadingMode;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.mixin.AccessorMinecraftServer;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.network.message.WorldListUpdateMessage;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.BorderChangeListener.DelegateBorderChangeListener;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;

public class VirtualWorlds extends SavedData {
   protected static final String DATA_NAME = "the_vault_VirtualWorlds";
   protected static ThreadPool CONCURRENT_POOL = new ThreadPool(Runtime.getRuntime().availableProcessors());
   protected static List<Runnable> SCHEDULED_TASKS = new ArrayList<>();
   protected static AtomicReference<ReportedException> CRASH = new AtomicReference<>();
   private VListNBT<VirtualWorld, CompoundTag> entries = new VListNBT<>(world -> {
      CompoundTag nbtx = new CompoundTag();
      nbtx.putString("id", world.dimension().location().toString());
      nbtx.putInt("mode", world.getThreadingMode().ordinal());
      nbtx.putBoolean("delete", world.isMarkedForDeletion());
      return nbtx;
   }, nbtx -> {
      VirtualWorld world = VirtualWorld.create(new ResourceLocation(nbtx.getString("id")), ThreadingMode.values()[nbtx.getInt("mode")]);
      if (nbtx.getBoolean("delete")) {
         world.markForDeletion();
      }

      return world;
   });

   protected VirtualWorlds(CompoundTag nbt) {
      this.load(nbt);
   }

   protected VirtualWorlds() {
   }

   public boolean isDirty() {
      return true;
   }

   public static List<VirtualWorld> getAll() {
      return get(ServerLifecycleHooks.getCurrentServer()).entries;
   }

   private static VirtualWorld load(VirtualWorld world) {
      MinecraftServer server = world.getServer();
      ((AccessorMinecraftServer)server).getWorldsMap().put(world.dimension(), world);
      server.overworld().getWorldBorder().addListener(new DelegateBorderChangeListener(world.getWorldBorder()));
      MinecraftForge.EVENT_BUS.post(new Load(world));
      return world;
   }

   private static VirtualWorld unload(VirtualWorld world) {
      MinecraftServer server = world.getServer();
      ((AccessorMinecraftServer)server).getWorldsMap().remove(world.dimension());
      MinecraftForge.EVENT_BUS.post(new Unload(world));
      return world;
   }

   public static VirtualWorld register(VirtualWorld world) {
      load(world);
      world.getServer().markWorldsDirty();
      world.getServer()
         .getPlayerList()
         .getPlayers()
         .forEach(
            player -> {
               List<ResourceLocation> ids = ((AccessorMinecraftServer)world.getServer())
                  .getWorldsMap()
                  .keySet()
                  .stream()
                  .<ResourceLocation>map(ResourceKey::location)
                  .collect(Collectors.toList());
               ModNetwork.CHANNEL.sendTo(new WorldListUpdateMessage(ids), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }
         );
      get(world.getServer()).entries.add(world);
      return world;
   }

   public static VirtualWorld deregister(VirtualWorld world) {
      unload(world);
      world.getServer().markWorldsDirty();
      world.getServer()
         .getPlayerList()
         .getPlayers()
         .forEach(
            player -> {
               List<ResourceLocation> ids = ((AccessorMinecraftServer)world.getServer())
                  .getWorldsMap()
                  .keySet()
                  .stream()
                  .<ResourceLocation>map(ResourceKey::location)
                  .collect(Collectors.toList());
               ModNetwork.CHANNEL.sendTo(new WorldListUpdateMessage(ids), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }
         );
      get(world.getServer()).entries.remove(world);
      return world;
   }

   public static void tick(BooleanSupplier hasTimeLeft, Phase phase) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      switch (phase) {
         case START:
            tickDeletions(server, hasTimeLeft);

            for (Level anyWorldx : server.getAllLevels()) {
               if (anyWorldx instanceof VirtualWorld) {
                  VirtualWorld world = (VirtualWorld)anyWorldx;
                  switch (world.getThreadingMode()) {
                     case CONCURRENT:
                        CONCURRENT_POOL.execute(
                           () -> world.swapThreadsAndRun(Thread.currentThread(), () -> tickWorldSafe(server, world, hasTimeLeft, true, CRASH))
                        );
                        break;
                     case PARALLEL:
                        throw new UnsupportedOperationException();
                  }
               }
            }
            break;
         case END:
            for (Level anyWorld : server.getAllLevels()) {
               if (anyWorld instanceof VirtualWorld) {
                  VirtualWorld world = (VirtualWorld)anyWorld;
                  if (world.getThreadingMode() == ThreadingMode.SEQUENTIAL) {
                     tickWorldSafe(server, world, hasTimeLeft, false, CRASH);
                  }
               }
            }

            CONCURRENT_POOL.awaitCompletion();
            if (CRASH.get() != null) {
               SCHEDULED_TASKS.clear();
               ReportedException value = CRASH.get();
               CRASH.set(null);
               throw value;
            }

            SCHEDULED_TASKS.forEach(Runnable::run);
            SCHEDULED_TASKS.clear();
      }
   }

   private static void tickDeletions(MinecraftServer server, BooleanSupplier hasTimeLeft) {
      LevelStorageAccess session = ((AccessorMinecraftServer)server).getSession();

      for (VirtualWorld world : new ArrayList<>(get(server).entries)) {
         if (world.isMarkedForDeletion()) {
            deregister(world);
            final Path folder = session.getDimensionPath(world.dimension());
            VaultMod.LOGGER.info("Deleting level {}", folder);

            try {
               Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                  public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) throws IOException {
                     VaultMod.LOGGER.debug("Deleting {}", path);
                     Files.delete(path);
                     return FileVisitResult.CONTINUE;
                  }

                  public FileVisitResult postVisitDirectory(Path path, IOException exception) throws IOException {
                     if (exception != null) {
                        throw exception;
                     } else {
                        if (path.equals(folder)) {
                           Files.deleteIfExists(path);
                        }

                        Files.delete(path);
                        return FileVisitResult.CONTINUE;
                     }
                  }
               });
            } catch (IOException var7) {
            }
            break;
         }
      }
   }

   private static void tickWorldSafe(
      MinecraftServer server, VirtualWorld world, BooleanSupplier hasTimeLeft, boolean deferEvents, AtomicReference<ReportedException> crash
   ) {
      try {
         tickWorld(server, world, hasTimeLeft, deferEvents);
      } catch (Throwable var7) {
         CrashReport crashreport = CrashReport.forThrowable(var7, "Exception ticking world");
         world.fillReportDetails(crashreport);
         crash.set(new ReportedException(crashreport));
      }
   }

   private static void tickWorld(MinecraftServer server, VirtualWorld world, BooleanSupplier hasTimeLeft, boolean deferEvents) {
      long tickStart = Util.getNanos();
      if (!deferEvents) {
         ForgeEventFactory.onPreWorldTick(world, hasTimeLeft);
      } else {
         SCHEDULED_TASKS.add(() -> ForgeEventFactory.onPreWorldTick(world, hasTimeLeft));
      }

      try {
         world.tick(hasTimeLeft);
      } catch (Throwable var8) {
         CrashReport crashreport = CrashReport.forThrowable(var8, "Exception ticking world");
         world.fillReportDetails(crashreport);
         throw new ReportedException(crashreport);
      }

      if (!deferEvents) {
         ForgeEventFactory.onPostWorldTick(world, hasTimeLeft);
      } else {
         SCHEDULED_TASKS.add(() -> ForgeEventFactory.onPostWorldTick(world, hasTimeLeft));
      }

      ((AccessorMinecraftServer)server).getPerWorldTickTimes().computeIfAbsent(world.dimension(), k -> new long[100])[world.getTickCount() % 100] = Util.getNanos()
         - tickStart;
   }

   public static void load() {
      get(ServerLifecycleHooks.getCurrentServer()).entries.forEach(VirtualWorlds::load);
   }

   public CompoundTag save(CompoundTag nbt) {
      nbt.put("entries", this.entries.serializeNBT());
      return nbt;
   }

   public void load(CompoundTag nbt) {
      this.entries.deserializeNBT(nbt.getList("entries", 10));
   }

   private static VirtualWorlds get(MinecraftServer server) {
      return (VirtualWorlds)server.overworld().getDataStorage().computeIfAbsent(VirtualWorlds::new, VirtualWorlds::new, "the_vault_VirtualWorlds");
   }
}
