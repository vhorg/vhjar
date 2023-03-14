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

public class NumericAdapter extends NumberAdapter<Number> {
   public NumericAdapter(boolean nullable) {
      super(nullable);
   }

   public NumericAdapter asNullable() {
      return new NumericAdapter(true);
   }

   @Override
   protected void writeNumberBits(Number value, BitBuffer buffer) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Number readNumberBits(BitBuffer buffer) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void writeNumberBytes(Number value, ByteBuf buffer) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Number readNumberBytes(ByteBuf buffer) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void writeNumberData(Number value, DataOutput data) throws IOException {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Number readNumberData(DataInput data) throws IOException {
      throw new UnsupportedOperationException();
   }

   @Nullable
   @Override
   protected Tag writeNumberNbt(Number value) {
      return wrap(reduce(value));
   }

   @Nullable
   @Override
   protected Number readNumberNbt(Tag nbt) {
      if (nbt instanceof NumericTag numeric) {
         return numeric.getAsNumber();
      } else if (nbt instanceof ListTag list && list.size() == 1) {
         return this.readNumberNbt(list.get(0));
      } else {
         return nbt instanceof StringTag string ? parse(string.toString()).orElse(null) : null;
      }
   }

   @Nullable
   @Override
   protected JsonElement writeNumberJson(Number value) {
      return new JsonPrimitive(value);
   }

   @Nullable
   @Override
   protected Number readNumberJson(JsonElement json) {
      if (json instanceof JsonObject) {
         return null;
      } else if (json instanceof JsonArray array && array.size() == 1) {
         return this.readNumberJson(array.get(0));
      } else {
         return json instanceof JsonPrimitive primitive ? parse(primitive.getAsString()).orElse(null) : null;
      }
   }
}
