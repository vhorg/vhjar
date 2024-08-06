package iskallia.vault.core.data.adapter.array;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

public class DoubleArrayAdapter implements ISimpleAdapter<double[], Tag, JsonElement> {
   private final Object elementAdapter;
   private final boolean nullable;

   public DoubleArrayAdapter(Object elementAdapter, boolean nullable) {
      this.elementAdapter = elementAdapter;
      this.nullable = nullable;
   }

   public Object getElementAdapter() {
      return this.elementAdapter;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public DoubleArrayAdapter asNullable() {
      return new DoubleArrayAdapter(this.elementAdapter, true);
   }

   public final void writeBits(@Nullable double[] value, BitBuffer buffer) {
      if (this.elementAdapter instanceof IBitAdapter adapter) {
         if (this.nullable) {
            buffer.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.length), buffer);

            for (double element : value) {
               adapter.writeBits(element, buffer, null);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public final Optional<double[]> readBits(BitBuffer buffer) {
      if (!(this.elementAdapter instanceof IBitAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         double[] value = new double[Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow()];

         for (int i = 0; i < value.length; i++) {
            value[i] = (Double)adapter.readBits(buffer, null).orElse(0);
         }

         return Optional.of(value);
      }
   }

   public final void writeBytes(@Nullable double[] value, ByteBuf buffer) {
      if (this.elementAdapter instanceof IByteAdapter adapter) {
         if (this.nullable) {
            buffer.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_7.writeBytes(Integer.valueOf(value.length), buffer);

            for (double element : value) {
               adapter.writeBytes(element, buffer, null);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public final Optional<double[]> readBytes(ByteBuf buffer) {
      if (!(this.elementAdapter instanceof IByteAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         double[] value = new double[Adapters.INT_SEGMENTED_7.readBytes(buffer).orElseThrow()];

         for (int i = 0; i < value.length; i++) {
            value[i] = (Double)adapter.readBytes(buffer, null).orElse(0);
         }

         return Optional.of(value);
      }
   }

   public void writeData(@Nullable double[] value, DataOutput data) throws IOException {
      if (this.elementAdapter instanceof IDataAdapter adapter) {
         if (this.nullable) {
            data.writeBoolean(value == null);
         }

         if (value != null) {
            Adapters.INT_SEGMENTED_7.writeData(Integer.valueOf(value.length), data);

            for (double element : value) {
               adapter.writeData(element, data, null);
            }
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public Optional<double[]> readData(DataInput data) throws IOException {
      if (!(this.elementAdapter instanceof IDataAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (this.nullable && data.readBoolean()) {
         return Optional.empty();
      } else {
         double[] value = new double[Adapters.INT_SEGMENTED_7.readData(data).orElseThrow()];

         for (int i = 0; i < value.length; i++) {
            value[i] = (Double)adapter.readData(data, null).orElse(0);
         }

         return Optional.of(value);
      }
   }

   public final Optional<Tag> writeNbt(@Nullable double[] value) {
      if (!(this.elementAdapter instanceof INbtAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (value == null) {
         return Optional.empty();
      } else {
         NumericTag[] tags = new NumericTag[value.length];

         for (int i = 0; i < value.length; i++) {
            tags[i] = (NumericTag)adapter.writeNbt(value[i], null).orElseThrow();
         }

         if (Arrays.stream(tags).allMatch(element -> element.getAsByte() == element.getAsDouble())) {
            byte[] bytes = new byte[tags.length];

            for (int i = 0; i < tags.length; i++) {
               bytes[i] = tags[i].getAsByte();
            }

            return Optional.of(new ByteArrayTag(bytes));
         } else if (Arrays.stream(tags).allMatch(element -> element.getAsInt() == element.getAsDouble())) {
            int[] bytes = new int[tags.length];

            for (int i = 0; i < tags.length; i++) {
               bytes[i] = tags[i].getAsInt();
            }

            return Optional.of(new IntArrayTag(bytes));
         } else {
            ListTag doubles = new ListTag();

            for (NumericTag tag : tags) {
               doubles.add(DoubleTag.valueOf(tag.getAsDouble()));
            }

            return Optional.of(doubles);
         }
      }
   }

   @Override
   public final Optional<double[]> readNbt(@Nullable Tag nbt) {
      if (!(this.elementAdapter instanceof INbtAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (nbt instanceof NumericTag numeric) {
         return Optional.of(new double[]{numeric.getAsDouble()});
      } else if (!(nbt instanceof CollectionTag<?> array)) {
         return Optional.empty();
      } else {
         double[] value = new double[array.size()];

         for (int i = 0; i < array.size(); i++) {
            value[i] = (Double)adapter.readNbt((Tag)array.get(i), null).orElse(0);
         }

         return Optional.of(value);
      }
   }

   public final Optional<JsonElement> writeJson(@Nullable double[] value) {
      if (!(this.elementAdapter instanceof IJsonAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (value == null) {
         return Optional.empty();
      } else {
         JsonPrimitive[] primitives = new JsonPrimitive[value.length];

         for (int i = 0; i < value.length; i++) {
            primitives[i] = adapter.writeJson(value[i], null).orElseGet(() -> new JsonPrimitive(0));
         }

         JsonArray doubles = new JsonArray(primitives.length);

         for (JsonPrimitive primitive : primitives) {
            doubles.add(primitive.getAsDouble());
         }

         return Optional.of(doubles);
      }
   }

   @Override
   public final Optional<double[]> readJson(@Nullable JsonElement json) {
      if (!(this.elementAdapter instanceof IJsonAdapter adapter)) {
         throw new UnsupportedOperationException();
      } else if (json == null) {
         return Optional.empty();
      } else if (!(json instanceof JsonArray array)) {
         return Optional.of(new double[]{json.getAsDouble()});
      } else {
         double[] value = new double[array.size()];

         for (int i = 0; i < array.size(); i++) {
            value[i] = (Double)adapter.readJson(array.get(i), null).orElse(0);
         }

         return Optional.of(value);
      }
   }
}
