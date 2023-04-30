package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

public class CompoundNbtAdapter extends NbtAdapter<CompoundTag> {
   public CompoundNbtAdapter(boolean nullable) {
      super(nullable);
   }

   public CompoundNbtAdapter asNullable() {
      return new CompoundNbtAdapter(true);
   }

   protected void writeTagBits(CompoundTag value, BitBuffer buffer) {
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(value.size()), buffer);

      for (String key : value.getAllKeys()) {
         Adapters.UTF_8.writeBits(key, buffer);
         Adapters.GENERIC_NBT.writeBits(value.get(key), buffer);
      }
   }

   protected CompoundTag readTagBits(BitBuffer buffer) {
      CompoundTag compound = new CompoundTag();
      int size = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();

      for (int i = 0; i < size; i++) {
         String key = Adapters.UTF_8.readBits(buffer).orElseThrow();
         Tag tag = Adapters.GENERIC_NBT.readBits(buffer).orElseThrow();
         compound.put(key, tag);
      }

      return compound;
   }

   protected void writeTagBytes(CompoundTag value, ByteBuf buffer) {
      Adapters.INT_SEGMENTED_3.writeBytes(Integer.valueOf(value.size()), buffer);

      for (String key : value.getAllKeys()) {
         Adapters.UTF_8.writeBytes(key, buffer);
         Adapters.GENERIC_NBT.writeBytes(value.get(key), buffer);
      }
   }

   protected CompoundTag readTagBytes(ByteBuf buffer) {
      CompoundTag compound = new CompoundTag();
      int size = Adapters.INT_SEGMENTED_3.readBytes(buffer).orElseThrow();

      for (int i = 0; i < size; i++) {
         String key = Adapters.UTF_8.readBytes(buffer).orElseThrow();
         Tag tag = Adapters.GENERIC_NBT.readBytes(buffer).orElseThrow();
         compound.put(key, tag);
      }

      return compound;
   }

   protected void writeTagData(CompoundTag value, DataOutput data) throws IOException {
      Adapters.INT_SEGMENTED_3.writeData(Integer.valueOf(value.size()), data);

      for (String key : value.getAllKeys()) {
         Adapters.UTF_8.writeData(key, data);
         Adapters.GENERIC_NBT.writeData(value.get(key), data);
      }
   }

   protected CompoundTag readTagData(DataInput data) throws IOException {
      CompoundTag compound = new CompoundTag();
      int size = Adapters.INT_SEGMENTED_3.readData(data).orElseThrow();

      for (int i = 0; i < size; i++) {
         String key = Adapters.UTF_8.readData(data).orElseThrow();
         Tag tag = Adapters.GENERIC_NBT.readData(data).orElseThrow();
         compound.put(key, tag);
      }

      return compound;
   }

   protected Tag writeTagNbt(CompoundTag value) {
      return value.copy();
   }

   @Nullable
   protected CompoundTag readTagNbt(Tag nbt) {
      return nbt instanceof CompoundTag tag ? tag.copy() : null;
   }

   protected JsonElement writeTagJson(CompoundTag value) {
      return new JsonPrimitive(value.getAsString());
   }

   @Nullable
   protected CompoundTag readTagJson(JsonElement json) {
      if (json instanceof JsonPrimitive primitive && primitive.isString()) {
         try {
            return TagParser.parseTag(primitive.getAsString());
         } catch (CommandSyntaxException var8) {
            return null;
         }
      } else if (!(json instanceof JsonObject object)) {
         return null;
      } else {
         CompoundTag nbt = new CompoundTag();

         for (String key : object.keySet()) {
            JsonElement element = object.get(key);
            Adapters.GENERIC_NBT.readJson(element).ifPresent(tag -> nbt.put(key, tag));
         }

         return nbt;
      }
   }
}
