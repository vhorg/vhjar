package iskallia.vault.core.world.roll;

import com.google.gson.JsonObject;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.item.crystal.ObjectEntryAdapter;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;

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

   public static class Adapter extends ObjectEntryAdapter<IntRoll> {
      public Adapter() {
         super("type");
         this.register("constant", IntRoll.Constant.class, IntRoll.Constant::new);
         this.register("uniform", IntRoll.Uniform.class, IntRoll.Uniform::new);
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
         buffer.writeIntSegmented(this.count, 7);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         this.count = buffer.readIntSegmented(7);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         nbt.putInt("count", this.count);
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         this.count = nbt.getInt("count");
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         json.addProperty("count", this.count);
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
         this.count = json.get("count").getAsInt();
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
         buffer.writeIntSegmented(this.min, 7);
         buffer.writeIntSegmented(this.max, 7);
      }

      @Override
      public void readBits(BitBuffer buffer) {
         this.min = buffer.readIntSegmented(7);
         this.max = buffer.readIntSegmented(7);
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         nbt.putInt("min", this.min);
         nbt.putInt("max", this.max);
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         this.min = nbt.getInt("min");
         this.max = nbt.getInt("max");
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         json.addProperty("min", this.min);
         json.addProperty("max", this.max);
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
         this.min = json.get("min").getAsInt();
         this.max = json.get("max").getAsInt();
      }
   }
}
