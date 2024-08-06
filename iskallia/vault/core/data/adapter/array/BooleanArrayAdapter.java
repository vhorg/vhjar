package iskallia.vault.core.data.adapter.array;

import com.google.gson.JsonArray;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;

public class BooleanArrayAdapter implements ISimpleAdapter<boolean[], Tag, JsonArray> {
   private final boolean nullable;

   public BooleanArrayAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public BooleanArrayAdapter asNullable() {
      return new BooleanArrayAdapter(true);
   }

   public final void writeBits(@Nullable boolean[] value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.length), buffer);

         for (boolean element : value) {
            Adapters.BOOLEAN.writeBits(element, buffer);
         }
      }
   }

   @Override
   public final Optional<boolean[]> readBits(BitBuffer buffer) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         boolean[] value = new boolean[Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow()];

         for (int i = 0; i < value.length; i++) {
            value[i] = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
         }

         return Optional.of(value);
      }
   }

   public final void writeBytes(@Nullable boolean[] value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.INT_SEGMENTED_7.writeBytes(Integer.valueOf(value.length), buffer);

         for (boolean element : value) {
            Adapters.BOOLEAN.writeBytes(element, buffer);
         }
      }
   }

   @Override
   public final Optional<boolean[]> readBytes(ByteBuf buffer) {
      if (this.nullable && buffer.readBoolean()) {
         return Optional.empty();
      } else {
         boolean[] value = new boolean[Adapters.INT_SEGMENTED_7.readBytes(buffer).orElseThrow()];

         for (int i = 0; i < value.length; i++) {
            value[i] = Adapters.BOOLEAN.readBytes(buffer).orElseThrow();
         }

         return Optional.of(value);
      }
   }

   public void writeData(@Nullable boolean[] value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         Adapters.INT_SEGMENTED_7.writeData(Integer.valueOf(value.length), data);

         for (boolean element : value) {
            Adapters.BOOLEAN.writeData(element, data);
         }
      }
   }

   @Override
   public Optional<boolean[]> readData(DataInput data) throws IOException {
      if (this.nullable && data.readBoolean()) {
         return Optional.empty();
      } else {
         boolean[] value = new boolean[Adapters.INT_SEGMENTED_7.readData(data).orElseThrow()];

         for (int i = 0; i < value.length; i++) {
            value[i] = Adapters.BOOLEAN.readData(data).orElseThrow();
         }

         return Optional.of(value);
      }
   }

   public final Optional<Tag> writeNbt(@Nullable boolean[] value) {
      if (value == null) {
         return Optional.empty();
      } else {
         int size = value.length * 2;
         if (size <= 64) {
            long compressed = 0L;

            for (int i = 0; i < value.length; i++) {
               compressed |= (value[i] ? 11L : 10L) << i * 2;
            }

            if (size <= 8) {
               return Optional.of(ByteTag.valueOf((byte)compressed));
            } else if (size <= 16) {
               return Optional.of(ShortTag.valueOf((short)compressed));
            } else {
               return size <= 32 ? Optional.of(IntTag.valueOf((int)compressed)) : Optional.of(LongTag.valueOf(compressed));
            }
         } else {
            byte[] result = new byte[value.length];

            for (int i = 0; i < value.length; i++) {
               result[i] = (byte)(value[i] ? 1 : 0);
            }

            return Adapters.BYTE_ARRAY.writeNbt(result);
         }
      }
   }

   @Override
   public final Optional<boolean[]> readNbt(@Nullable Tag nbt) {
      if (!(nbt instanceof NumericTag numeric)) {
         if (nbt instanceof ByteArrayTag array) {
            boolean[] value = new boolean[array.size()];

            for (int i = 0; i < array.size(); i++) {
               value[i] = array.get(i).getAsByte() == 1;
            }

            return Optional.of(value);
         } else {
            return Optional.empty();
         }
      } else {
         long compressed = numeric.getAsLong();

         List<Boolean> list;
         for (list = new ArrayList<>(); compressed != 0L; compressed >>>= 2) {
            list.add((compressed & 3L) == 3L);
         }

         boolean[] result = new boolean[list.size()];

         for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
         }

         return Optional.of(result);
      }
   }

   public final Optional<JsonArray> writeJson(@Nullable boolean[] value) {
      if (value == null) {
         return Optional.empty();
      } else {
         JsonArray array = new JsonArray(value.length);

         for (boolean b : value) {
            array.add(b);
         }

         return Optional.of(array);
      }
   }

   public final Optional<boolean[]> readJson(@Nullable JsonArray json) {
      if (json == null) {
         return Optional.empty();
      } else {
         boolean[] value = new boolean[json.size()];

         for (int i = 0; i < json.size(); i++) {
            value[i] = json.get(i).getAsBoolean();
         }

         return Optional.of(value);
      }
   }
}
