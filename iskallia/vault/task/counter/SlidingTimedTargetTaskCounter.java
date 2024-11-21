package iskallia.vault.task.counter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.task.TaskContext;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class SlidingTimedTargetTaskCounter<T, C extends SlidingTimedTargetTaskCounter.Config<T, ?>> extends TargetTaskCounter<T, C> {
   private final LinkedList<SlidingTimedTargetTaskCounter.Frame<T>> frames = new LinkedList<>();
   private int window;

   public SlidingTimedTargetTaskCounter(TaskCounter.Group<T> group, C config) {
      super(group, config);
   }

   public int getWindow() {
      return this.window;
   }

   @Override
   public void onPopulate(TaskContext context) {
      super.onPopulate(context);
      this.window = this.getConfig().window.get(context.getSource().getRandom());
   }

   @Override
   public void onAdd(T value, TaskContext context) {
      super.onAdd(value, context);
      this.frames.add(new SlidingTimedTargetTaskCounter.Frame<>(this.getGroup(), context.getTickTime(), value));
   }

   @Override
   public void onRemove(T value, TaskContext context) {
      super.onRemove(value, context);
      this.frames.add(new SlidingTimedTargetTaskCounter.Frame<>(this.getGroup(), context.getTickTime(), this.getGroup().getInverse().apply(value)));
   }

   @Override
   public void onAttach(TaskContext context) {
      super.onAttach(context);
      CommonEvents.SERVER_TICK.register(this, event -> {
         long time = context.getTickTime();

         while (!this.frames.isEmpty()) {
            SlidingTimedTargetTaskCounter.Frame<T> frame = this.frames.peekFirst();
            if (time - frame.time <= this.window - 1) {
               break;
            }

            this.frames.removeFirst();
            super.onRemove(frame.getValue(), context);
         }
      });
   }

   @Override
   public void onReset(TaskContext context) {
      super.onReset(context);
      this.frames.clear();
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.frames.size()), buffer);

      for (SlidingTimedTargetTaskCounter.Frame<T> frame : this.frames) {
         frame.writeBits(buffer);
      }

      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.window), buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      int size = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.frames.clear();

      for (int i = 0; i < size; i++) {
         SlidingTimedTargetTaskCounter.Frame<T> frame = new SlidingTimedTargetTaskCounter.Frame<>(this.getGroup());
         frame.readBits(buffer);
         this.frames.add(frame);
      }

      this.window = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         ListTag frames = new ListTag();

         for (SlidingTimedTargetTaskCounter.Frame<T> frame : this.frames) {
            frame.writeNbt().ifPresent(frames::add);
         }

         nbt.put("frames", frames);
         Adapters.INT.writeNbt(Integer.valueOf(this.window)).ifPresent(tag -> nbt.put("window", tag));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      ListTag frames = nbt.getList("frames", 10);
      this.frames.clear();

      for (int i = 0; i < frames.size(); i++) {
         SlidingTimedTargetTaskCounter.Frame<T> frame = new SlidingTimedTargetTaskCounter.Frame<>(this.getGroup());
         frame.readNbt(frames.getCompound(i));
         this.frames.add(frame);
      }

      this.window = Adapters.INT.readNbt(nbt.get("window")).orElse(0);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         JsonArray frames = new JsonArray();

         for (SlidingTimedTargetTaskCounter.Frame<T> frame : this.frames) {
            frame.writeJson().ifPresent(frames::add);
         }

         json.add("frames", frames);
         Adapters.INT.writeJson(Integer.valueOf(this.window)).ifPresent(tag -> json.add("window", tag));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.frames.clear();
      JsonElement var3 = json.get("frames");
      if (var3 instanceof JsonArray) {
         for (JsonElement child : (JsonArray)var3) {
            if (child instanceof JsonObject object) {
               SlidingTimedTargetTaskCounter.Frame<T> frame = new SlidingTimedTargetTaskCounter.Frame<>(this.getGroup());
               frame.readJson(object);
               this.frames.add(frame);
            }
         }
      }

      this.window = Adapters.INT.readJson(json.get("window")).orElse(0);
   }

   public static class Config<T, G> extends TargetTaskCounter.Config<T, G> {
      protected IntRoll window;

      public Config(G target, ISimpleAdapter<G, ? super Tag, ? super JsonElement> adapter, BiFunction<G, RandomSource, T> generator, IntRoll window) {
         super(target, TaskCounterPredicate.GREATER_OR_EQUAL_TO, adapter, generator);
         this.window = window;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         Adapters.INT_ROLL.writeBits(this.window, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.window = Adapters.INT_ROLL.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            Adapters.INT_ROLL.writeNbt(this.window).ifPresent(tag -> nbt.put("window", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.window = Adapters.INT_ROLL.readNbt(nbt.get("window")).orElseThrow();
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            Adapters.INT_ROLL.writeJson(this.window).ifPresent(tag -> json.add("window", tag));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.window = Adapters.INT_ROLL.readJson(json.get("window")).orElseThrow();
      }
   }

   private static final class Frame<T> implements ISerializable<CompoundTag, JsonObject> {
      private final TaskCounter.Group<T> group;
      private long time;
      private T value;

      private Frame(TaskCounter.Group<T> group) {
         this.group = group;
      }

      private Frame(TaskCounter.Group<T> group, long time, T value) {
         this.group = group;
         this.time = time;
         this.value = value;
      }

      public long getTime() {
         return this.time;
      }

      public T getValue() {
         return this.value;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.LONG.writeBits(Long.valueOf(this.time), buffer);
         this.group.getAdapter().writeBits(this.value, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         this.time = Adapters.LONG.readBits(buffer).orElseThrow();
         this.value = this.group.getAdapter().readBits(buffer).orElse(null);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            Adapters.LONG.writeNbt(Long.valueOf(this.time)).ifPresent(tag -> nbt.put("time", tag));
            this.group.getAdapter().writeNbt(this.value).ifPresent(tag -> nbt.put("value", tag));
            return (CompoundTag)nbt;
         });
      }

      public void readNbt(CompoundTag nbt) {
         this.time = Adapters.LONG.readNbt(nbt.get("time")).orElseThrow();
         this.value = this.group.getAdapter().readNbt(nbt.get("value")).orElseThrow();
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return Optional.of(new JsonObject()).map(json -> {
            Adapters.LONG.writeJson(Long.valueOf(this.time)).ifPresent(tag -> json.add("time", tag));
            this.group.getAdapter().writeJson(this.value).ifPresent(tag -> json.add("value", tag));
            return (JsonObject)json;
         });
      }

      public void readJson(JsonObject json) {
         this.time = Adapters.LONG.readJson(json.get("time")).orElseThrow();
         this.value = this.group.getAdapter().readJson(json.get("value")).orElseThrow();
      }
   }
}
