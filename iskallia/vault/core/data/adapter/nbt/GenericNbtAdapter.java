package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.number.BoundedByteAdapter;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;

public class GenericNbtAdapter extends NbtAdapter<Tag> {
   protected static final BoundedByteAdapter NBT_ID = new BoundedByteAdapter((byte)0, (byte)(Adapters.NBT.length - 1), false);

   public GenericNbtAdapter(boolean nullable) {
      super(nullable);
   }

   public GenericNbtAdapter asNullable() {
      return new GenericNbtAdapter(true);
   }

   @Override
   protected void writeTagBits(Tag value, BitBuffer buffer) {
      NBT_ID.writeBits(Byte.valueOf(value.getId()), buffer);
      Adapters.NBT[value.getId()].writeBits(value, buffer);
   }

   @Override
   protected Tag readTagBits(BitBuffer buffer) {
      return (Tag)Adapters.NBT[NBT_ID.readBits(buffer).orElseThrow()].readBits(buffer).orElseThrow();
   }

   @Override
   protected void writeTagBytes(Tag value, ByteBuf buffer) {
      NBT_ID.writeBytes(Byte.valueOf(value.getId()), buffer);
      Adapters.NBT[value.getId()].writeBytes(value, buffer);
   }

   @Override
   protected Tag readTagBytes(ByteBuf buffer) {
      return (Tag)Adapters.NBT[NBT_ID.readBytes(buffer).orElseThrow()].readBytes(buffer).orElseThrow();
   }

   @Override
   protected void writeTagData(Tag value, DataOutput data) throws IOException {
      NBT_ID.writeData(Byte.valueOf(value.getId()), data);
      Adapters.NBT[value.getId()].writeData(value, data);
   }

   @Override
   protected Tag readTagData(DataInput data) throws IOException {
      return (Tag)Adapters.NBT[NBT_ID.readData(data).orElseThrow()].readData(data).orElseThrow();
   }

   @Nullable
   @Override
   protected Tag writeTagNbt(Tag value) {
      return Adapters.NBT[value.getId()].writeNbt(value).orElse(null);
   }

   @Nullable
   @Override
   protected Tag readTagNbt(Tag nbt) {
      return (Tag)Adapters.NBT[nbt.getId()].readNbt(nbt).orElse(null);
   }

   @Nullable
   @Override
   protected JsonElement writeTagJson(Tag value) {
      return Adapters.NBT[value.getId()].writeJson(value).orElse(null);
   }

   @Nullable
   @Override
   protected Tag readTagJson(JsonElement json) {
      if (json instanceof JsonPrimitive value) {
         if (value.isNumber()) {
            return (Tag)Adapters.NUMERIC_NBT.readJson(value).orElse(null);
         }

         if (value.isString()) {
            return (Tag)Adapters.STRING_NBT.readJson(value).orElse(null);
         }

         if (value.isBoolean()) {
            return Adapters.BOOLEAN.writeNbt(value.getAsBoolean()).orElse(null);
         }
      } else {
         if (json instanceof JsonObject value) {
            return (Tag)Adapters.COMPOUND_NBT.readJson(value).orElse(null);
         }

         if (json instanceof JsonArray value) {
            return (Tag)Adapters.COLLECTION_NBT.readJson(value).orElse(null);
         }
      }

      return null;
   }
}
