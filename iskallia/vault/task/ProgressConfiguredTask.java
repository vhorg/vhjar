package iskallia.vault.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.task.condition.TaskCondition;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.util.IProgressTask;
import iskallia.vault.task.util.TaskProgress;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public abstract class ProgressConfiguredTask<N, C extends ConfiguredTask.Config> extends ConfiguredTask<C> implements IProgressTask, RepeatingTask {
   protected TaskCounter<N, ?> counter;
   protected TaskCondition<?> condition;
   protected ISimpleAdapter<TaskCounter<N, ?>, ? super Tag, ? super JsonElement> adapter;

   public ProgressConfiguredTask(C config, ISimpleAdapter<? extends TaskCounter<N, ?>, ? super Tag, ? super JsonElement> adapter) {
      super(config);
      this.adapter = (ISimpleAdapter<TaskCounter<N, ?>, ? super Tag, ? super JsonElement>)adapter;
   }

   public ProgressConfiguredTask(C config, TaskCounter<N, ?> counter, ISimpleAdapter<? extends TaskCounter<N, ?>, ? super Tag, ? super JsonElement> adapter) {
      super(config);
      this.counter = counter;
      this.adapter = (ISimpleAdapter<TaskCounter<N, ?>, ? super Tag, ? super JsonElement>)adapter;
   }

   public TaskCounter<N, ?> getCounter() {
      return this.counter;
   }

   @Nullable
   public TaskCondition<?> getCondition() {
      return this.condition;
   }

   @Override
   public TaskProgress getProgress() {
      return this.counter.getProgress();
   }

   @Override
   public void onPopulate(TaskContext context) {
      this.counter.populate(context);
   }

   @Override
   public boolean isCompleted() {
      return super.isCompleted() && this.counter.isCompleted();
   }

   @Override
   public void onAttach(TaskContext context) {
      this.counter.onAttach(context);
      super.onAttach(context);
   }

   @Override
   public void onDetach() {
      this.counter.onDetach();
      super.onDetach();
   }

   @Override
   public void onReset(TaskContext context) {
      this.counter.onReset(context);
      super.onReset(context);
   }

   @Override
   public void onRepeat(TaskContext context) {
      this.counter.onRepeat(context);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      this.adapter.writeBits(this.counter, buffer);
      Adapters.TASK_CONDITION.writeBits(this.condition, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.counter = this.adapter.readBits(buffer).orElseThrow();
      this.condition = Adapters.TASK_CONDITION.readBits(buffer).orElse(null);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         this.adapter.writeNbt(this.counter).ifPresent(tag -> nbt.put("counter", tag));
         Adapters.TASK_CONDITION.writeNbt(this.condition).ifPresent(tag -> nbt.put("condition", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.counter = this.adapter.readNbt(nbt.get("counter")).orElseThrow();
      this.condition = Adapters.TASK_CONDITION.readNbt(nbt.get("condition")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         this.adapter.writeJson(this.counter).ifPresent(tag -> json.add("counter", tag));
         Adapters.TASK_CONDITION.writeJson(this.condition).ifPresent(tag -> json.add("condition", tag));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.counter = this.adapter.readJson(json.get("counter")).orElseThrow();
      this.condition = Adapters.TASK_CONDITION.readJson(json.get("condition")).orElse(null);
   }
}
