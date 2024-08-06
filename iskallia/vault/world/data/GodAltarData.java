package iskallia.vault.world.data;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.crystal.data.serializable.INbtSerializable;
import iskallia.vault.network.message.UpdateGodAltarDataMessage;
import iskallia.vault.task.GodAltarTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class GodAltarData extends SavedData {
   protected static final String DATA_NAME = "the_vault_GodAltars";
   public static List<GodAltarData.Entry> CLIENT = new ArrayList<>();
   private final Map<UUID, GodAltarData.Entry> entries = new HashMap<>();

   public static Optional<GodAltarData.Entry> get(UUID uuid) {
      return Optional.ofNullable(get().entries.get(uuid));
   }

   public static UUID add(UUID uuid, Task task, TaskSource source, MinecraftServer server) {
      GodAltarData data = get();
      data.entries.put(uuid, new GodAltarData.Entry(task, source));
      task.onAttach(TaskContext.of(source, server));
      return uuid;
   }

   public static void remove(UUID uuid) {
      GodAltarData data = get();
      GodAltarData.Entry entry = data.entries.remove(uuid);
      if (entry != null) {
         entry.task.onDetach();
      }
   }

   public static boolean contains(Entity entity) {
      for (GodAltarData.Entry entry : get().entries.values()) {
         if (entry.source instanceof EntityTaskSource entitySource && entitySource.matches(entity)) {
            return true;
         }
      }

      return false;
   }

   @SubscribeEvent
   public static void onServerStarted(ServerStartedEvent event) {
      get().entries.forEach((id, entry) -> entry.task.onAttach(TaskContext.of(entry.source, event.getServer())));
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      get().entries.forEach((id, entry) -> entry.task.onDetach());
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         List<GodAltarData.Entry> tasks = new ArrayList<>();

         for (GodAltarData.Entry value : get().entries.values()) {
            TaskSource var6 = value.source;
            if (var6 instanceof EntityTaskSource) {
               EntityTaskSource entitySource = (EntityTaskSource)var6;
               if (entitySource.matches(player)) {
                  tasks.add(value);
               }
            }
         }

         ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateGodAltarDataMessage(tasks));
      }
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         for (GodAltarData.Entry entry : new ArrayList<>(get().entries.values())) {
            Task tasks = entry.task;
            if (tasks instanceof GodAltarTask) {
               GodAltarTask root = (GodAltarTask)tasks;
               if (root.isExpired()) {
                  remove(root.getAltarUuid());
               }
            }
         }

         MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

         for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            List<GodAltarData.Entry> tasks = new ArrayList<>();

            for (GodAltarData.Entry value : get().entries.values()) {
               TaskSource var8 = value.source;
               if (var8 instanceof EntityTaskSource) {
                  EntityTaskSource entitySource = (EntityTaskSource)var8;
                  if (entitySource.matches(player)) {
                     tasks.add(value);
                  }
               }
            }

            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateGodAltarDataMessage(tasks));
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateGodAltarDataMessage(new ArrayList<>()));
      }
   }

   public static boolean contains(UUID uuid) {
      return get().entries.containsKey(uuid);
   }

   @Nonnull
   public CompoundTag save(CompoundTag nbt) {
      this.entries.forEach((uuid, entry) -> entry.writeNbt().ifPresent(entryNbt -> nbt.put(uuid.toString(), entryNbt)));
      return nbt;
   }

   private static GodAltarData load(CompoundTag nbt) {
      GodAltarData data = new GodAltarData();
      nbt.getAllKeys().forEach(key -> {
         GodAltarData.Entry entry = new GodAltarData.Entry();
         entry.readNbt(nbt.getCompound(key));
         data.entries.put(UUID.fromString(key), entry);
      });
      return data;
   }

   public boolean isDirty() {
      return true;
   }

   public static GodAltarData get() {
      return (GodAltarData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(GodAltarData::load, GodAltarData::new, "the_vault_GodAltars");
   }

   public static class Entry implements INbtSerializable<CompoundTag> {
      private Task task;
      private TaskSource source;

      public Entry() {
      }

      public Entry(Task task, TaskSource source) {
         this.task = task;
         this.source = source;
      }

      public Task getTask() {
         return this.task;
      }

      public TaskSource getSource() {
         return this.source;
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         Adapters.TASK.writeNbt(this.task).ifPresent(task -> nbt.put("task", task));
         Adapters.TASK_SOURCE.writeNbt(this.source).ifPresent(source -> nbt.put("source", source));
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         this.task = Adapters.TASK.readNbt(nbt.get("task")).orElse(null);
         this.source = Adapters.TASK_SOURCE.readNbt(nbt.get("source")).orElse(null);
      }
   }
}
