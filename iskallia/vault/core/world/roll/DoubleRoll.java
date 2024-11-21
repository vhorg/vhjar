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

public interface DoubleRoll extends ISerializable<CompoundTag, JsonObject> {
   double getMin();

   double getMax();

   double get(RandomSource var1);

   default boolean contains(double value) {
      return value >= this.getMin() && value <= this.getMax();
   }

   static DoubleRoll.Constant ofConstant(double count) {
      return new DoubleRoll.Constant(count);
   }

   static DoubleRoll.Uniform ofUniform(double min, double max) {
      return new DoubleRoll.Uniform(min, max);
   }

   static double getMin(DoubleRoll roll) {
      if (roll instanceof DoubleRoll.Constant constant) {
         return constant.getCount();
      } else if (roll instanceof DoubleRoll.Uniform uniform) {
         return uniform.getMin();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   static double getMax(DoubleRoll roll) {
      if (roll instanceof DoubleRoll.Constant constant) {
         return constant.getCount();
      } else if (roll instanceof DoubleRoll.Uniform uniform) {
         return uniform.getMax();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public static class Adapter extends TypeSupplierAdapter<DoubleRoll> {
      public Adapter() {
         super("type", true);
         this.register("constant", DoubleRoll.Constant.class, DoubleRoll.Constant::new);
         this.register("uniform", DoubleRoll.Uniform.class, DoubleRoll.Uniform::new);
      }

      @Nullable
      protected DoubleRoll readSuppliedNbt(Tag nbt) {
         if (nbt instanceof NumericTag || nbt instanceof StringTag) {
            Optional<Double> result = Adapters.DOUBLE.readNbt(nbt);
            if (result.isPresent()) {
               return DoubleRoll.ofConstant(result.get());
            }
         }

         return (DoubleRoll)super.readSuppliedNbt(nbt);
      }

      @Nullable
      protected DoubleRoll readSuppliedJson(JsonElement json) {
         if (json instanceof JsonPrimitive primitive && (primitive.isNumber() || primitive.isString())) {
            Optional<Double> result = Adapters.DOUBLE.readJson(json);
            if (result.isPresent()) {
               return DoubleRoll.ofConstant(result.get());
            }
         }

         return (DoubleRoll)super.readSuppliedJson(json);
      }
   }

   public static class Constant implements DoubleRoll {
      private double count;

      protected Constant() {
      }

      protected Constant(double count) {
         this.count = count;
      }

      public double getCount() {
         return this.count;
      }

      @Override
      public double getMin() {
         return this.getCount();
      }

      @Override
      public double getMax() {
         return this.getCount();
      }

      @Override
      public double get(RandomSource random) {
         return this.count;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.DOUBLE.writeBits(Double.valueOf(this.count), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         Adapters.DOUBLE.readBits(buffer).ifPresent(value -> this.count = value);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.count)).ifPresent(tag -> nbt.put("count", tag));
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         Adapters.DOUBLE.readNbt(nbt.get("count")).ifPresent(value -> this.count = value);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         Adapters.DOUBLE.writeJson(Double.valueOf(this.count)).ifPresent(tag -> json.add("count", tag));
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
         Adapters.DOUBLE.readJson(json.get("count")).ifPresent(value -> this.count = value);
      }
   }

   public static class Uniform implements DoubleRoll {
      private double min;
      private double max;

      protected Uniform() {
      }

      protected Uniform(double min, double max) {
         this.min = min;
         this.max = max;
      }

      @Override
      public double getMin() {
         return this.min;
      }

      @Override
      public double getMax() {
         return this.min + (this.max - this.min);
      }

      @Override
      public double get(RandomSource random) {
         return random.nextDouble() * (this.max - this.min) + this.min;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.DOUBLE.writeBits(Double.valueOf(this.min), buffer);
         Adapters.DOUBLE.writeBits(Double.valueOf(this.max), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         Adapters.DOUBLE.readBits(buffer).ifPresent(value -> this.min = value);
         Adapters.DOUBLE.readBits(buffer).ifPresent(value -> this.max = value);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.min)).ifPresent(tag -> nbt.put("min", tag));
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.max)).ifPresent(tag -> nbt.put("max", tag));
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         Adapters.DOUBLE.readNbt(nbt.get("min")).ifPresent(value -> this.min = value);
         Adapters.DOUBLE.readNbt(nbt.get("max")).ifPresent(value -> this.max = value);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         Adapters.DOUBLE.writeJson(Double.valueOf(this.min)).ifPresent(tag -> json.add("min", tag));
         Adapters.DOUBLE.writeJson(Double.valueOf(this.max)).ifPresent(tag -> json.add("max", tag));
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
         Adapters.DOUBLE.readJson(json.get("min")).ifPresent(value -> this.min = value);
         Adapters.DOUBLE.readJson(json.get("max")).ifPresent(value -> this.max = value);
      }
   }
}
