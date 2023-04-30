package iskallia.vault.core.data.adapter.number;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class BigIntegerAdapter extends NumberAdapter<BigInteger> {
   public BigIntegerAdapter(boolean nullable) {
      super(nullable);
   }

   public BigIntegerAdapter asNullable() {
      return new BigIntegerAdapter(true);
   }

   protected void writeNumberBits(BigInteger value, BitBuffer buffer) {
      Adapters.BYTE_ARRAY.writeBits(value.toByteArray(), buffer);
   }

   protected BigInteger readNumberBits(BitBuffer buffer) {
      return new BigInteger(Adapters.BYTE_ARRAY.readBits(buffer).orElseThrow());
   }

   protected void writeNumberBytes(BigInteger value, ByteBuf buffer) {
      Adapters.BYTE_ARRAY.writeBytes(value.toByteArray(), buffer);
   }

   protected BigInteger readNumberBytes(ByteBuf buffer) {
      return new BigInteger(Adapters.BYTE_ARRAY.readBytes(buffer).orElseThrow());
   }

   protected void writeNumberData(BigInteger value, DataOutput data) throws IOException {
      Adapters.BYTE_ARRAY.writeData(value.toByteArray(), data);
   }

   protected BigInteger readNumberData(DataInput data) throws IOException {
      return new BigInteger(Adapters.BYTE_ARRAY.readData(data).orElseThrow());
   }

   protected Tag writeNumberNbt(BigInteger value) {
      return wrap(reduce(value));
   }

   @Nullable
   protected BigInteger readNumberNbt(Tag nbt) {
      if (nbt instanceof NumericTag numeric) {
         return BigInteger.valueOf(numeric.getAsLong());
      } else if (nbt instanceof ByteArrayTag byteArray) {
         return new BigInteger(byteArray.getAsByteArray());
      } else if (nbt instanceof ListTag list && list.size() == 1) {
         return this.readNumberNbt(list.get(0));
      } else {
         return nbt instanceof StringTag string
            ? parse(string.getAsString()).map(number -> number instanceof BigInteger value ? value : BigInteger.valueOf(number.longValue())).orElse(null)
            : null;
      }
   }

   protected JsonElement writeNumberJson(BigInteger value) {
      return new JsonPrimitive(value);
   }

   @Nullable
   protected BigInteger readNumberJson(JsonElement json) {
      if (json instanceof JsonObject) {
         return null;
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readNumberJson(array.get(0));
      } else {
         if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
               return primitive.getAsBigInteger();
            }

            if (primitive.isString()) {
               return parse(primitive.getAsString())
                  .map(number -> number instanceof BigInteger value ? value : BigInteger.valueOf(number.longValue()))
                  .orElse(null);
            }
         }

         return null;
      }
   }
}
