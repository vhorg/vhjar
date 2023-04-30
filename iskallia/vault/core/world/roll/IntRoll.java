package iskallia.vault.core.world.roll;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.basic.TypeSupplierAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public interface IntRoll extends ISerializable<CompoundTag, JsonObject> {
   int get(RandomSource var1);

   static IntRoll.Constant ofConstant(int count) {
      return new IntRoll.Constant(count);
   }

   static IntRoll.Uniform ofUniform(int min, int max) {
      return new IntRoll.Uniform(min, max);
   }

   static int getMin(IntRoll roll) {
      if (roll instanceof IntRoll.Constant constant) {
         return constant.getCount();
      } else if (roll instanceof IntRoll.Uniform uniform) {
         return uniform.getMin();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   static int getMax(IntRoll roll) {
      if (roll instanceof IntRoll.Constant constant) {
         return constant.getCount();
      } else if (roll instanceof IntRoll.Uniform uniform) {
         return uniform.getMax();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public static class Adapter extends TypeSupplierAdapter<IntRoll> {
      public Adapter() {
         super("type", true);
         this.register("constant", IntRoll.Constant.class, IntRoll.Constant::new);
         this.register("uniform", IntRoll.Uniform.class, IntRoll.Uniform::new);
      }

      @Nullable
      protected IntRoll readSuppliedNbt(Tag nbt) {
         if (nbt instanceof NumericTag || nbt instanceof StringTag) {
            Optional<Integer> result = Adapters.INT.readNbt(nbt);
            if (result.isPresent()) {
               return IntRoll.ofConstant(result.get());
            }
         }

         return (IntRoll)super.readSuppliedNbt(nbt);
      }

      @Nullable
      protected IntRoll readSuppliedJson(JsonElement json) {
         if (json instanceof JsonPrimitive primitive && (primitive.isNumber() || primitive.isString())) {
            Optional<Integer> result = Adapters.INT.readJson(json);
            if (result.isPresent()) {
               return IntRoll.ofConstant(result.get());
            }
         }

         return (IntRoll)super.readSuppliedJson(json);
      }
   }

   public static class Constant implements IntRoll {
      private int count;

      protected Constant() {
      }

      protected Constant(int count) {
         this.count = count;
      }

      public int getCount() {
         return this.count;
      }

      @Override
      public int get(RandomSource random) {
         return this.count;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.count), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         Adapters.INT_SEGMENTED_7.readBits(buffer).ifPresent(value -> this.count = value);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         Adapters.INT.writeNbt(Integer.valueOf(this.count)).ifPresent(tag -> nbt.put("count", tag));
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         Adapters.INT.readNbt(nbt.get("count")).ifPresent(value -> this.count = value);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         Adapters.INT.writeJson(Integer.valueOf(this.count)).ifPresent(tag -> json.add("count", tag));
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
         Adapters.INT.readJson(json.get("count")).ifPresent(value -> this.count = value);
      }
   }

   public static class Uniform implements IntRoll {
      private int min;
      private int max;

      protected Uniform() {
      }

      protected Uniform(int min, int max) {
         this.min = min;
         this.max = max;
      }

      public int getMin() {
         return this.min;
      }

      public int getMax() {
         return this.max;
      }

      @Override
      public int get(RandomSource random) {
         return random.nextInt(this.max - this.min + 1) + this.min;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.min), buffer);
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.max), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         Adapters.INT_SEGMENTED_7.readBits(buffer).ifPresent(value -> this.min = value);
         Adapters.INT_SEGMENTED_7.readBits(buffer).ifPresent(value -> this.max = value);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         Adapters.INT.writeNbt(Integer.valueOf(this.min)).ifPresent(tag -> nbt.put("min", tag));
         Adapters.INT.writeNbt(Integer.valueOf(this.max)).ifPresent(tag -> nbt.put("max", tag));
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         Adapters.INT.readNbt(nbt.get("min")).ifPresent(value -> this.min = value);
         Adapters.INT.readNbt(nbt.get("max")).ifPresent(value -> this.max = value);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         Adapters.INT.writeJson(Integer.valueOf(this.min)).ifPresent(tag -> json.add("min", tag));
         Adapters.INT.writeJson(Integer.valueOf(this.max)).ifPresent(tag -> json.add("max", tag));
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
         Adapters.INT.readJson(json.get("min")).ifPresent(value -> this.min = value);
         Adapters.INT.readJson(json.get("max")).ifPresent(value -> this.max = value);
      }
   }
}
