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

public class IntAdapter extends NumberAdapter<Integer> {
   public IntAdapter(boolean nullable) {
      super(nullable);
   }

   public IntAdapter asNullable() {
      return new IntAdapter(true);
   }

   protected void writeNumberBits(Integer value, BitBuffer buffer) {
      buffer.writeInt(value);
   }

   protected Integer readNumberBits(BitBuffer buffer) {
      return buffer.readInt();
   }

   protected void writeNumberBytes(Integer value, ByteBuf buffer) {
      buffer.writeInt(value);
   }

   protected Integer readNumberBytes(ByteBuf buffer) {
      return buffer.readInt();
   }

   protected void writeNumberData(Integer value, DataOutput data) throws IOException {
      data.writeInt(value);
   }

   protected Integer readNumberData(DataInput data) throws IOException {
      return data.readInt();
   }

   protected Tag writeNumberNbt(Integer value) {
      return wrap(reduce(value));
   }

   @Nullable
   protected Integer readNumberNbt(Tag nbt) {
      if (nbt instanceof NumericTag numeric) {
         return numeric.getAsInt();
      } else if (nbt instanceof ListTag list && list.size() == 1) {
         return this.readNumberNbt(list.get(0));
      } else {
         return nbt instanceof StringTag string ? parse(string.getAsString()).map(Number::intValue).orElse(null) : null;
      }
   }

   protected JsonElement writeNumberJson(Integer value) {
      return new JsonPrimitive(value);
   }

   @Nullable
   protected Integer readNumberJson(JsonElement json) {
      if (json instanceof JsonObject) {
         return null;
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readNumberJson(array.get(0));
      } else {
         if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
               return primitive.getAsInt();
            }

            if (primitive.isString()) {
               return parse(primitive.getAsString()).map(Number::intValue).orElse(null);
            }
         }

         return null;
      }
   }
}
