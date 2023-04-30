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
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class IntArrayNbtAdapter extends NbtAdapter<IntArrayTag> {
   public IntArrayNbtAdapter(boolean nullable) {
      super(nullable);
   }

   public IntArrayNbtAdapter asNullable() {
      return new IntArrayNbtAdapter(this.isNullable());
   }

   protected void writeTagBits(IntArrayTag value, BitBuffer buffer) {
      Adapters.INT_ARRAY.writeBits(value.getAsIntArray(), buffer);
   }

   protected IntArrayTag readTagBits(BitBuffer buffer) {
      return new IntArrayTag(Adapters.INT_ARRAY.readBits(buffer).orElseThrow());
   }

   protected void writeTagBytes(IntArrayTag value, ByteBuf buffer) {
      Adapters.INT_ARRAY.writeBytes(value.getAsIntArray(), buffer);
   }

   protected IntArrayTag readTagBytes(ByteBuf buffer) {
      return new IntArrayTag(Adapters.INT_ARRAY.readBytes(buffer).orElseThrow());
   }

   protected void writeTagData(IntArrayTag value, DataOutput data) throws IOException {
      Adapters.INT_ARRAY.writeData(value.getAsIntArray(), data);
   }

   protected IntArrayTag readTagData(DataInput data) throws IOException {
      return new IntArrayTag(Adapters.INT_ARRAY.readData(data).orElseThrow());
   }

   protected Tag writeTagNbt(IntArrayTag value) {
      return value.copy();
   }

   @Nullable
   protected IntArrayTag readTagNbt(Tag nbt) {
      return nbt instanceof IntArrayTag tag ? tag.copy() : null;
   }

   protected JsonElement writeTagJson(IntArrayTag value) {
      return (JsonElement)Adapters.INT_ARRAY.writeJson(value.getAsIntArray()).map(array -> {
         JsonArray copy = new JsonArray();
         copy.add("I");

         for (int i = 1; i < array.size(); i++) {
            copy.add(array.get(i));
         }

         return copy;
      }).orElse(null);
   }

   @Nullable
   protected IntArrayTag readTagJson(JsonElement json) {
      if (!(json instanceof JsonArray array)) {
         return null;
      } else {
         if (array.size() > 0 && array.get(0) instanceof JsonPrimitive primitive && primitive.getAsString().equals("I")) {
            JsonArray copy = new JsonArray();

            for (int i = 1; i < array.size(); i++) {
               copy.add(array.get(i));
            }

            array = copy;
         }

         return Adapters.INT_ARRAY.readJson(array).<IntArrayTag>map(IntArrayTag::new).orElse(null);
      }
   }
}
