package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class ListNbtAdapter extends NbtAdapter<ListTag> {
   protected static final String[] ID_TO_KEY = new String[]{
      "END", "BYTE", "SHORT", "INT", "LONG", "FLOAT", "DOUBLE", "BYTE_ARRAY", "STRING", "LIST", "COMPOUND", "INT_ARRAY", "LONG_ARRAY"
   };
   protected static final Object2IntMap<String> KEY_TO_ID = new Object2IntOpenHashMap();

   public ListNbtAdapter(boolean nullable) {
      super(nullable);
   }

   public ListNbtAdapter asNullable() {
      return new ListNbtAdapter(this.isNullable());
   }

   protected void writeTagBits(ListTag value, BitBuffer buffer) {
      GenericNbtAdapter.NBT_ID.writeBits(Byte.valueOf(value.getElementType()), buffer);
      Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(value.size()), buffer);

      for (Tag element : value) {
         Adapters.NBT[value.getId()].writeBits(element, buffer);
      }
   }

   protected ListTag readTagBits(BitBuffer buffer) {
      ListTag list = new ListTag();
      byte id = GenericNbtAdapter.NBT_ID.readBits(buffer).orElseThrow();
      list.add((Tag)Adapters.NBT[id].readBits(buffer).orElseThrow());
      return list;
   }

   protected void writeTagBytes(ListTag value, ByteBuf buffer) {
      GenericNbtAdapter.NBT_ID.writeBytes(Byte.valueOf(value.getElementType()), buffer);
      Adapters.INT_SEGMENTED_3.writeBytes(Integer.valueOf(value.size()), buffer);

      for (Tag element : value) {
         Adapters.NBT[value.getId()].writeBytes(element, buffer);
      }
   }

   protected ListTag readTagBytes(ByteBuf buffer) {
      ListTag list = new ListTag();
      byte id = GenericNbtAdapter.NBT_ID.readBytes(buffer).orElseThrow();
      list.add((Tag)Adapters.NBT[id].readBytes(buffer).orElseThrow());
      return list;
   }

   protected void writeTagData(ListTag value, DataOutput data) throws IOException {
      GenericNbtAdapter.NBT_ID.writeData(Byte.valueOf(value.getElementType()), data);
      Adapters.INT_SEGMENTED_3.writeData(Integer.valueOf(value.size()), data);

      for (Tag element : value) {
         Adapters.NBT[value.getId()].writeData(element, data);
      }
   }

   protected ListTag readTagData(DataInput data) throws IOException {
      ListTag list = new ListTag();
      byte id = GenericNbtAdapter.NBT_ID.readData(data).orElseThrow();
      list.add((Tag)Adapters.NBT[id].readData(data).orElseThrow());
      return list;
   }

   protected Tag writeTagNbt(ListTag value) {
      return value.copy();
   }

   @Nullable
   protected ListTag readTagNbt(Tag nbt) {
      return nbt instanceof ListTag tag ? tag.copy() : null;
   }

   protected JsonElement writeTagJson(ListTag value) {
      JsonArray array = new JsonArray();
      array.add(KEY_TO_ID.getInt(value.getElementType()));

      for (Tag tag : value) {
         Adapters.NBT[value.getElementType()].writeJson(tag).ifPresent(o -> array.add(o));
      }

      return array;
   }

   @Nullable
   protected ListTag readTagJson(JsonElement json) {
      if (json instanceof JsonArray array && array.size() > 0) {
         ListTag list = new ListTag();
         int id;
         if (array.get(0) instanceof JsonPrimitive primitive && primitive.isString() && (id = KEY_TO_ID.getInt(primitive.getAsString())) >= 0) {
            for (int i = 1; i < array.size(); i++) {
               Adapters.NBT[id].readJson(array.get(i)).ifPresent(tag -> list.add(tag));
            }
         } else {
            for (int i = 0; i < array.size(); i++) {
               JsonElement element = array.get(i);

               try {
                  Adapters.GENERIC_NBT.readJson(element).ifPresent(list::add);
               } catch (UnsupportedOperationException var9) {
                  return null;
               }
            }
         }

         return list;
      } else {
         return null;
      }
   }

   static {
      for (int i = 0; i < ID_TO_KEY.length; i++) {
         KEY_TO_ID.put(ID_TO_KEY[i], i);
      }

      KEY_TO_ID.defaultReturnValue(-1);
   }
}
