package iskallia.vault.core.data.adapter.primitive;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class DoubleAdapter extends NumberAdapter<Double> {
   public DoubleAdapter(boolean nullable) {
      super(nullable);
   }

   public DoubleAdapter asNullable() {
      return new DoubleAdapter(true);
   }

   protected void writeNumberBits(Double value, BitBuffer buffer) {
      buffer.writeDouble(value);
   }

   protected Double readNumberBits(BitBuffer buffer) {
      return buffer.readDouble();
   }

   protected void writeNumberBytes(Double value, ByteBuf buffer) {
      buffer.writeDouble(value);
   }

   protected Double readNumberBytes(ByteBuf buffer) {
      return buffer.readDouble();
   }

   protected void writeNumberData(Double value, DataOutput data) throws IOException {
      data.writeDouble(value);
   }

   protected Double readNumberData(DataInput data) throws IOException {
      return data.readDouble();
   }

   @Nullable
   protected Tag writeNumberNbt(Double value) {
      return wrap(reduce(value));
   }

   @Nullable
   protected Double readNumberNbt(Tag nbt) {
      if (nbt instanceof NumericTag numeric) {
         return numeric.getAsDouble();
      } else if (nbt instanceof ListTag list && list.size() == 1) {
         return this.readNumberNbt(list.get(0));
      } else {
         return nbt instanceof StringTag string ? parse(string.getAsString()).map(Number::doubleValue).orElse(null) : null;
      }
   }

   @Nullable
   protected JsonElement writeNumberJson(Double value) {
      return new JsonPrimitive(value);
   }

   @Nullable
   protected Double readNumberJson(JsonElement json) {
      if (json instanceof JsonObject) {
         return null;
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readNumberJson(array.get(0));
      } else {
         if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
               return primitive.getAsDouble();
            }

            if (primitive.isString()) {
               return parse(primitive.getAsString()).map(Number::doubleValue).orElse(null);
            }
         }

         return null;
      }
   }
}
