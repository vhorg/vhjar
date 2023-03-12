package iskallia.vault.core.data.adapter.array;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import iskallia.vault.item.crystal.data.adapter.IByteAdapter;
import iskallia.vault.item.crystal.data.adapter.IDataAdapter;
import iskallia.vault.item.crystal.data.adapter.IJsonAdapter;
import iskallia.vault.item.crystal.data.adapter.INbtAdapter;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

public class ByteArrayAdapter implements ISimpleAdapter<byte[], Tag, JsonArray> {
   private final Object elementAdapter;
   private final boolean nullable;

   public ByteArrayAdapter(Object elementAdapter, boolean nullable) {
      this.elementAdapter = elementAdapter;
      this.nullable = nullable;
   }

   public Object getElementAdapter() {
      return this.elementAdapter;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public ByteArrayAdapter asNullable() {
      return new ByteArrayAdapter(this.elementAdapter, true);
   }

   public final void writeBits(@Nullable byte[] value, BitBuffer buffer) {
      if (this.elementAdapter instanceof IBitAdapter adapter) {
         if (this.nullable) {
            buffer.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.length), buffer);

            for (byte element : value) {
               adapter.writeBits(element, buffer, null);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public final Optional<byte[]> readBits(BitBuffer buffer) {
      if (!(this.elementAdapter instanceof IBitAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         byte[] value = new byte[Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow()];

         for (int i = 0; i < value.length; i++) {
            value[i] = adapter.readBits(buffer, null).orElse((byte)0);
         }

         return Optional.of(value);
      }
   }

   public final void writeBytes(@Nullable byte[] value, ByteBuf buffer) {
      if (this.elementAdapter instanceof IByteAdapter adapter) {
         if (this.nullable) {
            buffer.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_7.writeBytes(Integer.valueOf(value.length), buffer);

            for (int element : value) {
               adapter.writeBytes(element, buffer, null);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public final Optional<byte[]> readBytes(ByteBuf buffer) {
      if (!(this.elementAdapter instanceof IByteAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         byte[] value = new byte[Adapters.INT_SEGMENTED_7.readBytes(buffer).orElseThrow()];

         for (int i = 0; i < value.length; i++) {
            value[i] = adapter.readBytes(buffer, null).orElse((byte)0);
         }

         return Optional.of(value);
      }
   }

   public void writeData(@Nullable byte[] value, DataOutput data) throws IOException {
      if (this.elementAdapter instanceof IDataAdapter adapter) {
         if (this.nullable) {
            data.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_7.writeData(Integer.valueOf(value.length), data);

            for (int element : value) {
               adapter.writeData(element, data, null);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public Optional<byte[]> readData(DataInput data) throws IOException {
      if (!(this.elementAdapter instanceof IDataAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && data.readBoolean()) {
         return Optional.empty();
      } else {
         byte[] value = new byte[Adapters.INT_SEGMENTED_7.readData(data).orElseThrow()];

         for (int i = 0; i < value.length; i++) {
            value[i] = adapter.readData(data, null).orElse((byte)0);
         }

         return Optional.of(value);
      }
   }

   public final Optional<Tag> writeNbt(@Nullable byte[] value) {
      if (!(this.elementAdapter instanceof INbtAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (value == null) {
         return Optional.empty();
      } else {
         byte[] bytes = new byte[value.length];

         for (int i = 0; i < value.length; i++) {
            bytes[i] = ((NumericTag)adapter.writeNbt(value[i], null).orElseThrow()).getAsByte();
         }

         return Optional.of(new ByteArrayTag(bytes));
      }
   }

   @Override
   public final Optional<byte[]> readNbt(@Nullable Tag nbt) {
      if (!(this.elementAdapter instanceof INbtAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (nbt instanceof NumericTag numeric) {
         return Optional.of(new byte[]{numeric.getAsByte()});
      } else if (!(nbt instanceof CollectionTag<?> array)) {
         return Optional.empty();
      } else {
         byte[] value = new byte[array.size()];

         for (int i = 0; i < array.size(); i++) {
            value[i] = adapter.readNbt((Tag)array.get(i), null).orElse((byte)0);
         }

         return Optional.of(value);
      }
   }

   public final Optional<JsonArray> writeJson(@Nullable byte[] value) {
      if (!(this.elementAdapter instanceof IJsonAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (value == null) {
         return Optional.empty();
      } else {
         JsonPrimitive[] primitives = new JsonPrimitive[value.length];

         for (int i = 0; i < value.length; i++) {
            primitives[i] = adapter.writeJson(value[i], null).orElseGet(() -> new JsonPrimitive((byte)0));
         }

         JsonArray bytes = new JsonArray(primitives.length);

         for (JsonPrimitive primitive : primitives) {
            bytes.add(primitive.getAsByte());
         }

         return Optional.of(bytes);
      }
   }

   public final Optional<byte[]> readJson(@Nullable JsonArray json) {
      if (!(this.elementAdapter instanceof IJsonAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (json == null) {
         return Optional.empty();
      } else {
         byte[] value = new byte[json.size()];

         for (int i = 0; i < json.size(); i++) {
            value[i] = adapter.readJson(json.get(i), null).orElse((byte)0);
         }

         return Optional.of(value);
      }
   }
}
