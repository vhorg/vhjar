package iskallia.vault.task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.task.source.TaskSource;
import iskallia.vault.task.util.IProgressTask;
import iskallia.vault.task.util.TaskProgress;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public abstract class ProgressConfiguredTask<N extends Number, C extends ConfiguredTask.Config> extends ConfiguredTask<C> implements IProgressTask {
   protected N currentCount;
   protected N targetCount;
   private N zero;
   private ISimpleAdapter<N, ? super Tag, ? super JsonElement> adapter;
   private Comparator<N> comparator;

   public ProgressConfiguredTask(N zero, ISimpleAdapter<N, ? super Tag, ? super JsonElement> adapter, Comparator<N> comparator) {
      this.zero = zero;
      this.adapter = adapter;
      this.comparator = comparator;
   }

   public ProgressConfiguredTask(C config, N zero, ISimpleAdapter<N, ? super Tag, ? super JsonElement> adapter, Comparator<N> comparator) {
      super(config);
      this.zero = zero;
      this.adapter = adapter;
      this.comparator = comparator;
      this.currentCount = this.zero;
      this.targetCount = this.zero;
   }

   @Override
   public boolean isCompleted(TaskSource source) {
      return super.isCompleted(source) && this.comparator.compare(this.currentCount, this.targetCount) >= 0;
   }

   @Override
   public TaskProgress getProgress() {
      return new TaskProgress(this.currentCount, this.targetCount);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      if (this.isPopulated()) {
         this.adapter.writeBits(this.currentCount, buffer);
         this.adapter.writeBits(this.targetCount, buffer);
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      if (this.isPopulated()) {
         this.currentCount = this.adapter.readBits(buffer).orElse(this.zero);
         this.targetCount = this.adapter.readBits(buffer).orElse(this.zero);
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         if (!this.isPopulated()) {
            return (CompoundTag)nbt;
         } else {
            this.adapter.writeNbt(this.currentCount).ifPresent(value -> nbt.put("currentCount", value));
            this.adapter.writeNbt(this.targetCount).ifPresent(value -> nbt.put("targetCount", value));
            return (CompoundTag)nbt;
         }
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      if (this.isPopulated()) {
         this.currentCount = this.adapter.readNbt(nbt.get("currentCount")).orElse(this.zero);
         this.targetCount = this.adapter.readNbt(nbt.get("targetCount")).orElse(this.zero);
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         if (!this.isPopulated()) {
            return (JsonObject)json;
         } else {
            this.adapter.writeJson(this.currentCount).ifPresent(value -> json.add("currentCount", value));
            this.adapter.writeJson(this.targetCount).ifPresent(value -> json.add("targetCount", value));
            return (JsonObject)json;
         }
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      if (this.isPopulated()) {
         this.currentCount = this.adapter.readJson(json.get("currentCount")).orElse(this.zero);
         this.targetCount = this.adapter.readJson(json.get("targetCount")).orElse(this.zero);
      }
   }
}
