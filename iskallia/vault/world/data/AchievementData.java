package iskallia.vault.world.data;

import iskallia.vault.task.Task;
import iskallia.vault.task.TaskContext;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class AchievementData extends SavedData {
   protected static final String DATA_NAME = "the_vault_Achievements";
   public static Task CLIENT;
   private final List<AchievementData.Entry> entries = new ArrayList<>();

   @SubscribeEvent
   public static void onServerStarted(ServerStartedEvent event) {
      get().entries.forEach(entry -> {
         entry.getContext().setServer(event.getServer());
         entry.task.onAttach(entry.getContext());
      });
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      get().entries.forEach(entry -> entry.task.onDetach());
   }

   @Nonnull
   public CompoundTag save(CompoundTag nbt) {
      return nbt;
   }

   private static AchievementData load(CompoundTag nbt) {
      return new AchievementData();
   }

   public static AchievementData get() {
      return (AchievementData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(AchievementData::load, AchievementData::new, "the_vault_Achievements");
   }

   public static class Entry {
      private Task task;
      private TaskContext context;

      public Entry() {
      }

      public Entry(Task task, TaskContext context) {
         this.task = task;
         this.context = context;
      }

      public Task getTask() {
         return this.task;
      }

      public TaskContext getContext() {
         return this.context;
      }
   }
}
