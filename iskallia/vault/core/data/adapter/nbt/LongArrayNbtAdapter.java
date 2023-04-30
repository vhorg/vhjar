package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class LongArrayNbtAdapter extends NbtAdapter<LongArrayTag> {
   public LongArrayNbtAdapter(boolean nullable) {
      super(nullable);
   }

   public LongArrayNbtAdapter asNullable() {
      return new LongArrayNbtAdapter(this.isNullable());
   }

   protected void writeTagBits(LongArrayTag value, BitBuffer buffer) {
      Adapters.LONG_ARRAY.writeBits(value.getAsLongArray(), buffer);
   }

   protected LongArrayTag readTagBits(BitBuffer buffer) {
      return new LongArrayTag(Adapters.LONG_ARRAY.readBits(buffer).orElseThrow());
   }

   protected void writeTagBytes(LongArrayTag value, ByteBuf buffer) {
      Adapters.LONG_ARRAY.writeBytes(value.getAsLongArray(), buffer);
   }

   protected LongArrayTag readTagBytes(ByteBuf buffer) {
      return new LongArrayTag(Adapters.LONG_ARRAY.readBytes(buffer).orElseThrow());
   }

   protected void writeTagData(LongArrayTag value, DataOutput data) throws IOException {
      Adapters.LONG_ARRAY.writeData(value.getAsLongArray(), data);
   }

   protected LongArrayTag readTagData(DataInput data) throws IOException {
      return new LongArrayTag(Adapters.LONG_ARRAY.readData(data).orElseThrow());
   }

   protected Tag writeTagNbt(LongArrayTag value) {
      return value.copy();
   }

   @Nullable
   protected LongArrayTag readTagNbt(Tag nbt) {
      return nbt instanceof LongArrayTag tag ? tag.copy() : null;
   }

   protected JsonElement writeTagJson(LongArrayTag value) {
      return (JsonElement)Adapters.LONG_ARRAY.writeJson(value.getAsLongArray()).map(array -> {
         JsonArray copy = new JsonArray();
         copy.add("L");

         for (int i = 1; i < array.size(); i++) {
            copy.add(array.get(i));
         }

         return copy;
      }).orElse(null);
   }

   @Nullable
   protected LongArrayTag readTagJson(JsonElement json) {
      if (!(json instanceof JsonArray array)) {
         return null;
      } else {
         if (array.size() > 0 && array.get(0) instanceof JsonPrimitive primitive && primitive.getAsString().equals("L")) {
            JsonArray copy = new JsonArray();

            for (int i = 1; i < array.size(); i++) {
               copy.add(array.get(i));
            }

            array = copy;
         }

         return Adapters.LONG_ARRAY.readJson(array).<LongArrayTag>map(LongArrayTag::new).orElse(null);
      }
   }
}
