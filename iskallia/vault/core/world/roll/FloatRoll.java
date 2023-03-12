package iskallia.vault.core.world.roll;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import iskallia.vault.core.random.RandomSource;
import java.lang.reflect.Type;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface FloatRoll extends INBTSerializable<CompoundTag> {
   float get(RandomSource var1);

   float getMin();

   float getMax();

   JsonObject serializeJson();

   void deserializeJson(JsonObject var1);

   static FloatRoll.Constant ofConstant(float count) {
      return new FloatRoll.Constant(count);
   }

   static FloatRoll.Uniform ofUniform(float min, float max) {
      return new FloatRoll.Uniform(min, max);
   }

   static FloatRoll fromNBT(CompoundTag nbt) {
      String var2 = nbt.getString("type");

      FloatRoll roll = (FloatRoll)(switch (var2) {
         case "constant" -> new FloatRoll.Constant();
         case "uniform" -> new FloatRoll.Uniform();
         default -> null;
      });
      if (roll != null) {
         roll.deserializeNBT(nbt);
      }

      return roll;
   }

   static FloatRoll fromJson(JsonObject object) {
      Object var10000;
      if (!object.has("type")) {
         var10000 = null;
      } else {
         String var2 = object.get("type").getAsString();
         switch (var2) {
            case "constant":
               var10000 = new FloatRoll.Constant();
               break;
            case "uniform":
               var10000 = new FloatRoll.Uniform();
               break;
            default:
               var10000 = null;
         }
      }

      FloatRoll roll = (FloatRoll)var10000;
      if (roll != null) {
         roll.deserializeJson(object);
      }

      return roll;
   }

   public static class Adapter implements JsonSerializer<FloatRoll>, JsonDeserializer<FloatRoll> {
      public static final FloatRoll.Adapter INSTANCE = new FloatRoll.Adapter();

      public FloatRoll deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         String type = object.get("type").getAsString();
         switch (type) {
            case "constant":
               return FloatRoll.ofConstant(object.get("count").getAsFloat());
            case "uniform":
               return FloatRoll.ofUniform(object.get("min").getAsFloat(), object.get("max").getAsFloat());
            default:
               return null;
         }
      }

      public JsonElement serialize(FloatRoll value, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject object = new JsonObject();
         if (value instanceof FloatRoll.Constant constant) {
            object.addProperty("type", "constant");
            object.addProperty("count", constant.getCount());
         } else if (value instanceof FloatRoll.Uniform uniform) {
            object.addProperty("type", "uniform");
            object.addProperty("min", uniform.getMin());
            object.addProperty("max", uniform.getMax());
         }

         return object;
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
      public float get(RandomSource random) {
         return this.count;
      }

      @Override
      public float getMin() {
         return this.count;
      }

      @Override
      public float getMax() {
         return this.count;
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("type", "constant");
         nbt.putFloat("count", this.count);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.count = nbt.getFloat("count");
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
         this.count = object.get("count").getAsFloat();
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
         return this.max;
      }

      @Override
      public float get(RandomSource random) {
         return random.nextFloat(this.min, this.max);
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("type", "uniform");
         nbt.putFloat("min", this.min);
         nbt.putFloat("max", this.max);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.min = nbt.getFloat("min");
         this.max = nbt.getFloat("max");
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
         this.min = object.get("min").getAsFloat();
         this.max = object.get("max").getAsFloat();
      }
   }
}
