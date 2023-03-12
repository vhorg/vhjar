package iskallia.vault.core.data.adapter.array;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import iskallia.vault.item.crystal.data.adapter.IByteAdapter;
import iskallia.vault.item.crystal.data.adapter.IComplexAdapter;
import iskallia.vault.item.crystal.data.adapter.IDataAdapter;
import iskallia.vault.item.crystal.data.adapter.IJsonAdapter;
import iskallia.vault.item.crystal.data.adapter.INbtAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class ArrayAdapter<T> implements IComplexAdapter<T[], Tag, JsonElement, Object> {
   private final IntFunction<T[]> constructor;
   private final Object elementAdapter;
   private final Supplier<T> defaultValue;
   private final boolean nullable;

   public ArrayAdapter(IntFunction<T[]> constructor, Object elementAdapter, Supplier<T> defaultValue, boolean nullable) {
      this.constructor = constructor;
      this.elementAdapter = elementAdapter;
      this.defaultValue = defaultValue;
      this.nullable = nullable;
   }

   public IntFunction<T[]> getConstructor() {
      return this.constructor;
   }

   public Object getElementAdapter() {
      return this.elementAdapter;
   }

   public Supplier<T> getDefaultValue() {
      return this.defaultValue;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public ArrayAdapter<T> asNullable() {
      return new ArrayAdapter<>(this.constructor, this.elementAdapter, this.defaultValue, true);
   }

   public final void writeBits(@Nullable T[] value, BitBuffer buffer, Object context) {
      if (this.elementAdapter instanceof IBitAdapter adapter) {
         if (this.nullable) {
            buffer.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(value.length), buffer);

            for (T element : value) {
               adapter.writeBits(element, buffer, context);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public final Optional<T[]> readBits(BitBuffer buffer, Object context) {
      if (!(this.elementAdapter instanceof IBitAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         T[] value = (T[])((Object[])this.constructor.apply(Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow()));

         for (int i = 0; i < value.length; i++) {
            value[i] = adapter.readBits(buffer, context).orElseGet(this.defaultValue);
         }

         return Optional.of((T)value);
      }
   }

   public final void writeBytes(@Nullable T[] value, ByteBuf buffer, Object context) {
      if (this.elementAdapter instanceof IByteAdapter adapter) {
         if (this.nullable) {
            buffer.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_3.writeBytes(Integer.valueOf(value.length), buffer);

            for (T element : value) {
               adapter.writeBytes(element, buffer, context);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public final Optional<T[]> readBytes(ByteBuf buffer, Object context) {
      if (!(this.elementAdapter instanceof IByteAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         T[] value = (T[])((Object[])this.constructor.apply(Adapters.INT_SEGMENTED_3.readBytes(buffer).orElseThrow()));

         for (int i = 0; i < value.length; i++) {
            value[i] = adapter.readBytes(buffer, context).orElseGet(this.defaultValue);
         }

         return Optional.of((T)value);
      }
   }

   public void writeData(@Nullable T[] value, DataOutput data, Object context) throws IOException {
      if (this.elementAdapter instanceof IDataAdapter adapter) {
         if (this.nullable) {
            data.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_3.writeData(Integer.valueOf(value.length), data);

            for (T element : value) {
               adapter.writeData(element, data, context);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public Optional<T[]> readData(DataInput data, Object context) throws IOException {
      if (!(this.elementAdapter instanceof IDataAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && data.readBoolean()) {
         return Optional.empty();
      } else {
         T[] value = (T[])((Object[])this.constructor.apply(Adapters.INT_SEGMENTED_3.readData(data).orElseThrow()));

         for (int i = 0; i < value.length; i++) {
            value[i] = adapter.readData(data, context).orElseGet(this.defaultValue);
         }

         return Optional.of((T)value);
      }
   }

   public final Optional<Tag> writeNbt(@Nullable T[] value, Object context) {
      if (!(this.elementAdapter instanceof INbtAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (value == null) {
         return Optional.empty();
      } else {
         ListTag list = new ListTag();

         for (T element : value) {
            list.add((Tag)adapter.writeNbt(element, context).orElseGet(() -> (T)adapter.writeNbt(this.defaultValue.get(), context).orElseThrow()));
         }

         return Optional.of(list);
      }
   }

   @Override
   public final Optional<T[]> readNbt(@Nullable Tag nbt, Object context) {
      if (!(this.elementAdapter instanceof INbtAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (!(nbt instanceof ListTag list)) {
         return Optional.empty();
      } else {
         Object[] value = (Object[])this.constructor.apply(list.size());

         for (int i = 0; i < list.size(); i++) {
            value[i] = adapter.readNbt(list.get(i), context).orElseGet(this.defaultValue);
         }

         return (Optional<T[]>)Optional.of(value);
      }
   }

   public final Optional<JsonElement> writeJson(@Nullable T[] value, Object context) {
      if (!(this.elementAdapter instanceof IJsonAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (value == null) {
         return Optional.empty();
      } else {
         JsonArray array = new JsonArray();

         for (T element : value) {
            array.add(adapter.writeJson(element, context).orElseGet(() -> (JsonElement)adapter.writeJson(this.defaultValue.get(), context).orElse(null)));
         }

         return Optional.of(array);
      }
   }

   @Override
   public final Optional<T[]> readJson(@Nullable JsonElement json, Object context) {
      if (!(this.elementAdapter instanceof IJsonAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (json == null) {
         return Optional.empty();
      } else if (!(json instanceof JsonArray array)) {
         return Optional.empty();
      } else {
         T[] value = (T[])((Object[])this.constructor.apply(array.size()));

         for (int i = 0; i < array.size(); i++) {
            value[i] = adapter.readJson(array.get(i), context).orElseGet(this.defaultValue);
         }

         return Optional.of((T)value);
      }
   }
}
