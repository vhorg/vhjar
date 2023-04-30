package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.number.BoundedIntAdapter;
import iskallia.vault.core.net.BitBuffer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public class CollectionNbtAdapter extends NbtAdapter<CollectionTag<?>> {
   private static NbtAdapter[] ADAPTERS = new NbtAdapter[]{Adapters.BYTE_ARRAY_NBT, Adapters.INT_ARRAY_NBT, Adapters.LONG_ARRAY_NBT, Adapters.LIST_NBT};
   private static final BoundedIntAdapter ID = new BoundedIntAdapter(0, ADAPTERS.length - 1, false);
   private static final Object2IntMap<Class<?>> TYPE_TO_ID = new Object2IntOpenHashMap();

   public CollectionNbtAdapter(boolean nullable) {
      super(nullable);
   }

   public CollectionNbtAdapter asNullable() {
      return new CollectionNbtAdapter(this.isNullable());
   }

   protected void writeTagBits(CollectionTag<?> value, BitBuffer buffer) {
      int id = TYPE_TO_ID.getInt(value.getClass());
      ID.writeBits(Integer.valueOf(id), buffer);
      ADAPTERS[id].writeBits((Tag)value, buffer);
   }

   protected CollectionTag<?> readTagBits(BitBuffer buffer) {
      int id = ID.readBits(buffer).orElseThrow();
      return (CollectionTag<?>)ADAPTERS[id].readBits(buffer).orElse(null);
   }

   protected void writeTagBytes(CollectionTag<?> value, ByteBuf buffer) {
      int id = TYPE_TO_ID.getInt(value.getClass());
      ID.writeBytes(Integer.valueOf(id), buffer);
      ADAPTERS[id].writeBytes((Tag)value, buffer);
   }

   protected CollectionTag<?> readTagBytes(ByteBuf buffer) {
      int id = ID.readBytes(buffer).orElseThrow();
      return (CollectionTag<?>)ADAPTERS[id].readBytes(buffer).orElse(null);
   }

   protected void writeTagData(CollectionTag<?> value, DataOutput data) throws IOException {
      int id = TYPE_TO_ID.getInt(value.getClass());
      ID.writeData(Integer.valueOf(id), data);
      ADAPTERS[id].writeData((Tag)value, data);
   }

   protected CollectionTag<?> readTagData(DataInput data) throws IOException {
      int id = ID.readData(data).orElseThrow();
      return (CollectionTag<?>)ADAPTERS[id].readData(data).orElse(null);
   }

   protected Tag writeTagNbt(CollectionTag<?> value) {
      return value;
   }

   @Nullable
   protected CollectionTag<?> readTagNbt(Tag nbt) {
      return nbt instanceof CollectionTag<?> tag ? tag : null;
   }

   protected JsonElement writeTagJson(CollectionTag<?> value) {
      int id = TYPE_TO_ID.getInt(value.getClass());
      return ADAPTERS[id].writeJson((Tag)value).orElse(null);
   }

   @Nullable
   protected CollectionTag<?> readTagJson(JsonElement json) {
      if (json instanceof JsonArray array && array.size() > 0) {
         String key = array.get(0) instanceof JsonPrimitive primitive && primitive.isString() ? primitive.getAsString() : null;
         if ("B".equals(key)) {
            return (CollectionTag<?>)Adapters.BYTE_ARRAY_NBT.readJson(json).orElse(null);
         } else if ("I".equals(key)) {
            return (CollectionTag<?>)Adapters.INT_ARRAY_NBT.readJson(json).orElse(null);
         } else {
            return "L".equals(key)
               ? (CollectionTag)Adapters.LONG_ARRAY_NBT.readJson(json).orElse(null)
               : (CollectionTag)Adapters.LIST_NBT.readJson(json).orElse(null);
         }
      } else {
         return null;
      }
   }
}
