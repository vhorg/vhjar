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

public interface FloatRoll extends ISerializable<CompoundTag, JsonObject> {
   float getMin();

   float getMax();

   float get(RandomSource var1);

   default boolean contains(float value) {
      return value >= this.getMin() && value <= this.getMax();
   }

   static FloatRoll.Constant ofConstant(float count) {
      return new FloatRoll.Constant(count);
   }

   static FloatRoll.Uniform ofUniform(float min, float max) {
      return new FloatRoll.Uniform(min, max);
   }

   public static class Adapter extends TypeSupplierAdapter<FloatRoll> {
      public Adapter() {
         super("type", true);
         this.register("constant", FloatRoll.Constant.class, FloatRoll.Constant::new);
         this.register("uniform", FloatRoll.Uniform.class, FloatRoll.Uniform::new);
      }

      @Nullable
      protected FloatRoll readSuppliedNbt(Tag nbt) {
         if (nbt instanceof NumericTag || nbt instanceof StringTag) {
            Optional<Float> result = Adapters.FLOAT.readNbt(nbt);
            if (result.isPresent()) {
               return FloatRoll.ofConstant(result.get());
            }
         }

         return (FloatRoll)super.readSuppliedNbt(nbt);
      }

      @Nullable
      protected FloatRoll readSuppliedJson(JsonElement json) {
         if (json instanceof JsonPrimitive primitive && (primitive.isNumber() || primitive.isString())) {
            Optional<Float> result = Adapters.FLOAT.readJson(json);
            if (result.isPresent()) {
               return FloatRoll.ofConstant(result.get());
            }
         }

         return (FloatRoll)super.readSuppliedJson(json);
      }
   }

   public static class Constant implements FloatRoll {
      private float count;

      protected Constant() {
      }

      protected Constant(float count) {
         this.count = count;
      }

      public float getCount() {
         return this.count;
      }

      @Override
      public float getMin() {
         return this.getCount();
      }

      @Override
      public float getMax() {
         return this.getCount();
      }

      @Override
      public float get(RandomSource random) {
         return this.count;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.FLOAT.writeBits(Float.valueOf(this.count), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         Adapters.FLOAT.readBits(buffer).ifPresent(value -> this.count = value);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         Adapters.FLOAT.writeNbt(Float.valueOf(this.count)).ifPresent(tag -> nbt.put("count", tag));
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         Adapters.FLOAT.readNbt(nbt.get("count")).ifPresent(value -> this.count = value);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         Adapters.FLOAT.writeJson(Float.valueOf(this.count)).ifPresent(tag -> json.add("count", tag));
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
         Adapters.FLOAT.readJson(json.get("count")).ifPresent(value -> this.count = value);
      }
   }

   public static class Uniform implements FloatRoll {
      private float min;
      private float max;

      protected Uniform() {
      }

      protected Uniform(float min, float max) {
         this.min = min;
         this.max = max;
      }

      @Override
      public float getMin() {
         return this.min;
      }

      @Override
      public float getMax() {
         return this.min + (this.max - this.min);
      }

      @Override
      public float get(RandomSource random) {
         return random.nextFloat() * (this.max - this.min) + this.min;
      }

      @Override
      public void writeBits(BitBuffer buffer) {
         Adapters.FLOAT.writeBits(Float.valueOf(this.min), buffer);
         Adapters.FLOAT.writeBits(Float.valueOf(this.max), buffer);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         Adapters.FLOAT.readBits(buffer).ifPresent(value -> this.min = value);
         Adapters.FLOAT.readBits(buffer).ifPresent(value -> this.max = value);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         Adapters.FLOAT.writeNbt(Float.valueOf(this.min)).ifPresent(tag -> nbt.put("min", tag));
         Adapters.FLOAT.writeNbt(Float.valueOf(this.max)).ifPresent(tag -> nbt.put("max", tag));
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         Adapters.FLOAT.readNbt(nbt.get("min")).ifPresent(value -> this.min = value);
         Adapters.FLOAT.readNbt(nbt.get("max")).ifPresent(value -> this.max = value);
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         Adapters.FLOAT.writeJson(Float.valueOf(this.min)).ifPresent(tag -> json.add("min", tag));
         Adapters.FLOAT.writeJson(Float.valueOf(this.max)).ifPresent(tag -> json.add("max", tag));
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
         Adapters.FLOAT.readJson(json.get("min")).ifPresent(value -> this.min = value);
         Adapters.FLOAT.readJson(json.get("max")).ifPresent(value -> this.max = value);
      }
   }
}
