package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public abstract class ConsumableTask<C extends ConfiguredTask.Config> extends ConfiguredTask<C> {
   protected boolean consumed;

   public ConsumableTask() {
   }

   public ConsumableTask(C config) {
      super(config);
   }

   @Override
   public boolean isCompleted() {
      return this.consumed;
   }

   protected abstract void onConsume(TaskContext var1);

   @Override
   public void onPopulate(TaskContext context) {
   }

   @Override
   public void onStart(TaskContext context) {
      super.onStart(context);
      if (!this.consumed) {
         this.onConsume(context);
         this.consumed = true;
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.BOOLEAN.writeBits(this.consumed, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.consumed = Adapters.BOOLEAN.readBits(buffer).orElse(false);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.BOOLEAN.writeNbt(this.consumed).ifPresent(value -> nbt.put("consumed", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.consumed = Adapters.BOOLEAN.readNbt(nbt.get("consumed")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.BOOLEAN.writeJson(this.consumed).ifPresent(value -> json.add("consumed", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.consumed = Adapters.BOOLEAN.readJson(json.get("consumed")).orElse(false);
   }
}
