package iskallia.vault.task.counter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.roll.FloatRoll;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.item.crystal.data.adapter.INbtAdapter;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.util.TaskProgress;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public abstract class TaskCounter<T, C extends TaskCounter.Config> implements ISerializable<CompoundTag, JsonObject> {
   private final TaskCounter.Group<T> group;
   private C config;
   private boolean populated;

   public TaskCounter(TaskCounter.Group<T> group, C config) {
      this.group = group;
      this.config = config;
   }

   public TaskCounter.Group<T> getGroup() {
      return this.group;
   }

   public C getConfig() {
      return this.config;
   }

   public <N extends Tag> N get(String name) {
      return (N)this.config.variables.get(name);
   }

   public <V> Optional<V> get(String name, INbtAdapter<V, ?, ?> adapter) {
      return ((INbtAdapter<V, Tag, ?>)adapter).readNbt(this.get(name), null);
   }

   public boolean isPopulated() {
      return this.populated;
   }

   public void setPopulated(boolean populated) {
      this.populated = populated;
   }

   public abstract void onPopulate(TaskContext var1);

   public void populate(TaskContext context) {
      if (!this.isPopulated()) {
         this.onPopulate(context);
         this.setPopulated(true);
      }
   }

   public abstract TaskProgress getProgress();

   public void onSet(T value, TaskContext context) {
      this.populate(context);
   }

   public void onAdd(T value, TaskContext context) {
      this.populate(context);
   }

   public void onRemove(T value, TaskContext context) {
      this.populate(context);
   }

   public boolean isCompleted() {
      return this.isPopulated();
   }

   public void onAttach(TaskContext context) {
      this.populate(context);
   }

   public void onReset(TaskContext context) {
      this.populate(context);
   }

   public void onRepeat(TaskContext context) {
      this.populate(context);
   }

   public void onDetach() {
      CommonEvents.release(this);
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      this.config.writeBits(buffer);
      Adapters.BOOLEAN.writeBits(this.populated, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.config.readBits(buffer);
      this.populated = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         if (!this.populated) {
            CompoundTag other = this.config.writeNbt().orElseThrow();
            other.getAllKeys().forEach(key -> nbt.put(key, Objects.requireNonNull(other.get(key))));
         } else {
            nbt.put("config", (Tag)this.config.writeNbt().orElseThrow());
         }

         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      if (!nbt.contains("config")) {
         this.config.readNbt(nbt);
         this.populated = false;
      } else {
         this.config.readNbt(nbt.getCompound("config"));
         this.populated = true;
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return Optional.of(new JsonObject()).map(json -> {
         if (!this.populated) {
            this.config.writeJson().orElseThrow().entrySet().forEach(entry -> json.add((String)entry.getKey(), (JsonElement)entry.getValue()));
         } else {
            json.add("config", (JsonElement)this.config.writeJson().orElseThrow());
         }

         return (JsonObject)json;
      });
   }

   public void readJson(JsonObject json) {
      if (!json.has("config")) {
         this.config.readJson(json);
         this.populated = false;
      } else {
         this.config.readJson(json.getAsJsonObject("config"));
         this.populated = true;
      }
   }

   public static TargetTaskCounter<Integer, TargetTaskCounter.Config<Integer, ?>> ofTargetInt() {
      return ofTargetInt(null, null);
   }

   public static TargetTaskCounter<Integer, TargetTaskCounter.Config<Integer, ?>> ofTargetInt(IntRoll target, TaskCounterPredicate predicate) {
      return new TargetTaskCounter<>(TaskCounter.Group.INT, new TargetTaskCounter.Config<>(target, predicate, Adapters.INT_ROLL, IntRoll::get));
   }

   public static SlidingTimedTargetTaskCounter<Integer, SlidingTimedTargetTaskCounter.Config<Integer, ?>> ofSlidingTargetInt() {
      return ofSlidingTargetInt(null, null);
   }

   public static SlidingTimedTargetTaskCounter<Integer, SlidingTimedTargetTaskCounter.Config<Integer, ?>> ofSlidingTargetInt(IntRoll target, IntRoll window) {
      return new SlidingTimedTargetTaskCounter<>(
         TaskCounter.Group.INT, new SlidingTimedTargetTaskCounter.Config<>(target, Adapters.INT_ROLL, IntRoll::get, window)
      );
   }

   public static TargetTaskCounter<Float, TargetTaskCounter.Config<Float, ?>> ofTargetFloat() {
      return ofTargetFloat(null, null);
   }

   public static TargetTaskCounter<Float, TargetTaskCounter.Config<Float, ?>> ofTargetFloat(FloatRoll target, TaskCounterPredicate predicate) {
      return new TargetTaskCounter<>(TaskCounter.Group.FLOAT, new TargetTaskCounter.Config<>(target, predicate, Adapters.FLOAT_ROLL, FloatRoll::get));
   }

   public static SlidingTimedTargetTaskCounter<Float, SlidingTimedTargetTaskCounter.Config<Float, ?>> ofSlidingTargetFloat() {
      return ofSlidingTargetFloat(null, null);
   }

   public static SlidingTimedTargetTaskCounter<Float, SlidingTimedTargetTaskCounter.Config<Float, ?>> ofSlidingTargetFloat(FloatRoll target, IntRoll window) {
      return new SlidingTimedTargetTaskCounter<>(
         TaskCounter.Group.FLOAT, new SlidingTimedTargetTaskCounter.Config<>(target, Adapters.FLOAT_ROLL, FloatRoll::get, window)
      );
   }

   public static class Adapter {
      public static TypeSupplierAdapter<? extends TaskCounter<Integer, ?>> INT = new TypeSupplierAdapter<TaskCounter<Integer, ?>>("type", true) {
         {
            this.register("target", TargetTaskCounter.class, TaskCounter::ofTargetInt);
            this.register("sliding_timed_target", SlidingTimedTargetTaskCounter.class, TaskCounter::ofSlidingTargetInt);
         }
      };
      public static TypeSupplierAdapter<? extends TaskCounter<Float, ?>> FLOAT = new TypeSupplierAdapter<TaskCounter<Float, ?>>("type", true) {
         {
            this.register("target", TargetTaskCounter.class, TaskCounter::ofTargetFloat);
            this.register("sliding_timed_target", SlidingTimedTargetTaskCounter.class, TaskCounter::ofSlidingTargetFloat);
         }
      };
   }

   public abstract static class Config implements ISerializable<CompoundTag, JsonObject> {
      protected final Map<String, Tag> variables = new LinkedHashMap<>();

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(this.variables.size()), buffer);
         this.variables.forEach((name, value) -> {
            Adapters.UTF_8.writeBits(name, buffer);
            Adapters.GENERIC_NBT.writeBits(value, buffer);
         });
      }

      @Override
      public void readBits(BitBuffer buffer) {
         this.variables.clear();
         int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

         for (int i = 0; i < size; i++) {
            this.variables.put(Adapters.UTF_8.readBits(buffer).orElseThrow(), Adapters.GENERIC_NBT.readBits(buffer).orElseThrow());
         }
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return Optional.of(new CompoundTag()).map(nbt -> {
            CompoundTag variables = new CompoundTag();
            this.variables.forEach(variables::put);
            nbt.put("variables", variables);
            return (CompoundTag)nbt;
         });
      }

      public void readNbt(CompoundTag nbt) {
         this.variables.clear();
         CompoundTag variables = nbt.getCompound("variables");

         for (String name : variables.getAllKeys()) {
            this.variables.put(name, variables.get(name));
         }
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return Optional.of(new JsonObject()).map(json -> {
            JsonObject variables = new JsonObject();
            this.variables.forEach((name, value) -> Adapters.GENERIC_NBT.writeJson(value).ifPresent(tag -> variables.add(name, tag)));
            json.add("variables", variables);
            return (JsonObject)json;
         });
      }

      public void readJson(JsonObject json) {
         this.variables.clear();
         if (json.get("variables") instanceof JsonObject variables) {
            for (String name : variables.keySet()) {
               Adapters.GENERIC_NBT.readJson(variables.get(name)).ifPresent(tag -> this.variables.put(name, tag));
            }
         }
      }
   }

   public static class Group<T> {
      public static final TaskCounter.Group<Integer> INT = new TaskCounter.Group<>(Adapters.INT_SEGMENTED_7, 0, Integer::sum, Integer::compare, i -> -i);
      public static final TaskCounter.Group<Float> FLOAT = new TaskCounter.Group<>(Adapters.FLOAT, 0.0F, Float::sum, Float::compare, i -> -i);
      private final ISimpleAdapter<T, ? super Tag, ? super JsonElement> adapter;
      private final T identity;
      private final BinaryOperator<T> operator;
      private final Comparator<T> ordering;
      private final UnaryOperator<T> inverse;

      public Group(
         ISimpleAdapter<T, ? super Tag, ? super JsonElement> adapter, T identity, BinaryOperator<T> operator, Comparator<T> ordering, UnaryOperator<T> inverse
      ) {
         this.adapter = adapter;
         this.identity = identity;
         this.operator = operator;
         this.ordering = ordering;
         this.inverse = inverse;
      }

      public ISimpleAdapter<T, ? super Tag, ? super JsonElement> getAdapter() {
         return this.adapter;
      }

      public T getIdentity() {
         return this.identity;
      }

      public BinaryOperator<T> getOperator() {
         return this.operator;
      }

      public Comparator<T> getOrdering() {
         return this.ordering;
      }

      public UnaryOperator<T> getInverse() {
         return this.inverse;
      }
   }
}
