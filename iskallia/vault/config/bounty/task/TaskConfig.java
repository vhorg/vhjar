package iskallia.vault.config.bounty.task;

import com.google.gson.annotations.Expose;
import iskallia.vault.bounty.TaskRegistry;
import iskallia.vault.bounty.task.properties.TaskProperties;
import iskallia.vault.config.Config;
import iskallia.vault.config.bounty.task.entry.TaskEntry;
import iskallia.vault.config.entry.LevelEntryMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public abstract class TaskConfig<E extends TaskEntry<?>, T extends TaskProperties> extends Config {
   @Expose
   protected LevelEntryMap<E> LEVELS = new LevelEntryMap();
   private static final Map<ResourceLocation, ? extends TaskConfig<?, ?>> TASK_CONFIGS = new HashMap<>();

   protected abstract E generateConfigEntry();

   public abstract T getGeneratedTaskProperties(int var1);

   @Override
   public String getName() {
      return "bounty/task/";
   }

   @Override
   protected void reset() {
      for (int i = 0; i < 30; i += 10) {
         this.LEVELS.put(Integer.valueOf(i), this.generateConfigEntry());
      }
   }

   protected E getEntry(int vaultLevel) {
      Optional<E> entry = this.LEVELS.getForLevel(vaultLevel);
      if (entry.isEmpty()) {
         throw new IllegalArgumentException("No entry found for the given level: " + vaultLevel);
      } else {
         return entry.get();
      }
   }

   public static Map<ResourceLocation, ? extends TaskConfig<?, ?>> getTaskConfigs() {
      return TASK_CONFIGS;
   }

   public static <T extends TaskConfig<?, ?>> T getConfig(ResourceLocation taskId) {
      return (T)TASK_CONFIGS.get(taskId);
   }

   public static void registerTaskConfigs() {
      TASK_CONFIGS.put(TaskRegistry.KILL_ENTITY, new KillEntityTaskConfig().readConfig());
      TASK_CONFIGS.put(TaskRegistry.DAMAGE_ENTITY, new DamageEntityTaskConfig().readConfig());
      TASK_CONFIGS.put(TaskRegistry.COMPLETION, new CompletionTaskConfig().readConfig());
      TASK_CONFIGS.put(TaskRegistry.ITEM_SUBMISSION, new ItemSubmissionTaskConfig().readConfig());
      TASK_CONFIGS.put(TaskRegistry.ITEM_DISCOVERY, new ItemDiscoveryTaskConfig().readConfig());
      TASK_CONFIGS.put(TaskRegistry.MINING, new MiningTaskConfig().readConfig());
   }
}
