package iskallia.vault.core.data.adapter.list;

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
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class ListAdapter<T> implements IComplexAdapter<List<T>, Tag, JsonElement, Object> {
   private final IntFunction<List<T>> constructor;
   private final Object elementAdapter;
   private final Supplier<T> defaultValue;
   private final boolean nullable;

   public ListAdapter(IntFunction<List<T>> constructor, Object elementAdapter, Supplier<T> defaultValue, boolean nullable) {
      this.constructor = constructor;
      this.elementAdapter = elementAdapter;
      this.defaultValue = defaultValue;
      this.nullable = nullable;
   }

   public IntFunction<List<T>> getConstructor() {
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

   public ListAdapter<T> asNullable() {
      return new ListAdapter<>(this.constructor, this.elementAdapter, this.defaultValue, true);
   }

   public void writeBits(@Nullable List<T> value, BitBuffer buffer, @Nullable Object context) {
      if (this.elementAdapter instanceof IBitAdapter adapter) {
         if (this.nullable) {
            buffer.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_3.writeBits(Integer.valueOf(value.size()), buffer);

            for (T element : value) {
               adapter.writeBits(element, buffer, context);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public Optional<List<T>> readBits(BitBuffer buffer, @Nullable Object context) {
      if (!(this.elementAdapter instanceof IBitAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         int listSize = Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow();
         List<T> value = this.constructor.apply(listSize);

         for (int i = 0; i < listSize; i++) {
            adapter.readBits(buffer, context).or(this.defaultValue).ifPresent(element -> value.add((T)element));
         }

         return Optional.of(value);
      }
   }

   public void writeBytes(@Nullable List<T> value, ByteBuf buffer, @Nullable Object context) {
      if (this.elementAdapter instanceof IByteAdapter adapter) {
         if (this.nullable) {
            buffer.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_3.writeBytes(Integer.valueOf(value.size()), buffer);

            for (T element : value) {
               adapter.writeBytes(element, buffer, context);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public Optional<List<T>> readBytes(ByteBuf buffer, @Nullable Object context) {
      if (!(this.elementAdapter instanceof IByteAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         int listSize = Adapters.INT_SEGMENTED_3.readBytes(buffer).orElseThrow();
         List<T> value = this.constructor.apply(listSize);

         for (int i = 0; i < listSize; i++) {
            adapter.readBytes(buffer, context).or(this.defaultValue).ifPresent(element -> value.add((T)element));
         }

         return Optional.of(value);
      }
   }

   public void writeData(@Nullable List<T> value, DataOutput data, @Nullable Object context) throws IOException {
      if (this.elementAdapter instanceof IDataAdapter adapter) {
         if (this.nullable) {
            data.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_3.writeData(Integer.valueOf(value.size()), data);

            for (T element : value) {
               adapter.writeData(element, data, context);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public Optional<List<T>> readData(DataInput data, @Nullable Object context) throws IOException {
      if (!(this.elementAdapter instanceof IDataAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && data.readBoolean()) {
         return Optional.empty();
      } else {
         int listSize = Adapters.INT_SEGMENTED_3.readData(data).orElseThrow();
         List<T> value = this.constructor.apply(listSize);

         for (int i = 0; i < listSize; i++) {
            adapter.readData(data, context).or(this.defaultValue).ifPresent(element -> value.add((T)element));
         }

         return Optional.of(value);
      }
   }

   public Optional<Tag> writeNbt(@Nullable List<T> value, @Nullable Object context) {
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
   public Optional<List<T>> readNbt(@Nullable Tag nbt, @Nullable Object context) {
      if (!(this.elementAdapter instanceof INbtAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (!(nbt instanceof ListTag listTag)) {
         return Optional.empty();
      } else {
         List value = this.constructor.apply(listTag.size());

         for (int i = 0; i < listTag.size(); i++) {
            adapter.readNbt(listTag.get(i), context).or(this.defaultValue).ifPresent(element -> value.add(element));
         }

         return Optional.of(value);
      }
   }

   public Optional<JsonElement> writeJson(@Nullable List<T> value, @Nullable Object context) {
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
   public Optional<List<T>> readJson(@Nullable JsonElement json, @Nullable Object context) {
      if (!(this.elementAdapter instanceof IJsonAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (!(json instanceof JsonArray array)) {
         return Optional.empty();
      } else {
         List value = this.constructor.apply(array.size());

         for (int i = 0; i < array.size(); i++) {
            adapter.readJson(array.get(i), context).or(this.defaultValue).ifPresent(element -> value.add(element));
         }

         return Optional.of(value);
      }
   }
}
