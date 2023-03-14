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

public class FloatAdapter extends NumberAdapter<Float> {
   public FloatAdapter(boolean nullable) {
      super(nullable);
   }

   public FloatAdapter asNullable() {
      return new FloatAdapter(true);
   }

   protected void writeNumberBits(Float value, BitBuffer buffer) {
      buffer.writeFloat(value);
   }

   protected Float readNumberBits(BitBuffer buffer) {
      return buffer.readFloat();
   }

   protected void writeNumberBytes(Float value, ByteBuf buffer) {
      buffer.writeFloat(value);
   }

   protected Float readNumberBytes(ByteBuf buffer) {
      return buffer.readFloat();
   }

   protected void writeNumberData(Float value, DataOutput data) throws IOException {
      data.writeFloat(value);
   }

   protected Float readNumberData(DataInput data) throws IOException {
      return data.readFloat();
   }

   @Nullable
   protected Tag writeNumberNbt(Float value) {
      return wrap(reduce(value));
   }

   @Nullable
   protected Float readNumberNbt(Tag nbt) {
      if (nbt instanceof NumericTag numeric) {
         return numeric.getAsFloat();
      } else if (nbt instanceof ListTag list && list.size() == 1) {
         return this.readNumberNbt(list.get(0));
      } else {
         return nbt instanceof StringTag string ? parse(string.getAsString()).map(Number::floatValue).orElse(null) : null;
      }
   }

   @Nullable
   protected JsonElement writeNumberJson(Float value) {
      return new JsonPrimitive(value);
   }

   @Nullable
   protected Float readNumberJson(JsonElement json) {
      if (json instanceof JsonObject) {
         return null;
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readNumberJson(array.get(0));
      } else {
         if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
               return primitive.getAsFloat();
            }

            if (primitive.isString()) {
               return parse(primitive.getAsString()).map(Number::floatValue).orElse(null);
            }
         }

         return null;
      }
   }
}
