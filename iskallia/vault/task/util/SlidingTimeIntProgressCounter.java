package iskallia.vault.task.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.util.nbt.NBTHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class SlidingTimeIntProgressCounter implements IProgressCounter<Integer> {
   public static final String TYPE = "sliding_time";
   private List<SlidingTimeIntProgressCounter.Action> actions = new ArrayList<>();
   private int totalCount;
   private final int limitTicks;

   public SlidingTimeIntProgressCounter(SlidingTimeIntProgressCounter.Config config) {
      this.limitTicks = config.getLimitTicks();
   }

   public void addCount(Integer count) {
      this.actions.add(new SlidingTimeIntProgressCounter.Action(0, count));
      this.updateTotalCount();
   }

   public Integer getCount() {
      return this.totalCount;
   }

   public void setCount(Integer count) {
      if (count == 0) {
         this.actions.clear();
      } else {
         if (count < this.totalCount) {
            throw new UnsupportedOperationException("Cannot set count to value lower than total count other than 0");
         }

         this.actions.add(new SlidingTimeIntProgressCounter.Action(0, count - this.totalCount));
      }
   }

   @Override
   public void onTick() {
      if (!this.actions.isEmpty()) {
         Iterator<SlidingTimeIntProgressCounter.Action> it = this.actions.iterator();
         boolean updateTotal = false;

         while (it.hasNext()) {
            SlidingTimeIntProgressCounter.Action action = it.next();
            action.incrementTickTime();
            if (action.tickTime() > this.limitTicks) {
               it.remove();
               updateTotal = true;
            }
         }

         if (updateTotal) {
            this.updateTotalCount();
         }
      }
   }

   private void updateTotalCount() {
      this.totalCount = this.actions.stream().mapToInt(SlidingTimeIntProgressCounter.Action::count).sum();
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      buffer.writeCollection(this.actions, SlidingTimeIntProgressCounter.Action::writeBits);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.actions = buffer.readCollection(ArrayList::new, SlidingTimeIntProgressCounter.Action::fromBits);
      this.updateTotalCount();
   }

   @Override
   public Optional<Tag> writeNbt() {
      CompoundTag tag = new CompoundTag();
      NBTHelper.writeCollection(tag, "actions", this.actions, CompoundTag.class, SlidingTimeIntProgressCounter.Action::writeNbt);
      return Optional.of(tag);
   }

   @Override
   public void readNbt(Tag nbt) {
      if (nbt instanceof CompoundTag compoundTag) {
         this.actions = NBTHelper.readCollection(compoundTag, "actions", CompoundTag.class, SlidingTimeIntProgressCounter.Action::fromNbt, new ArrayList<>());
         this.updateTotalCount();
      }
   }

   @Override
   public Optional<JsonElement> writeJson() {
      JsonObject json = new JsonObject();
      JsonArray actions = new JsonArray();
      this.actions.forEach(action -> action.writeJson().ifPresent(actions::add));
      json.add("actions", actions);
      return Optional.of(json);
   }

   @Override
   public void readJson(JsonElement json) {
      if (json instanceof JsonObject jsonObject) {
         this.actions.clear();
         jsonObject.getAsJsonArray("actions")
            .forEach(jsonElement -> this.actions.add(SlidingTimeIntProgressCounter.Action.fromJson(jsonElement.getAsJsonObject())));
         this.updateTotalCount();
      }
   }

   public int getLimitTicks() {
      return this.limitTicks;
   }

   private static final class Action {
      private int tickTime;
      private final int count;

      private Action(int tickTime, int count) {
         this.tickTime = tickTime;
         this.count = count;
      }

      public void incrementTickTime() {
         this.tickTime++;
      }

      public int tickTime() {
         return this.tickTime;
      }

      public int count() {
         return this.count;
      }

      public void writeBits(BitBuffer buffer) {
         buffer.writeInt(this.tickTime);
         buffer.writeInt(this.count);
      }

      public static SlidingTimeIntProgressCounter.Action fromBits(BitBuffer buffer) {
         return new SlidingTimeIntProgressCounter.Action(buffer.readInt(), buffer.readInt());
      }

      public CompoundTag writeNbt() {
         CompoundTag tag = new CompoundTag();
         tag.putInt("tickTime", this.tickTime);
         tag.putInt("count", this.count);
         return tag;
      }

      public static SlidingTimeIntProgressCounter.Action fromNbt(CompoundTag nbt) {
         return new SlidingTimeIntProgressCounter.Action(nbt.getInt("tickTime"), nbt.getInt("count"));
      }

      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         json.addProperty("tickTime", this.tickTime);
         json.addProperty("count", this.count);
         return Optional.of(json);
      }

      public static SlidingTimeIntProgressCounter.Action fromJson(JsonObject json) {
         return new SlidingTimeIntProgressCounter.Action(json.get("tickTime").getAsInt(), json.get("count").getAsInt());
      }
   }

   public static class Config extends IProgressCounter.Config<Integer> {
      private int limitTicks;

      public Config() {
         super("sliding_time");
      }

      public Config(int limitTicks) {
         super("sliding_time");
         this.limitTicks = limitTicks;
      }

      public int getLimitTicks() {
         return this.limitTicks;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         buffer.writeInt(this.limitTicks);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.limitTicks = buffer.readInt();
      }

      @Override
      public Optional<Tag> writeNbt() {
         return super.writeNbt().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).map(nbt -> {
            nbt.putInt("limitTicks", this.limitTicks);
            return (Tag)nbt;
         });
      }

      @Override
      public void readNbt(Tag nbt) {
         super.readNbt(nbt);
         this.limitTicks = nbt instanceof CompoundTag compoundTag ? compoundTag.getInt("limitTicks") : 0;
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            json.addProperty("limitTicks", this.limitTicks);
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.limitTicks = json.get("limitTicks").getAsInt();
      }

      @Override
      public IProgressCounter<Integer> initCounter() {
         return new SlidingTimeIntProgressCounter(this);
      }
   }
}
