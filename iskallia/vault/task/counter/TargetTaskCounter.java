package iskallia.vault.task.counter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.EnumAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.task.TaskContext;
import iskallia.vault.task.util.TaskProgress;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class TargetTaskCounter<T, C extends TargetTaskCounter.Config<T, ?>> extends TaskCounter<T, C> {
   protected T current;
   protected T target;
   protected T baseTarget;

   public TargetTaskCounter(TaskCounter.Group<T> group, C config) {
      super(group, config);
      this.current = group.getIdentity();
   }

   public T getCurrent() {
      return this.current;
   }

   public void setCurrent(T current) {
      this.current = current;
   }

   public T getTarget() {
      return this.target;
   }

   public void setTarget(Object target) {
      this.target = (T)target;
   }

   public TaskCounterPredicate getPredicate() {
      return this.getConfig().getPredicate();
   }

   public T getBaseTarget() {
      return this.baseTarget;
   }

   @Override
   public TaskProgress getProgress() {
      return this.current instanceof Number current && this.target instanceof Number target ? new TaskProgress(current, target) : new TaskProgress(0.0, 0.0);
   }

   @Override
   public boolean isCompleted() {
      return super.isCompleted() && this.isPopulated() && this.getPredicate().test(this.current, this.target, this.getGroup());
   }

   @Override
   public void onPopulate(TaskContext context) {
      this.current = this.getGroup().getIdentity();
      this.target = this.baseTarget = this.getConfig().generateCount(context.getSource().getRandom());
   }

   @Override
   public void onSet(T value, TaskContext context) {
      super.onSet(value, context);
      this.current = value;
   }

   @Override
   public void onAdd(T value, TaskContext context) {
      super.onAdd(value, context);
      this.current = this.getGroup().getOperator().apply(this.current, value);
   }

   @Override
   public void onRemove(T value, TaskContext context) {
      super.onRemove(value, context);
      this.current = this.getGroup().getOperator().apply(this.current, this.getGroup().getInverse().apply(value));
   }

   @Override
   public void onReset(TaskContext context) {
      super.onReset(context);
      this.current = this.getGroup().getIdentity();
      this.target = this.baseTarget;
   }

   @Override
   public void onRepeat(TaskContext context) {
      super.onRepeat(context);
      if (this.getPredicate() != TaskCounterPredicate.GREATER_THAN && this.getPredicate() != TaskCounterPredicate.GREATER_OR_EQUAL_TO) {
         this.current = this.getGroup().getIdentity();
      } else {
         T overflow = this.getGroup().getOperator().apply(this.current, this.getGroup().getInverse().apply(this.target));
         if (this.getGroup().getOrdering().compare(overflow, this.getGroup().getIdentity()) < 0) {
            this.current = this.getGroup().getIdentity();
         } else {
            this.current = overflow;
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      if (this.isPopulated()) {
         this.getGroup().getAdapter().writeBits(this.current, buffer);
         this.getGroup().getAdapter().writeBits(this.target, buffer);
         this.getGroup().getAdapter().writeBits(this.baseTarget, buffer);
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      if (this.isPopulated()) {
         this.current = this.getGroup().getAdapter().readBits(buffer).orElseThrow();
         this.target = this.getGroup().getAdapter().readBits(buffer).orElseThrow();
         this.baseTarget = this.getGroup().getAdapter().readBits(buffer).orElseThrow();
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         if (!this.isPopulated()) {
            return (CompoundTag)nbt;
         } else {
            this.getGroup().getAdapter().writeNbt(this.current).ifPresent(tag -> nbt.put("current", tag));
            this.getGroup().getAdapter().writeNbt(this.target).ifPresent(tag -> nbt.put("target", tag));
            this.getGroup().getAdapter().writeNbt(this.baseTarget).ifPresent(tag -> nbt.put("baseTarget", tag));
            return (CompoundTag)nbt;
         }
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      if (this.isPopulated()) {
         this.current = this.getGroup().getAdapter().readNbt(nbt.get("current")).orElseThrow();
         this.target = this.getGroup().getAdapter().readNbt(nbt.get("target")).orElseThrow();
         this.baseTarget = this.getGroup().getAdapter().readNbt(nbt.get("baseTarget")).orElseThrow();
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         if (!this.isPopulated()) {
            return (JsonObject)json;
         } else {
            this.getGroup().getAdapter().writeJson(this.current).ifPresent(tag -> json.add("current", tag));
            this.getGroup().getAdapter().writeJson(this.target).ifPresent(tag -> json.add("target", tag));
            this.getGroup().getAdapter().writeJson(this.baseTarget).ifPresent(tag -> json.add("baseTarget", tag));
            return (JsonObject)json;
         }
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      if (this.isPopulated()) {
         this.current = this.getGroup().getAdapter().readJson(json.get("current")).orElseThrow();
         this.target = this.getGroup().getAdapter().readJson(json.get("target")).orElseThrow();
         this.baseTarget = this.getGroup().getAdapter().readJson(json.get("baseTarget")).orElseThrow();
      }
   }

   public static class Config<T, G> extends TaskCounter.Config {
      public static EnumAdapter<TaskCounterPredicate> PREDICATE = Adapters.ofEnum(TaskCounterPredicate.class, EnumAdapter.Mode.NAME);
      private G target;
      private TaskCounterPredicate predicate;
      private final ISimpleAdapter<G, ? super Tag, ? super JsonElement> adapter;
      private final BiFunction<G, RandomSource, T> generator;

      public Config(
         G target, TaskCounterPredicate predicate, ISimpleAdapter<G, ? super Tag, ? super JsonElement> adapter, BiFunction<G, RandomSource, T> generator
      ) {
         this.target = target;
         this.predicate = predicate;
         this.adapter = adapter;
         this.generator = generator;
      }

      public G getTarget() {
         return this.target;
      }

      public TaskCounterPredicate getPredicate() {
         return this.predicate;
      }

      public T generateCount(RandomSource random) {
         return this.generator.apply(this.target, random);
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         super.writeBits(buffer);
         this.adapter.writeBits(this.target, buffer);
         PREDICATE.writeBits(this.predicate, buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         super.readBits(buffer);
         this.target = this.adapter.readBits(buffer).orElseThrow();
         this.predicate = PREDICATE.readBits(buffer).orElseThrow();
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         return super.writeNbt().map(nbt -> {
            this.adapter.writeNbt(this.target).ifPresent(tag -> nbt.put("target", tag));
            PREDICATE.writeNbt(this.predicate).ifPresent(tag -> nbt.put("predicate", tag));
            return (CompoundTag)nbt;
         });
      }

      @Override
      public void readNbt(CompoundTag nbt) {
         super.readNbt(nbt);
         this.target = this.adapter.readNbt(nbt.get("target")).orElseThrow();
         this.predicate = PREDICATE.readNbt(nbt.get("predicate")).orElse(TaskCounterPredicate.GREATER_OR_EQUAL_TO);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         return super.writeJson().map(json -> {
            this.adapter.writeJson(this.target).ifPresent(tag -> json.add("target", tag));
            PREDICATE.writeJson(this.predicate).ifPresent(tag -> json.add("predicate", tag));
            return (JsonObject)json;
         });
      }

      @Override
      public void readJson(JsonObject json) {
         super.readJson(json);
         this.target = this.adapter.readJson(json.get("target")).orElseThrow();
         this.predicate = PREDICATE.readJson(json.get("predicate")).orElse(TaskCounterPredicate.GREATER_OR_EQUAL_TO);
      }
   }
}
