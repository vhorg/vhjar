package iskallia.vault.core.vault.enhancement;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE
)
public class EnhancementData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerEnhancements";
   protected List<EnhancementTask<?>> tasks = new ArrayList<>();

   public static List<EnhancementTask<?>> get(Player player) {
      return get().tasks.stream().filter(task -> task.belongsTo(player)).collect(Collectors.toList());
   }

   public static void add(EnhancementTask<?> task) {
      get().tasks.add(task);
      task.initServer(ServerLifecycleHooks.getCurrentServer());
   }

   public static void remove(Vault vault, UUID player) {
      get().tasks.removeIf(task -> {
         if (task.belongsTo(vault) && task.belongsTo(player)) {
            task.releaseServer();
            return true;
         } else {
            return false;
         }
      });
   }

   public static Map<UUID, EnhancementTask<?>> getForAltar(UUID uuid) {
      Map<UUID, EnhancementTask<?>> result = new HashMap<>();

      for (EnhancementTask<?> task : get().tasks) {
         if (task.getAltar().equals(uuid)) {
            result.put(task.getPlayer(), task);
         }
      }

      return result;
   }

   @SubscribeEvent
   public static void onStartTick(ServerStartedEvent event) {
      EnhancementData enhancements = get();
      enhancements.tasks.forEach(task -> task.initServer(event.getServer()));
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         EnhancementData enhancements = get();
         enhancements.tasks.removeIf(enhancement -> {
            if (ServerVaults.get(enhancement.getVault()).isEmpty()) {
               enhancement.releaseServer();
               return true;
            } else {
               return false;
            }
         });
      }
   }

   @SubscribeEvent
   public static void onServerStop(ServerStoppedEvent event) {
      get().tasks.forEach(EnhancementTask::releaseServer);
   }

   public boolean isDirty() {
      return true;
   }

   @Nonnull
   public CompoundTag save(CompoundTag nbt) {
      ListTag list = new ListTag();

      for (EnhancementTask<?> task : this.tasks) {
         Adapters.ENHANCEMENT_TASK.writeNbt(task).ifPresent(list::add);
      }

      nbt.put("tasks", list);
      return nbt;
   }

   private static EnhancementData load(CompoundTag nbt) {
      EnhancementData data = new EnhancementData();
      ListTag list = nbt.getList("tasks", 10);

      for (int i = 0; i < list.size(); i++) {
         Adapters.ENHANCEMENT_TASK.readNbt(list.getCompound(i)).ifPresent(data.tasks::add);
      }

      return data;
   }

   public static EnhancementData get() {
      return (EnhancementData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(EnhancementData::load, EnhancementData::new, "the_vault_PlayerEnhancements");
   }
}
