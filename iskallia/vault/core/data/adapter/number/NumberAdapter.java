package iskallia.vault.core.data.adapter.number;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public abstract class NumberAdapter<N extends Number> implements ISimpleAdapter<N, Tag, JsonElement> {
   private final boolean nullable;

   public NumberAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   protected abstract void writeNumberBits(N var1, BitBuffer var2);

   protected abstract N readNumberBits(BitBuffer var1);

   protected abstract void writeNumberBytes(N var1, ByteBuf var2);

   protected abstract N readNumberBytes(ByteBuf var1);

   protected abstract void writeNumberData(N var1, DataOutput var2) throws IOException;

   protected abstract N readNumberData(DataInput var1) throws IOException;

   @Nullable
   protected abstract Tag writeNumberNbt(N var1);

   @Nullable
   protected abstract N readNumberNbt(Tag var1);

   @Nullable
   protected abstract JsonElement writeNumberJson(N var1);

   @Nullable
   protected abstract N readNumberJson(JsonElement var1);

   public final void writeBits(@Nullable N value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeNumberBits(value, buffer);
      }
   }

   @Override
   public final Optional<N> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(this.readNumberBits(buffer));
   }

   public final void writeBytes(@Nullable N value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeNumberBytes(value, buffer);
      }
   }

   @Override
   public final Optional<N> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(this.readNumberBytes(buffer));
   }

   public void writeData(@Nullable N value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeNumberData(value, data);
      }
   }

   @Override
   public Optional<N> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.of(this.readNumberData(data));
   }

   public final Optional<Tag> writeNbt(@Nullable N value) {
      return value == null ? Optional.empty() : Optional.ofNullable(this.writeNumberNbt(value));
   }

   @Override
   public final Optional<N> readNbt(@Nullable Tag nbt) {
      return nbt == null ? Optional.empty() : Optional.ofNullable(this.readNumberNbt(nbt));
   }

   public final Optional<JsonElement> writeJson(@Nullable N value) {
      return value == null ? Optional.empty() : Optional.ofNullable(this.writeNumberJson(value));
   }

   @Override
   public final Optional<N> readJson(@Nullable JsonElement json) {
      return json != null && !(json instanceof JsonNull) ? Optional.ofNullable(this.readNumberJson(json)) : Optional.empty();
   }

   public static Optional<Number> parse(String string) {
      try {
         if (!string.contains(".") && !string.contains("e") && !string.contains("E")) {
            String exception = string.length() < 2 ? "" : string.substring(0, 2);

            return Optional.ofNullable(reduce(switch (exception) {
               case "0x" -> new BigInteger(string.substring(2), 16);
               case "0o" -> new BigInteger(string.substring(2), 8);
               case "0b" -> new BigInteger(string.substring(2), 2);
               default -> new BigInteger(string, 10);
            }));
         } else {
            return Optional.ofNullable(reduce(new BigDecimal(string)));
         }
      } catch (NumberFormatException var3) {
         return Optional.empty();
      }
   }

   public static Number reduce(Number number) {
      if (number instanceof Byte) {
         return number;
      } else if (number instanceof Short) {
         return (Number)(number.shortValue() == number.byteValue() ? number.byteValue() : number);
      } else if (number instanceof Integer) {
         if (number.intValue() == number.byteValue()) {
            return number.byteValue();
         } else {
            return (Number)(number.intValue() == number.shortValue() ? number.shortValue() : number);
         }
      } else if (number instanceof Float) {
         if (number.floatValue() == number.byteValue()) {
            return number.byteValue();
         } else {
            return (Number)(number.floatValue() == number.shortValue() ? number.shortValue() : number);
         }
      } else if (number instanceof Long) {
         if (number.longValue() == number.byteValue()) {
            return number.byteValue();
         } else if (number.longValue() == number.shortValue()) {
            return number.shortValue();
         } else {
            return (Number)(number.longValue() == number.intValue() ? number.intValue() : number);
         }
      } else if (number instanceof Double) {
         if (number.doubleValue() == number.byteValue()) {
            return number.byteValue();
         } else if (number.doubleValue() == number.shortValue()) {
            return number.shortValue();
         } else if (number.doubleValue() == number.intValue()) {
            return number.intValue();
         } else {
            return (Number)(number.doubleValue() == number.floatValue() ? number.floatValue() : number);
         }
      } else if (number instanceof BigInteger integer) {
         return integer.bitLength() <= 63 ? reduce(integer.longValueExact()) : number;
      } else if (number instanceof BigDecimal decimal) {
         if (decimal.stripTrailingZeros().scale() <= 0) {
            return reduce(decimal.toBigIntegerExact());
         } else {
            return BigDecimal.valueOf(decimal.doubleValue()).compareTo(decimal) == 0 ? reduce(decimal.doubleValue()) : number;
         }
      } else {
         return number;
      }
   }

   public static Tag wrap(Number number) {
      if (number instanceof Byte value) {
         return ByteTag.valueOf(value);
      } else if (number instanceof Short value) {
         return ShortTag.valueOf(value);
      } else if (number instanceof Integer value) {
         return IntTag.valueOf(value);
      } else if (number instanceof Float value) {
         return FloatTag.valueOf(value);
      } else if (number instanceof Long value) {
         return LongTag.valueOf(value);
      } else if (number instanceof Double value) {
         return DoubleTag.valueOf(value);
      } else if (number instanceof BigInteger value) {
         return new ByteArrayTag(value.toByteArray());
      } else {
         return number instanceof BigDecimal value ? StringTag.valueOf(value.toString()) : null;
      }
   }
}
