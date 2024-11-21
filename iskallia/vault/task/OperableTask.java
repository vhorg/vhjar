package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.TickEvent.Phase;

public abstract class OperableTask extends NodeTask {
   private boolean operating;

   public OperableTask() {
      this.children = new ArrayList<>();
   }

   public boolean isOperating() {
      return this.operating;
   }

   public boolean shouldBeOperating(TaskContext context) {
      return !this.isCompleted() && (this.parent == null || this.parent.hasActiveChildren());
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.SERVER_TICK.at(Phase.END).register(this, event -> this.onTick(context));
      super.onAttach(context);
   }

   public void onStart(TaskContext context) {
      this.operating = true;
   }

   public void onTick(TaskContext context) {
      boolean newOperating = this.shouldBeOperating(context);
      if (newOperating && !this.operating) {
         this.onStart(context);
      } else if (!newOperating && this.operating) {
         this.onStop(context);
      }
   }

   public void onStop(TaskContext context) {
      this.operating = false;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      buffer.writeBoolean(this.operating);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.operating = buffer.readBoolean();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.BOOLEAN.writeNbt(this.operating).ifPresent(value -> nbt.put("operating", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.operating = Adapters.BOOLEAN.readNbt(nbt.get("operating")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.BOOLEAN.writeJson(this.operating).ifPresent(value -> json.add("operating", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.operating = Adapters.BOOLEAN.readJson(json.get("operating")).orElse(false);
   }
}
