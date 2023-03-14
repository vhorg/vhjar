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

public class LongAdapter extends NumberAdapter<Long> {
   public LongAdapter(boolean nullable) {
      super(nullable);
   }

   public LongAdapter asNullable() {
      return new LongAdapter(true);
   }

   protected void writeNumberBits(Long value, BitBuffer buffer) {
      buffer.writeLong(value);
   }

   protected Long readNumberBits(BitBuffer buffer) {
      return buffer.readLong();
   }

   protected void writeNumberBytes(Long value, ByteBuf buffer) {
      buffer.writeLong(value);
   }

   protected Long readNumberBytes(ByteBuf buffer) {
      return buffer.readLong();
   }

   protected void writeNumberData(Long value, DataOutput data) throws IOException {
      data.writeLong(value);
   }

   protected Long readNumberData(DataInput data) throws IOException {
      return data.readLong();
   }

   @Nullable
   protected Tag writeNumberNbt(Long value) {
      return wrap(reduce(value));
   }

   @Nullable
   protected Long readNumberNbt(Tag nbt) {
      if (nbt instanceof NumericTag numeric) {
         return numeric.getAsLong();
      } else if (nbt instanceof ListTag list && list.size() == 1) {
         return this.readNumberNbt(list.get(0));
      } else {
         return nbt instanceof StringTag string ? parse(string.getAsString()).map(Number::longValue).orElse(null) : null;
      }
   }

   @Nullable
   protected JsonElement writeNumberJson(Long value) {
      return new JsonPrimitive(value);
   }

   @Nullable
   protected Long readNumberJson(JsonElement json) {
      if (json instanceof JsonObject) {
         return null;
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readNumberJson(array.get(0));
      } else {
         if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
               return primitive.getAsLong();
            }

            if (primitive.isString()) {
               return parse(primitive.getAsString()).map(Number::longValue).orElse(null);
            }
         }

         return null;
      }
   }
}
