package iskallia.vault.core.data.adapter.number;

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

public class ShortAdapter extends NumberAdapter<Short> {
   public ShortAdapter(boolean nullable) {
      super(nullable);
   }

   public ShortAdapter asNullable() {
      return new ShortAdapter(true);
   }

   protected void writeNumberBits(Short value, BitBuffer buffer) {
      buffer.writeShort(value);
   }

   protected Short readNumberBits(BitBuffer buffer) {
      return buffer.readShort();
   }

   protected void writeNumberBytes(Short value, ByteBuf buffer) {
      buffer.writeShort(value);
   }

   protected Short readNumberBytes(ByteBuf buffer) {
      return buffer.readShort();
   }

   protected void writeNumberData(Short value, DataOutput data) throws IOException {
      data.writeShort(value);
   }

   protected Short readNumberData(DataInput data) throws IOException {
      return data.readShort();
   }

   protected Tag writeNumberNbt(Short value) {
      return wrap(reduce(value));
   }

   @Nullable
   protected Short readNumberNbt(Tag nbt) {
      if (nbt instanceof NumericTag numeric) {
         return numeric.getAsShort();
      } else if (nbt instanceof ListTag list && list.size() == 1) {
         return this.readNumberNbt(list.get(0));
      } else {
         return nbt instanceof StringTag string ? parse(string.getAsString()).map(Number::shortValue).orElse(null) : null;
      }
   }

   protected JsonElement writeNumberJson(Short value) {
      return new JsonPrimitive(value);
   }

   @Nullable
   protected Short readNumberJson(JsonElement json) {
      if (json instanceof JsonObject) {
         return null;
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readNumberJson(array.get(0));
      } else {
         if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
               return primitive.getAsShort();
            }

            if (primitive.isString()) {
               return parse(primitive.getAsString()).map(Number::shortValue).orElse(null);
            }
         }

         return null;
      }
   }
}
