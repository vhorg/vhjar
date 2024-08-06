package iskallia.vault.world.data;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.UpdateAchievementDataMessage;
import iskallia.vault.task.NodeTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class AchievementData extends SavedData {
   protected static final String DATA_NAME = "the_vault_Achievements";
   private final Map<UUID, Task> entries = new HashMap<>();
   public static Task CLIENT_ACHIEVEMENTS;

   public void initialize(ServerPlayer player) {
      Task achievements = ModConfigs.ACHIEVEMENT.getAchievements();
      if (achievements != null) {
         this.entries.put(player.getUUID(), achievements.copy());
         Task task = this.entries.get(player.getUUID());
         task.onAttach(TaskContext.of(EntityTaskSource.ofUuids(JavaRandom.ofNanoTime(), player.getUUID()), player.getServer()));
         this.setDirty();
      }
   }

   public void setDirty() {
   }

   public Optional<Task> getTask(Player player) {
      return Optional.ofNullable(this.entries.get(player.getUUID()));
   }

   @SubscribeEvent
   public static void onServerStarted(ServerStartedEvent event) {
      get().entries.forEach((uuid, task) -> task.onAttach(TaskContext.of(EntityTaskSource.ofUuids(JavaRandom.ofNanoTime(), uuid), event.getServer())));
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END && !get().entries.isEmpty()) {
         MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
         get().setDirty();
         if (server.getTickCount() % 20 == 0) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
               get()
                  .getTask(player)
                  .ifPresent(value -> ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateAchievementDataMessage(value)));
            }
         }
      }
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      get().entries.values().forEach(Task::onDetach);
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         get().initialize(player);
         get()
            .getTask(player)
            .ifPresent(value -> ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new UpdateAchievementDataMessage(value)));
      }
   }

   @NotNull
   public CompoundTag save(@NotNull CompoundTag tag) {
      this.entries.forEach((uuid, task) -> tag.put(uuid.toString(), Adapters.TASK.writeNbt(task).orElse(new CompoundTag())));
      return tag;
   }

   private static AchievementData load(CompoundTag tag) {
      AchievementData data = new AchievementData();

      for (String key : tag.getAllKeys()) {
         UUID id = UUID.fromString(key);
         CompoundTag taskTag = tag.getCompound(key);
         NodeTask task = (NodeTask)Adapters.TASK.readNbt(taskTag).orElseThrow();
         data.entries.put(id, task);
      }

      return data;
   }

   public static AchievementData get() {
      return (AchievementData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(AchievementData::load, AchievementData::new, "the_vault_Achievements");
   }
}
