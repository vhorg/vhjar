package iskallia.vault.core.world.loot;

import com.google.gson.JsonObject;
import iskallia.vault.core.random.RandomSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface LootRoll extends INBTSerializable<CompoundTag> {
   int get(RandomSource var1);

   int getMin();

   int getMax();

   JsonObject serializeJson();

   void deserializeJson(JsonObject var1);

   static LootRoll.Constant ofConstant(int count) {
      return new LootRoll.Constant(count);
   }

   static LootRoll.Uniform ofUniform(int min, int max) {
      return new LootRoll.Uniform(min, max);
   }

   static LootRoll fromNBT(CompoundTag nbt) {
      String var2 = nbt.getString("type");

      LootRoll roll = (LootRoll)(switch (var2) {
         case "constant" -> new LootRoll.Constant();
         case "uniform" -> new LootRoll.Uniform();
         default -> null;
      });
      if (roll != null) {
         roll.deserializeNBT(nbt);
      }

      return roll;
   }

   static LootRoll fromJson(JsonObject object) {
      Object var10000;
      if (!object.has("type")) {
         var10000 = null;
      } else {
         String var2 = object.get("type").getAsString();
         switch (var2) {
            case "constant":
               var10000 = new LootRoll.Constant();
               break;
            case "uniform":
               var10000 = new LootRoll.Uniform();
               break;
            default:
               var10000 = null;
         }
      }

      LootRoll roll = (LootRoll)var10000;
      if (roll != null) {
         roll.deserializeJson(object);
      }

      return roll;
   }

   public static class Constant implements LootRoll {
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
      public int getMin() {
         return this.count;
      }

      @Override
      public int getMax() {
         return this.count;
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("type", "constant");
         nbt.putInt("count", this.count);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.count = nbt.getInt("count");
      }

      @Override
      public JsonObject serializeJson() {
         JsonObject object = new JsonObject();
         object.addProperty("type", "constant");
         object.addProperty("count", this.count);
         return object;
      }

      @Override
      public void deserializeJson(JsonObject object) {
         this.count = object.get("count").getAsInt();
      }
   }

   public static class Uniform implements LootRoll {
      private int min;
      private int max;

      protected Uniform() {
      }

      protected Uniform(int min, int max) {
         this.min = min;
         this.max = max;
      }

      @Override
      public int getMin() {
         return this.min;
      }

      @Override
      public int getMax() {
         return this.max;
      }

      @Override
      public int get(RandomSource random) {
         return random.nextInt(this.max - this.min + 1) + this.min;
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("type", "uniform");
         nbt.putInt("min", this.min);
         nbt.putInt("max", this.max);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.min = nbt.getInt("min");
         this.max = nbt.getInt("max");
      }

      @Override
      public JsonObject serializeJson() {
         JsonObject object = new JsonObject();
         object.addProperty("type", "uniform");
         object.addProperty("min", this.min);
         object.addProperty("max", this.max);
         return object;
      }

      @Override
      public void deserializeJson(JsonObject object) {
         this.min = object.get("min").getAsInt();
         this.max = object.get("max").getAsInt();
      }
   }
}
