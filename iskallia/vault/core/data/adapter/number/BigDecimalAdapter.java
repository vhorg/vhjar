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
import java.math.BigDecimal;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public class BigDecimalAdapter extends NumberAdapter<BigDecimal> {
   public BigDecimalAdapter(boolean nullable) {
      super(nullable);
   }

   public BigDecimalAdapter asNullable() {
      return new BigDecimalAdapter(true);
   }

   protected void writeNumberBits(BigDecimal value, BitBuffer buffer) {
      Adapters.BIG_INTEGER.writeBits(value.unscaledValue(), buffer);
      Adapters.INT.writeBits(Integer.valueOf(value.scale()), buffer);
   }

   protected BigDecimal readNumberBits(BitBuffer buffer) {
      return new BigDecimal(Adapters.BIG_INTEGER.readBits(buffer).orElseThrow(), Adapters.INT.readBits(buffer).orElseThrow());
   }

   protected void writeNumberBytes(BigDecimal value, ByteBuf buffer) {
      Adapters.BIG_INTEGER.writeBytes(value.unscaledValue(), buffer);
      Adapters.INT.writeBytes(Integer.valueOf(value.scale()), buffer);
   }

   protected BigDecimal readNumberBytes(ByteBuf buffer) {
      return new BigDecimal(Adapters.BIG_INTEGER.readBytes(buffer).orElseThrow(), Adapters.INT.readBytes(buffer).orElseThrow());
   }

   protected void writeNumberData(BigDecimal value, DataOutput data) throws IOException {
      Adapters.BIG_INTEGER.writeData(value.unscaledValue(), data);
      Adapters.INT.writeData(Integer.valueOf(value.scale()), data);
   }

   protected BigDecimal readNumberData(DataInput data) throws IOException {
      return new BigDecimal(Adapters.BIG_INTEGER.readData(data).orElseThrow(), Adapters.INT.readData(data).orElseThrow());
   }

   protected Tag writeNumberNbt(BigDecimal value) {
      return wrap(reduce(value));
   }

   @Nullable
   protected BigDecimal readNumberNbt(Tag nbt) {
      if (nbt instanceof NumericTag numeric) {
         return BigDecimal.valueOf(numeric.getAsLong());
      } else if (nbt instanceof ListTag list && list.size() == 1) {
         return this.readNumberNbt(list.get(0));
      } else {
         return nbt instanceof StringTag string
            ? parse(string.getAsString()).map(number -> number instanceof BigDecimal value ? value : BigDecimal.valueOf(number.doubleValue())).orElse(null)
            : null;
      }
   }

   protected JsonElement writeNumberJson(BigDecimal value) {
      return new JsonPrimitive(value);
   }

   @Nullable
   protected BigDecimal readNumberJson(JsonElement json) {
      if (json instanceof JsonObject) {
         return null;
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readNumberJson(array.get(0));
      } else {
         if (json instanceof JsonPrimitive primitive) {
            if (primitive.isNumber()) {
               return primitive.getAsBigDecimal();
            }

            if (primitive.isString()) {
               return parse(primitive.getAsString())
                  .map(number -> number instanceof BigDecimal value ? value : BigDecimal.valueOf(number.doubleValue()))
                  .orElse(null);
            }
         }

         return null;
      }
   }
}
