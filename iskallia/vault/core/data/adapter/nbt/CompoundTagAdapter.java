package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import org.jetbrains.annotations.Nullable;

public class CompoundTagAdapter extends NbtAdapter<CompoundTag> {
   public CompoundTagAdapter(boolean nullable) {
      super(CompoundTag.class, nullable);
   }

   public CompoundTagAdapter asNullable() {
      return new CompoundTagAdapter(true);
   }

   protected void writeTagBits(CompoundTag value, BitBuffer buffer) {
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(value.size()), buffer);

      for (String key : value.getAllKeys()) {
         Tag tag = value.get(key);
         Adapters.UTF_8.writeBits(key, buffer);
         TAG_ID.writeBits(Byte.valueOf(value.getId()), buffer);
         ADAPTERS[tag.getId()].writeBits(tag, buffer);
      }
   }

   protected CompoundTag readTagBits(BitBuffer buffer) {
      CompoundTag compound = new CompoundTag();
      int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

      for (int i = 0; i < size; i++) {
         String key = Adapters.UTF_8.readBits(buffer).orElseThrow();
         Tag tag = (Tag)ADAPTERS[TAG_ID.readBits(buffer).orElseThrow()].readBits(buffer).orElseThrow();
         compound.put(key, tag);
      }

      return compound;
   }

   protected void writeTagBytes(CompoundTag value, ByteBuf buffer) {
      Adapters.INT_SEGMENTED_3.writeBytes(Integer.valueOf(value.size()), buffer);

      for (String key : value.getAllKeys()) {
         Tag tag = value.get(key);
         Adapters.UTF_8.writeBytes(key, buffer);
         TAG_ID.writeBytes(Byte.valueOf(value.getId()), buffer);
         ADAPTERS[tag.getId()].writeBytes(tag, buffer);
      }
   }

   protected CompoundTag readTagBytes(ByteBuf buffer) {
      CompoundTag compound = new CompoundTag();
      int size = Adapters.INT_SEGMENTED_3.readBytes(buffer).orElseThrow();

      for (int i = 0; i < size; i++) {
         String key = Adapters.UTF_8.readBytes(buffer).orElseThrow();
         Tag tag = (Tag)ADAPTERS[TAG_ID.readBytes(buffer).orElseThrow()].readBytes(buffer).orElseThrow();
         compound.put(key, tag);
      }

      return compound;
   }

   protected void writeTagData(CompoundTag value, DataOutput data) throws IOException {
      Adapters.INT_SEGMENTED_3.writeData(Integer.valueOf(value.size()), data);

      for (String key : value.getAllKeys()) {
         Tag tag = value.get(key);
         Adapters.UTF_8.writeData(key, data);
         TAG_ID.writeData(Byte.valueOf(value.getId()), data);
         ADAPTERS[tag.getId()].writeData(tag, data);
      }
   }

   protected CompoundTag readTagData(DataInput data) throws IOException {
      CompoundTag compound = new CompoundTag();
      int size = Adapters.INT_SEGMENTED_3.readData(data).orElseThrow();

      for (int i = 0; i < size; i++) {
         String key = Adapters.UTF_8.readData(data).orElseThrow();
         Tag tag = (Tag)ADAPTERS[TAG_ID.readData(data).orElseThrow()].readData(data).orElseThrow();
         compound.put(key, tag);
      }

      return compound;
   }

   protected JsonElement writeTagJson(CompoundTag value) {
      return new JsonPrimitive(value.getAsString());
   }

   @Nullable
   protected CompoundTag readTagJson(JsonElement json) {
      if (json instanceof JsonPrimitive primitive && primitive.isString()) {
         try {
            return TagParser.parseTag(primitive.getAsString());
         } catch (CommandSyntaxException var4) {
            return null;
         }
      } else {
         return null;
      }
   }
}
