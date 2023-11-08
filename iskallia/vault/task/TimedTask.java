package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.source.TaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class TimedTask extends Task {
   private long elapsed;
   private long duration;

   public TimedTask() {
   }

   public TimedTask(long duration) {
      this.elapsed = 0L;
      this.duration = duration;
   }

   public long getElapsed() {
      return this.elapsed;
   }

   public long getDuration() {
      return this.duration;
   }

   @Override
   public boolean isCompleted(TaskSource source) {
      return this.elapsed < this.duration;
   }

   @Override
   public void onTick(TaskSource source) {
      super.onTick(source);
      this.elapsed++;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.LONG_SEGMENTED_15.writeBits(Long.valueOf(this.elapsed), buffer);
      Adapters.LONG_SEGMENTED_15.writeBits(Long.valueOf(this.duration), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.elapsed = Adapters.LONG_SEGMENTED_15.readBits(buffer).orElseThrow();
      this.duration = Adapters.LONG_SEGMENTED_15.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.LONG_SEGMENTED_15.writeNbt(Long.valueOf(this.elapsed)).ifPresent(value -> nbt.put("elapsed", value));
         Adapters.LONG_SEGMENTED_15.writeNbt(Long.valueOf(this.duration)).ifPresent(value -> nbt.put("duration", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.elapsed = Adapters.LONG_SEGMENTED_15.readNbt(nbt.get("elapsed")).orElse(0L);
      this.duration = Adapters.LONG_SEGMENTED_15.readNbt(nbt.get("duration")).orElseThrow();
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.LONG_SEGMENTED_15.writeJson(Long.valueOf(this.elapsed)).ifPresent(value -> json.add("elapsed", value));
         Adapters.LONG_SEGMENTED_15.writeJson(Long.valueOf(this.duration)).ifPresent(value -> json.add("duration", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.elapsed = Adapters.LONG_SEGMENTED_15.readJson(json.get("elapsed")).orElse(0L);
      this.duration = Adapters.LONG_SEGMENTED_15.readJson(json.get("duration")).orElseThrow();
   }
}
