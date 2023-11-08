package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.source.TaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class OperableTask extends Task {
   protected Task delegate;
   protected boolean operating;

   public OperableTask() {
   }

   public OperableTask(Task delegate) {
      this.delegate = delegate;
      this.operating = false;
   }

   public Task getDelegate() {
      return this.delegate;
   }

   public boolean isOperating() {
      return this.operating;
   }

   public boolean shouldBeOperating(TaskSource source) {
      return !this.delegate.isCompleted(source);
   }

   @Override
   public boolean isCompleted(TaskSource source) {
      return this.delegate.isCompleted(source);
   }

   @Override
   public void onAttach(TaskSource source) {
      if (this.isOperating()) {
         this.delegate.onAttach(source);
      }
   }

   @Override
   public void onStart(TaskSource source) {
      this.delegate.onStart(source);
   }

   @Override
   public void onTick(TaskSource source) {
      this.delegate.onTick(source);
      boolean newOperating = this.shouldBeOperating(source);
      if (newOperating && !this.operating) {
         this.onStart(source);
      } else if (!newOperating && this.operating) {
         this.onStop(source);
      }

      this.operating = newOperating;
   }

   @Override
   public void onStop(TaskSource source) {
      this.delegate.onStop(source);
   }

   @Override
   public void onDetach() {
      if (this.isOperating()) {
         this.delegate.onDetach();
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.TASK.writeBits(this.delegate, buffer);
      buffer.writeBoolean(this.operating);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.delegate = Adapters.TASK.readBits(buffer).orElseThrow();
      this.operating = buffer.readBoolean();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.TASK.writeNbt(this.delegate).ifPresent(value -> nbt.put("delegate", value));
         Adapters.BOOLEAN.writeNbt(this.operating).ifPresent(value -> nbt.put("operating", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.delegate = Adapters.TASK.readNbt(nbt.getCompound("delegate")).orElseThrow();
      this.operating = Adapters.BOOLEAN.readNbt(nbt.get("operating")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.TASK.writeJson(this.delegate).ifPresent(value -> json.add("delegate", value));
         Adapters.BOOLEAN.writeJson(this.operating).ifPresent(value -> json.add("operating", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.delegate = Adapters.TASK.readJson(json.getAsJsonObject("delegate")).orElseThrow();
      this.operating = Adapters.BOOLEAN.readJson(json.get("operating")).orElse(false);
   }
}
