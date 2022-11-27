package iskallia.vault.bounty;

import iskallia.vault.VaultMod;
import iskallia.vault.bounty.task.CompletionTask;
import iskallia.vault.bounty.task.DamageTask;
import iskallia.vault.bounty.task.ItemDiscoveryTask;
import iskallia.vault.bounty.task.ItemSubmissionTask;
import iskallia.vault.bounty.task.KillEntityTask;
import iskallia.vault.bounty.task.MiningTask;
import iskallia.vault.bounty.task.Task;
import iskallia.vault.bounty.task.properties.TaskProperties;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public class TaskRegistry {
   public static final HashMap<ResourceLocation, Class<? extends Task<?>>> TASKS = new HashMap<>();
   public static ResourceLocation KILL_ENTITY = register(VaultMod.id("kill_entity"), KillEntityTask.class);
   public static ResourceLocation DAMAGE_ENTITY = register(VaultMod.id("damage_entity"), DamageTask.class);
   public static ResourceLocation COMPLETION = register(VaultMod.id("completion"), CompletionTask.class);
   public static ResourceLocation ITEM_SUBMISSION = register(VaultMod.id("item_submission"), ItemSubmissionTask.class);
   public static ResourceLocation ITEM_DISCOVERY = register(VaultMod.id("item_discovery"), ItemDiscoveryTask.class);
   public static ResourceLocation MINING = register(VaultMod.id("mining"), MiningTask.class);

   private static <T extends Task<?>> ResourceLocation register(ResourceLocation id, Class<T> taskClass) {
      TASKS.put(id, taskClass);
      return id;
   }

   public static <T extends Task<P>, P extends TaskProperties> T createTask(ResourceLocation id, UUID bountyId, P properties, TaskReward reward) {
      try {
         return (T)TASKS.get(id).getConstructor(UUID.class, properties.getClass(), TaskReward.class).newInstance(bountyId, properties, reward);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var5) {
         throw new IllegalArgumentException("Unable to acquire a task based on the given parameters: " + id);
      }
   }
}
