package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.source.EntityTaskSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.nbt.CompoundTag;

public class PlayerDividedTask extends NodeTask implements ResettingTask, RepeatingTask {
   private final Map<UUID, List<Task>> tasks = new HashMap<>();

   @Override
   public Iterable<Task> getChildren() {
      return new ArrayList<>();
   }

   @Override
   public boolean isCompleted() {
      return this.tasks.values().stream().anyMatch(tasks -> {
         for (Task task : tasks) {
            if (!task.streamSelfAndDescendants().allMatch(Task::isCompleted)) {
               return false;
            }
         }

         return true;
      });
   }

   @Override
   public boolean hasActiveChildren() {
      return this.parent == null || this.parent.hasActiveChildren();
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.SERVER_TICK.register(this, event -> {
         if (context.getSource() instanceof EntityTaskSource source) {
            for (UUID uuid : source.getUuids()) {
               if (!this.tasks.containsKey(uuid)) {
                  TaskContext newContext = this.newContext(context, uuid);
                  List<Task> tasks = new ArrayList<>();

                  for (Task task : this.children) {
                     task = task.copy();
                     task.parent = this;
                     tasks.add(task);
                     task.onAttach(newContext);
                  }

                  this.tasks.put(uuid, tasks);
               }
            }

            this.tasks.entrySet().removeIf(entry -> {
               UUID uuidx = entry.getKey();
               List<Task> tasksx = entry.getValue();
               if (source.getUuids().contains(uuidx)) {
                  return false;
               } else {
                  for (Task taskx : tasksx) {
                     taskx.onDetach();
                  }

                  return true;
               }
            });
         }
      });
      this.tasks.forEach((uuid, tasks) -> {
         TaskContext newContext = this.newContext(context, uuid);

         for (Task task : tasks) {
            task.onAttach(newContext);
         }
      });
   }

   @Override
   public void onDetach() {
      this.tasks.forEach((uuid, tasks) -> {
         for (Task task : tasks) {
            task.onDetach();
         }
      });
      super.onDetach();
   }

   @Override
   public void onReset(TaskContext context) {
      this.tasks.entrySet().removeIf(entry -> {
         for (Task task : entry.getValue()) {
            task.onDetach();
         }

         return true;
      });
   }

   @Override
   public void onRepeat(TaskContext context) {
      Iterator<Entry<UUID, List<Task>>> it = this.tasks.entrySet().iterator();

      label30:
      while (it.hasNext()) {
         Entry<UUID, List<Task>> entry = it.next();

         for (Task task : entry.getValue()) {
            if (!task.streamSelfAndDescendants().allMatch(Task::isCompleted)) {
               continue label30;
            }
         }

         for (Task child : entry.getValue()) {
            child.onDetach();
         }

         it.remove();
         break;
      }
   }

   public TaskContext newContext(TaskContext context, UUID uuid) {
      TaskContext copy = context.copy();
      if (copy.getSource() instanceof EntityTaskSource source) {
         source.getUuids().clear();
         source.getUuids().add(uuid);
      }

      return copy;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.tasks.size()), buffer);
      this.tasks.forEach((uuid, tasks) -> {
         Adapters.UUID.writeBits(uuid, buffer);
         CHILDREN.writeBits(tasks.toArray(Task[]::new), buffer);
      });
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      int size = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.tasks.clear();

      for (int i = 0; i < size; i++) {
         UUID uuid = Adapters.UUID.readBits(buffer).orElseThrow();
         List<Task> tasks = new ArrayList<>(Arrays.asList(CHILDREN.readBits(buffer).orElseThrow()));
         tasks.forEach(task -> task.parent = this);
         this.tasks.put(uuid, tasks);
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         CompoundTag tasksTag = new CompoundTag();
         this.tasks.forEach((uuid, tasks) -> CHILDREN.writeNbt(tasks.toArray(Task[]::new)).ifPresent(tag -> tasksTag.put(uuid.toString(), tag)));
         nbt.put("tasks", tasksTag);
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.tasks.clear();
      CompoundTag tasksTag = nbt.getCompound("tasks");

      for (String key : tasksTag.getAllKeys()) {
         List<Task> tasks = new ArrayList<>(Arrays.asList(CHILDREN.readNbt(tasksTag.get(key)).orElse(new Task[0])));
         tasks.forEach(task -> task.parent = this);
         this.tasks.put(UUID.fromString(key), tasks);
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         JsonObject tasksTag = new JsonObject();
         this.tasks.forEach((uuid, tasks) -> CHILDREN.writeJson(tasks.toArray(Task[]::new)).ifPresent(tag -> tasksTag.add(uuid.toString(), tag)));
         json.add("tasks", tasksTag);
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.tasks.clear();
      if (json.get("tasks") instanceof JsonObject tasksTag) {
         for (String key : tasksTag.keySet()) {
            List<Task> tasks = new ArrayList<>(Arrays.asList(CHILDREN.readJson(tasksTag.get(key)).orElse(new Task[0])));
            tasks.forEach(task -> task.parent = this);
            this.tasks.put(UUID.fromString(key), tasks);
         }
      }
   }
}
