package iskallia.vault.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.util.IProgressTask;
import iskallia.vault.task.util.TaskProgress;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public abstract class ProgressConfiguredTask<N, C extends ConfiguredTask.Config> extends ConfiguredTask<C> implements IProgressTask {
   protected TaskCounter<N, ?> counter;
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

   @Override
   public TaskProgress getProgress() {
      return this.counter.getProgress();
   }

   @Override
   public void onPopulate(TaskContext context) {
      this.counter.onPopulate(context);
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
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      this.adapter.writeBits(this.counter, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.counter = this.adapter.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         this.adapter.writeNbt(this.counter).ifPresent(tag -> nbt.put("counter", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.counter = this.adapter.readNbt(nbt.get("counter")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         this.adapter.writeJson(this.counter).ifPresent(tag -> json.add("counter", tag));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.counter = this.adapter.readJson(json.get("counter")).orElseThrow();
   }
}
