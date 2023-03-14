package iskallia.vault.core.data.adapter.nbt;

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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;

public class GenericNbtAdapter extends NbtAdapter<Tag> {
   public GenericNbtAdapter(boolean nullable) {
      super(Tag.class, nullable);
   }

   public GenericNbtAdapter asNullable() {
      return new GenericNbtAdapter(true);
   }

   @Override
   protected void writeTagBits(Tag value, BitBuffer buffer) {
      buffer.writeIntBounded(value.getId(), 0, 12);
      ADAPTERS[value.getId()].writeBits(value, buffer);
   }

   @Override
   protected Tag readTagBits(BitBuffer buffer) {
      return (Tag)ADAPTERS[buffer.readIntBounded(0, 12)].readBits(buffer).orElseThrow();
   }

   @Override
   protected void writeTagBytes(Tag value, ByteBuf buffer) {
      buffer.writeByte(value.getId());
      ADAPTERS[value.getId()].writeBytes(value, buffer);
   }

   @Override
   protected Tag readTagBytes(ByteBuf buffer) {
      return (Tag)ADAPTERS[buffer.readByte()].readBytes(buffer).orElseThrow();
   }

   @Override
   protected void writeTagData(Tag value, DataOutput data) throws IOException {
      data.writeByte(value.getId());
      ADAPTERS[value.getId()].writeData(value, data);
   }

   @Override
   protected Tag readTagData(DataInput data) throws IOException {
      return (Tag)ADAPTERS[data.readByte()].readData(data).orElseThrow();
   }

   @Nullable
   @Override
   protected Tag writeTagNbt(Tag value) {
      return value;
   }

   @Nullable
   @Override
   protected Tag readTagNbt(Tag nbt) {
      return nbt;
   }

   @Nullable
   @Override
   protected JsonElement writeTagJson(Tag value) {
      return ADAPTERS[value.getId()].writeJson(value).orElse(null);
   }

   @Nullable
   @Override
   protected Tag readTagJson(JsonElement json) {
      if (json instanceof JsonPrimitive value) {
         if (value.isNumber()) {
            return Adapters.NUMERIC.writeNbt(value.getAsNumber()).orElse(null);
         }

         if (value.isString()) {
            return Adapters.UTF_8.writeNbt(value.getAsString()).orElse(null);
         }

         if (value.isBoolean()) {
            return Adapters.BOOLEAN.writeNbt(value.getAsBoolean()).orElse(null);
         }
      } else {
         if (json instanceof JsonObject value) {
            CompoundTag tag = new CompoundTag();

            for (String key : value.keySet()) {
               this.readJson(value.get(key)).ifPresent(tag1 -> tag.put(key, tag1));
            }

            return tag;
         }

         if (json instanceof JsonArray value) {
            String hint = null;
            if (!value.isEmpty() && value.get(0) instanceof JsonPrimitive primitive && primitive.isString()) {
               hint = primitive.getAsString();
            }

            if (hint == null) {
               ListTag tag = new ListTag();

               for (JsonElement element : value) {
                  this.readJson(element).ifPresent(tag::add);
               }

               return tag;
            }

            if ("B".equals(hint)) {
               List<Byte> list = new ArrayList<>();

               for (int i = 1; i < value.size(); i++) {
                  list.add(Adapters.BYTE.readJson(value.get(i)).orElseThrow());
               }

               return new ByteArrayTag(list);
            }

            if ("I".equals(hint)) {
               List<Integer> list = new ArrayList<>();

               for (int i = 1; i < value.size(); i++) {
                  list.add(Adapters.INT.readJson(value.get(i)).orElseThrow());
               }

               return new IntArrayTag(list);
            }

            if ("L".equals(hint)) {
               List<Long> list = new ArrayList<>();

               for (int i = 1; i < value.size(); i++) {
                  list.add(Adapters.LONG.readJson(value.get(i)).orElseThrow());
               }

               return new LongArrayTag(list);
            }
         }
      }

      return null;
   }
}
