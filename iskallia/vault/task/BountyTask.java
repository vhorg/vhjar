package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.task.source.TaskSource;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

public class BountyTask extends OperableTask {
   private BountyTask.State state;
   private long timeout;

   public BountyTask() {
   }

   public BountyTask(Task delegate, BountyTask.State state) {
      super(delegate);
      this.state = state;
      this.timeout = 0L;
   }

   public BountyTask setState(BountyTask.State state, long timeout) {
      this.state = state;
      this.timeout = timeout;
      return this;
   }

   @Override
   public boolean shouldBeOperating(TaskSource source) {
      return super.shouldBeOperating(source) && this.state == BountyTask.State.ACTIVE && this.timeout > 0L;
   }

   @Override
   public void onTick(TaskSource source) {
      super.onTick(source);
      if (this.state == BountyTask.State.COMPLETE || this.state == BountyTask.State.ABANDONED) {
         this.timeout--;
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT.writeBits(Integer.valueOf(this.state.ordinal()), buffer);
      Adapters.LONG.writeBits(Long.valueOf(this.timeout), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.state = BountyTask.State.values()[Adapters.INT.readBits(buffer).orElse(0)];
      this.timeout = Adapters.LONG.readBits(buffer).orElse(0L);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.INT.writeNbt(Integer.valueOf(this.state.ordinal())).ifPresent(value -> nbt.put("state", value));
         Adapters.LONG.writeNbt(Long.valueOf(this.timeout)).ifPresent(value -> nbt.put("timeout", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.state = BountyTask.State.values()[Adapters.INT.readNbt(nbt).orElse(0)];
      this.timeout = Adapters.LONG.readNbt(nbt).orElse(0L);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.INT.writeJson(Integer.valueOf(this.state.ordinal())).ifPresent(value -> json.add("state", value));
         Adapters.LONG.writeJson(Long.valueOf(this.timeout)).ifPresent(value -> json.add("timeout", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.state = BountyTask.State.values()[Adapters.INT.readJson(json.get("state")).orElse(0)];
      this.timeout = Adapters.LONG.readJson(json.get("timeout")).orElse(0L);
   }

   public static enum State {
      ACTIVE,
      AVAILABLE,
      COMPLETE,
      ABANDONED;
   }
}
