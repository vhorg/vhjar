package iskallia.vault.task;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.task.renderer.BingoRenderer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class BingoTask extends ConfiguredTask<BingoTask.Config> implements LevelEntryList.ILevelEntry {
   private Task[] tasks;
   private boolean[] settledTasks;
   private boolean[] settledBingos;
   private List<Task> rewards = new ArrayList<>();
   private Map<UUID, Integer> selectedBingoIndex = new HashMap<>();
   private static final ArrayAdapter<Task> CHILDREN = Adapters.ofArray(Task[]::new, Adapters.TASK);

   public BingoTask() {
      super(new BingoTask.Config());
   }

   public BingoTask(BingoTask.Config config) {
      super(config);
   }

   @Override
   public int getLevel() {
      return this.getConfig().level;
   }

   public int getWidth() {
      return this.getConfig().width;
   }

   public int getHeight() {
      return this.getConfig().height;
   }

   public int getIndex(int row, int column) {
      return row * this.getWidth() + column;
   }

   public Task getChild(int index) {
      return this.tasks[index];
   }

   public Task getChild(int row, int column) {
      return this.getChild(this.getIndex(row, column));
   }

   public void setChild(int index, NodeTask task) {
      this.tasks[index] = task;
   }

   public void setChild(int row, int column, NodeTask task) {
      this.setChild(this.getIndex(row, column), task);
   }

   public BingoTask.State getState(int index) {
      return this.getState(index / this.getWidth(), index % this.getWidth());
   }

   public BingoTask.State getState(int row, int column) {
      if (!this.isCompleted(row, column)) {
         return BingoTask.State.INCOMPLETE;
      } else if (this.isBingo(row)) {
         return BingoTask.State.BINGO;
      } else if (this.isBingo(this.getHeight() + column)) {
         return BingoTask.State.BINGO;
      } else {
         if (this.getWidth() == this.getHeight()) {
            if (row == column && this.isBingo(this.getWidth() + this.getHeight())) {
               return BingoTask.State.BINGO;
            }

            if (this.getHeight() - row - 1 == column && this.isBingo(this.getWidth() + this.getHeight() + 1)) {
               return BingoTask.State.BINGO;
            }
         }

         return BingoTask.State.COMPLETE;
      }
   }

   public boolean isCompleted(int index) {
      return this.settledTasks[index];
   }

   public boolean isCompleted(int row, int column) {
      return this.isCompleted(this.getIndex(row, column));
   }

   public boolean areAllCompleted() {
      for (int row = 0; row < this.getConfig().height; row++) {
         for (int column = 0; column < this.getConfig().width; column++) {
            if (!this.isCompleted(row, column)) {
               return false;
            }
         }
      }

      for (Task reward : this.rewards) {
         if (!reward.isCompleted()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public Iterable<Task> getChildren() {
      return Iterables.concat(Arrays.asList(this.tasks), this.rewards);
   }

   @Override
   public void onPopulate(TaskContext context) {
      this.tasks = new NodeTask[this.getWidth() * this.getHeight()];
      WeightedList<Task> pool = this.getConfig().getTasks(context);
      pool.keySet().forEach(task -> task.getSelfAndChildren().forEach(subTask -> {
         if (subTask instanceof ProgressConfiguredTask<?, ?> progressTask) {
            progressTask.getCounter().populate(context);
         }
      }));
      pool.keySet()
         .removeIf(
            task -> task instanceof ProgressConfiguredTask<?, ?> progressTask
               && progressTask.getCondition() != null
               && !progressTask.getCondition().isConditionFulfilled(progressTask, context)
         );
      int count = this.getWidth() * this.getHeight();
      List<Task> tasks = new ArrayList<>();

      while (tasks.size() < count) {
         if (tasks.size() + pool.size() < count) {
            tasks.addAll(pool.keySet());
         } else {
            pool.getRandom(context.getSource().getRandom()).ifPresent(key -> {
               tasks.add(key);
               pool.remove(key);
            });
         }
      }

      for (int i = tasks.size() - 1; i > 0; i--) {
         tasks.set(i, tasks.set(context.getSource().getRandom().nextInt(i + 1), tasks.get(i)));
      }

      for (int index = 0; index < count; index++) {
         this.setChild(index, tasks.get(index).copy());
      }

      this.settledTasks = new boolean[this.tasks.length];
      this.settledBingos = new boolean[this.getMaxBingos()];
      this.rewards = new ArrayList<>();
   }

   @Override
   public void onTick(TaskContext context) {
      super.onTick(context);

      for (int index = 0; index < this.getWidth() * this.getHeight(); index++) {
         boolean completed = this.getChild(index).streamSelfAndDescendants().allMatch(Task::isCompleted);
         boolean settled = this.settledTasks[index];
         Task task = this.getChild(index);
         if (completed && !settled) {
            this.onComplete(task, context);
            this.settledTasks[index] = true;
            task.onDetach();
         }
      }

      for (int lineIndex = 0; lineIndex < this.getMaxBingos(); lineIndex++) {
         if (this.isBingo(lineIndex) && !this.settledBingos[lineIndex]) {
            this.onBingo(context);
            this.settledBingos[lineIndex] = true;
         }
      }
   }

   private void onBingo(TaskContext context) {
      WeightedList<Task> pool = this.getCompletedBingos() == 0 ? this.getConfig().getFirstBingo(context) : this.getConfig().getSubsequentBingo(context);
      pool.getRandom(context.getSource().getRandom()).ifPresent(task -> {
         task = task.copy();
         this.rewards.add(task);
         task.onAttach(context);
      });
   }

   private void onComplete(Task task, TaskContext context) {
      String name = task.streamSelfAndDescendants()
         .flatMap(child -> child.getRenderer() instanceof BingoRenderer.Leaf renderer ? Stream.of(renderer.name) : Stream.empty())
         .findFirst()
         .orElse("Unknown");

      for (Listener listener : context.getVault().get(Vault.LISTENERS).getAll()) {
         listener.getPlayer()
            .ifPresent(
               player -> {
                  player.displayClientMessage(
                     new TextComponent("")
                        .append(new TextComponent("Completed ").withStyle(ChatFormatting.GRAY))
                        .append(name)
                        .append(new TextComponent("!").withStyle(ChatFormatting.GRAY)),
                     false
                  );
                  player.level
                     .playSound(null, player, SoundEvents.NOTE_BLOCK_CHIME, SoundSource.MASTER, 0.75F, 0.75F + player.level.getRandom().nextFloat() * 0.25F);
               }
            );
      }
   }

   public int getCompletedBingos() {
      int count = 0;

      for (boolean value : this.settledBingos) {
         count += value ? 1 : 0;
      }

      return count;
   }

   public int getMaxBingos() {
      return this.getHeight() + this.getWidth() + (this.getWidth() == this.getHeight() ? 2 : 0);
   }

   public boolean isBingo(int lineIndex) {
      for (int index : this.getLine(lineIndex)) {
         if (!this.isCompleted(index)) {
            return false;
         }
      }

      return true;
   }

   public int[] getSelectedLine(UUID uuid) {
      return this.getLine(this.selectedBingoIndex.getOrDefault(uuid, 0));
   }

   public int[] getLine(int lineIndex) {
      if (lineIndex < this.getHeight()) {
         int[] bingo = new int[this.getWidth()];

         for (int column = 0; column < this.getWidth(); column++) {
            bingo[column] = this.getIndex(lineIndex, column);
         }

         return bingo;
      } else if ((lineIndex = lineIndex - this.getHeight()) >= this.getWidth()) {
         if (this.getWidth() == this.getHeight() && (lineIndex = lineIndex - this.getWidth()) < 2) {
            int[] bingo = new int[this.getWidth()];

            for (int index = 0; index < this.getWidth(); index++) {
               if (lineIndex == 0) {
                  bingo[index] = this.getIndex(index, index);
               } else if (lineIndex == 1) {
                  bingo[index] = this.getIndex(this.getHeight() - index - 1, index);
               }
            }

            return bingo;
         } else {
            throw new UnsupportedOperationException();
         }
      } else {
         int[] bingo = new int[this.getHeight()];

         for (int row = 0; row < this.getHeight(); row++) {
            bingo[row] = this.getIndex(row, lineIndex);
         }

         return bingo;
      }
   }

   public void progressBingoLine(UUID uuid, int delta) {
      this.selectedBingoIndex.put(uuid, Math.floorMod(this.selectedBingoIndex.getOrDefault(uuid, 0) + delta, this.getMaxBingos()));
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      if (this.isPopulated()) {
         CHILDREN.writeBits(this.tasks, buffer);
         Adapters.BOOLEAN_ARRAY.writeBits(this.settledTasks, buffer);
         Adapters.BOOLEAN_ARRAY.writeBits(this.settledBingos, buffer);
         CHILDREN.writeBits(this.rewards.toArray(Task[]::new), buffer);
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.selectedBingoIndex.size()), buffer);
         this.selectedBingoIndex.forEach((uuid, index) -> {
            Adapters.UUID.writeBits(uuid, buffer);
            Adapters.INT_SEGMENTED_7.writeBits(index, buffer);
         });
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      if (this.isPopulated()) {
         this.tasks = CHILDREN.readBits(buffer).orElseThrow();
         this.settledTasks = Adapters.BOOLEAN_ARRAY.readBits(buffer).orElseThrow();
         this.settledBingos = Adapters.BOOLEAN_ARRAY.readBits(buffer).orElseThrow();
         this.rewards.addAll(Arrays.asList(CHILDREN.readBits(buffer).orElse(new Task[0])));
         this.selectedBingoIndex.clear();
         int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

         for (int i = 0; i < size; i++) {
            this.selectedBingoIndex.put(Adapters.UUID.readBits(buffer).orElseThrow(), Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow());
         }
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         if (!this.isPopulated()) {
            return (CompoundTag)nbt;
         } else {
            CHILDREN.writeNbt(this.tasks).ifPresent(value -> nbt.put("tasks", value));
            Adapters.BOOLEAN_ARRAY.writeNbt(this.settledTasks).ifPresent(value -> nbt.put("settledTasks", value));
            Adapters.BOOLEAN_ARRAY.writeNbt(this.settledBingos).ifPresent(value -> nbt.put("settledBingos", value));
            CHILDREN.writeNbt(this.rewards.toArray(Task[]::new)).ifPresent(value -> nbt.put("rewards", value));
            CompoundTag selectedBingoIndex = new CompoundTag();
            this.selectedBingoIndex.forEach((uuid, index) -> Adapters.INT.writeNbt(index).ifPresent(tag -> selectedBingoIndex.put(uuid.toString(), tag)));
            nbt.put("selectedBingoIndex", selectedBingoIndex);
            return (CompoundTag)nbt;
         }
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      if (this.isPopulated()) {
         this.tasks = CHILDREN.readNbt(nbt.get("tasks")).orElse(new Task[0]);
         this.settledTasks = Adapters.BOOLEAN_ARRAY.readNbt(nbt.get("settledTasks")).orElse(new boolean[0]);
         this.settledBingos = Adapters.BOOLEAN_ARRAY.readNbt(nbt.get("settledBingos")).orElse(new boolean[0]);
         Arrays.stream(this.tasks).forEach(child -> child.parent = this);
         this.rewards.addAll(Arrays.asList(CHILDREN.readNbt(nbt.get("rewards")).orElse(new Task[0])));
         this.rewards.forEach(child -> child.parent = this);
         this.selectedBingoIndex.clear();
         CompoundTag selectedBingoIndex = nbt.getCompound("selectedBingoIndex");

         for (String key : selectedBingoIndex.getAllKeys()) {
            Adapters.INT.readNbt(selectedBingoIndex.get(key)).ifPresent(index -> this.selectedBingoIndex.put(UUID.fromString(key), index));
         }
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         if (!this.isPopulated()) {
            return (JsonObject)json;
         } else {
            CHILDREN.writeJson(this.tasks).ifPresent(value -> json.add("tasks", value));
            CHILDREN.writeJson(this.rewards.toArray(Task[]::new)).ifPresent(value -> json.add("rewards", value));
            return (JsonObject)json;
         }
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      if (this.isPopulated()) {
         this.tasks = CHILDREN.readJson(json.get("tasks")).orElse(new Task[0]);
         Arrays.stream(this.tasks).forEach(child -> child.parent = this);
         this.rewards.addAll(Arrays.asList(CHILDREN.readJson(json.get("rewards")).orElse(new Task[0])));
         this.rewards.forEach(child -> child.parent = this);
      }
   }

   public static class Config extends ConfiguredTask.Config {
      private int level;
      private int width;
      private int height;
      private final Map<Integer, WeightedList<Task>> tasks;
      private final Map<Integer, WeightedList<Task>> firstBingo;
      private final Map<Integer, WeightedList<Task>> subsequentBingo;

      public Config() {
         this.tasks = new HashMap<>();
         this.firstBingo = new HashMap<>();
         this.subsequentBingo = new HashMap<>();
      }

      public Config(int width, int height) {
         this.width = width;
         this.height = height;
         this.tasks = new HashMap<>();
         this.firstBingo = new HashMap<>();
         this.subsequentBingo = new HashMap<>();
      }

      private WeightedList<Task> getTasks(TaskContext context) {
         return this.getPool(this.tasks, context);
      }

      private WeightedList<Task> getFirstBingo(TaskContext context) {
         return this.getPool(this.firstBingo, context);
      }

      private WeightedList<Task> getSubsequentBingo(TaskContext context) {
         return this.getPool(this.subsequentBingo, context);
      }

      private WeightedList<Task> getPool(Map<Integer, WeightedList<Task>> map, TaskContext context) {
         WeightedList<Task> result = new WeightedList<>();
         map.forEach((level, pool) -> {
            if (level <= context.getLevel()) {
               result.putAll(pool);
            }
         });
         return result;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.level), buffer);
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.width), buffer);
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.height), buffer);
         Stream.of(this.tasks, this.firstBingo, this.subsequentBingo).forEach(map -> {
            Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(map.size()), buffer);
            map.forEach((level, pool) -> {
               Adapters.INT_SEGMENTED_7.writeBits(level, buffer);
               Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(pool.size()), buffer);
               pool.forEach((task, weight) -> {
                  Adapters.TASK.writeBits(task, buffer);
                  Adapters.DOUBLE.writeBits(weight, buffer);
               });
            });
         });
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.level = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
         this.width = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
         this.height = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
         Stream.of(this.tasks, this.firstBingo, this.subsequentBingo).forEach(map -> {
            map.clear();
            int mapSize = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();

            for (int i = 0; i < mapSize; i++) {
               int level = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
               int poolSize = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
               WeightedList<Task> pool = new WeightedList<>();

               for (int j = 0; j < poolSize; j++) {
                  pool.add(Adapters.TASK.readBits(buffer).orElseThrow(), Adapters.DOUBLE.readBits(buffer).orElseThrow());
               }

               map.put(level, pool);
            }
         });
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.INT.writeNbt(Integer.valueOf(this.level)).ifPresent(tag -> nbt.put("level", tag));
            Adapters.INT.writeNbt(Integer.valueOf(this.width)).ifPresent(tag -> nbt.put("width", tag));
            Adapters.INT.writeNbt(Integer.valueOf(this.height)).ifPresent(tag -> nbt.put("height", tag));
            Stream.of(this.tasks, this.firstBingo, this.subsequentBingo).forEach(map -> {
               ListTag list = new ListTag();
               map.forEach((level, pool) -> pool.forEach((task, weight) -> Adapters.TASK.writeNbt(task).ifPresent(tag -> {
                  if (tag instanceof CompoundTag compound) {
                     Adapters.INT.writeNbt(level).ifPresent(t -> compound.put("level", t));
                     Adapters.DOUBLE.writeNbt(weight).ifPresent(t -> compound.put("weight", t));
                     list.add(tag);
                  }
               })));
               nbt.put(map == this.tasks ? "tasks" : (map == this.firstBingo ? "firstBingo" : "subsequentBingo"), list);
            });
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.level = Adapters.INT.readNbt(nbt.get("level")).orElseThrow();
         this.width = Adapters.INT.readNbt(nbt.get("width")).orElseThrow();
         this.height = Adapters.INT.readNbt(nbt.get("height")).orElseThrow();
         Stream.of(this.tasks, this.firstBingo, this.subsequentBingo).forEach(map -> {
            map.clear();

            for (Tag element : nbt.getList(map == this.tasks ? "tasks" : (map == this.firstBingo ? "firstBingo" : "subsequentBingo"), 10)) {
               if (element instanceof CompoundTag compound) {
                  Adapters.TASK.readNbt(element).ifPresent(task -> {
                     int level = Adapters.INT.readNbt(compound.get("level")).orElse(0);
                     double weight = Adapters.DOUBLE.readNbt(compound.get("weight")).orElse(1.0);
                     map.computeIfAbsent(level, i -> new WeightedList<>()).add(task, weight);
                  });
               }
            }
         });
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.INT.writeJson(Integer.valueOf(this.level)).ifPresent(tag -> json.add("level", tag));
            Adapters.INT.writeJson(Integer.valueOf(this.width)).ifPresent(tag -> json.add("width", tag));
            Adapters.INT.writeJson(Integer.valueOf(this.height)).ifPresent(tag -> json.add("height", tag));
            Stream.of(this.tasks, this.firstBingo, this.subsequentBingo).forEach(map -> {
               JsonArray list = new JsonArray();
               map.forEach((level, pool) -> pool.forEach((task, weight) -> Adapters.TASK.writeJson(task).ifPresent(tag -> {
                  if (tag instanceof JsonObject object) {
                     Adapters.INT.writeJson(level).ifPresent(t -> object.add("level", t));
                     Adapters.DOUBLE.writeJson(weight).ifPresent(t -> object.add("weight", t));
                     list.add(tag);
                  }
               })));
               json.add(map == this.tasks ? "tasks" : (map == this.firstBingo ? "firstBingo" : "subsequentBingo"), list);
            });
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.level = Adapters.INT.readJson(json.get("level")).orElseThrow();
         this.width = Adapters.INT.readJson(json.get("width")).orElseThrow();
         this.height = Adapters.INT.readJson(json.get("height")).orElseThrow();
         this.tasks.clear();
         Stream.of(this.tasks, this.firstBingo, this.subsequentBingo).forEach(map -> {
            map.clear();
            JsonArray list = json.getAsJsonArray(map == this.tasks ? "tasks" : (map == this.firstBingo ? "firstBingo" : "subsequentBingo"));
            if (list != null) {
               for (JsonElement element : list) {
                  if (element instanceof JsonObject object) {
                     Adapters.TASK.readJson(element).ifPresent(task -> {
                        int level = Adapters.INT.readJson(object.get("level")).orElse(0);
                        double weight = Adapters.DOUBLE.readJson(object.get("weight")).orElse(1.0);
                        map.computeIfAbsent(level, i -> new WeightedList<>()).add(task, weight);
                     });
                  }
               }
            }
         });
      }
   }

   public static enum State {
      INCOMPLETE,
      COMPLETE,
      BINGO;
   }
}
