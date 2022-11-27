package iskallia.vault.core.net;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public abstract class BitBuffer {
   protected abstract void writeBits(long var1, int var3);

   protected abstract long readBits(int var1);

   public void writeBoolean(boolean value) {
      this.writeBits(value ? 1L : 0L, 1);
   }

   public boolean readBoolean() {
      return this.readBits(1) != 0L;
   }

   public void writeByte(byte value) {
      this.writeBits(value, 8);
   }

   public byte readByte() {
      return (byte)this.readBits(8);
   }

   public void writeByteBits(byte value, int bits) {
      if (bits >= 0 && bits <= 8) {
         this.writeBits(value, bits);
      } else {
         throw new IllegalStateException("Can only write 0 to 8 bits for a byte");
      }
   }

   public byte readByteBits(int bits) {
      if (bits >= 0 && bits <= 8) {
         return (byte)this.readBits(bits);
      } else {
         throw new IllegalStateException("Can only read 0 to 8 bits for a byte");
      }
   }

   public void writeBytes(byte[] value) {
      this.writeBytes(value, 0, value.length);
   }

   public byte[] readBytes(byte[] value) {
      return this.readBytes(value, 0, value.length);
   }

   public void writeBytes(byte[] value, int pos, int offset) {
      for (int i = 0; i < offset; i++) {
         this.writeByte(value[pos + i]);
      }
   }

   public byte[] readBytes(byte[] value, int pos, int offset) {
      for (int i = 0; i < offset; i++) {
         value[pos + i] = this.readByte();
      }

      return value;
   }

   public void writeShort(short value) {
      this.writeBits(value, 16);
   }

   public short readShort() {
      return (short)this.readBits(16);
   }

   public void writeShortBits(short value, int bits) {
      if (bits >= 0 && bits <= 16) {
         this.writeBits(value, bits);
      } else {
         throw new IllegalStateException("Can only write 0 to 16 bits for a short");
      }
   }

   public short readShortBits(int bits) {
      if (bits >= 0 && bits <= 16) {
         return (short)this.readBits(bits);
      } else {
         throw new IllegalStateException("Can only read 0 to 16 bits for a short");
      }
   }

   public void writeChar(char value) {
      this.writeBits(value, 16);
   }

   public char readChar() {
      return (char)this.readBits(16);
   }

   public void writeCharBits(char value, int bits) {
      if (bits >= 0 && bits <= 16) {
         this.writeBits(value, bits);
      } else {
         throw new IllegalStateException("Can only write 0 to 16 bits for a char");
      }
   }

   public char readCharBits(int bits) {
      if (bits >= 0 && bits <= 16) {
         return (char)this.readBits(bits);
      } else {
         throw new IllegalStateException("Can only read 0 to 16 bits for a char");
      }
   }

   public void writeInt(int value) {
      this.writeBits(value, 32);
   }

   public int readInt() {
      return (int)this.readBits(32);
   }

   public void writeIntBits(int value, int bits) {
      if (bits >= 0 && bits <= 32) {
         this.writeBits(value, bits);
      } else {
         throw new IllegalStateException("Can only write 0 to 32 bits for an int");
      }
   }

   public int readIntBits(int bits) {
      if (bits >= 0 && bits <= 32) {
         return (int)this.readBits(bits);
      } else {
         throw new IllegalStateException("Can only read 0 to 32 bits for an int");
      }
   }

   public void writeIntBounded(int value, int bound) {
      this.writeBits(value, 32 - Integer.numberOfLeadingZeros(bound - 1));
   }

   public int readIntBounded(int bound) {
      return (int)this.readBits(32 - Integer.numberOfLeadingZeros(bound - 1));
   }

   public void writeIntBounded(int value, int min, int max) {
      this.writeIntBounded(value - min, max - min + 1);
   }

   public int readIntBounded(int min, int max) {
      return this.readIntBounded(max - min + 1) + min;
   }

   public void writeIntSegmented(int value, int segment) {
      int mask = (1 << segment) - 1;

      while (true) {
         long bits = value & mask;
         value >>>= segment;
         if (value == 0) {
            this.writeBits(1L << segment | bits, segment + 1);
            return;
         }

         this.writeBits(bits, segment + 1);
      }
   }

   public int readIntSegmented(int segment) {
      int mask = 1 << segment;
      int value = 0;
      int shift = 0;

      while (true) {
         long bits = this.readBits(segment + 1);
         if ((bits & mask) != 0L) {
            return (int)(value | bits - mask << shift);
         }

         value = (int)(value | bits << shift);
         shift += segment;
      }
   }

   public void writeFloat(float value) {
      this.writeInt(Float.floatToIntBits(value));
   }

   public float readFloat() {
      return Float.intBitsToFloat(this.readInt());
   }

   public void writeLong(long value) {
      this.writeBits(value, 64);
   }

   public long readLong() {
      return this.readBits(64);
   }

   public void writeLongBits(long value, int bits) {
      if (bits >= 0 && bits <= 64) {
         this.writeBits(value, bits);
      } else {
         throw new IllegalStateException("Can only write 0 to 64 bits for a long");
      }
   }

   public long readLongBits(int bits) {
      if (bits >= 0 && bits <= 64) {
         return this.readBits(bits);
      } else {
         throw new IllegalStateException("Can only read 0 to 64 bits for a long");
      }
   }

   public void writeLongBounded(long value, long bound) {
      this.writeBits(value, 64 - Long.numberOfLeadingZeros(bound - 1L));
   }

   public long readLongBounded(long bound) {
      return (int)this.readBits(64 - Long.numberOfLeadingZeros(bound - 1L));
   }

   public void writeLongBounded(long value, long min, long max) {
      this.writeLongBounded(value - min, max - min + 1L);
   }

   public long readLongBounded(long min, long max) {
      return this.readLongBounded(max - min + 1L) + min;
   }

   public BitBuffer writeLongSegmented(long value, int segment) {
      long mask = (1L << segment) - 1L;

      while (true) {
         long bits = value & mask;
         value >>>= segment;
         if (value == 0L) {
            this.writeBits(1L << segment | bits, segment + 1);
            return this;
         }

         this.writeBits(bits, segment + 1);
      }
   }

   public long readLongSegmented(int segment) {
      long mask = 1L << segment;
      long value = 0L;
      int shift = 0;

      while (true) {
         long bits = this.readBits(segment + 1);
         if ((bits & mask) != 0L) {
            return value | bits - mask << shift;
         }

         value |= bits << shift;
         shift += segment;
      }
   }

   public void writeDouble(double value) {
      this.writeLong(Double.doubleToLongBits(value));
   }

   public double readDouble() {
      return Double.longBitsToDouble(this.readLong());
   }

   public <E extends Enum<E>> void writeEnum(E value) {
      this.writeIntSegmented(value.ordinal(), 3);
   }

   public <E extends Enum<E>> E readEnum(Class<E> type) {
      return type.getEnumConstants()[this.readIntSegmented(3)];
   }

   public <T> void writeOrdinal(T value, ToIntFunction<T> mapper, T... array) {
      this.writeIntBounded(mapper.applyAsInt(value), 0, array.length - 1);
   }

   public <T> T readOrdinal(T... array) {
      return array[this.readIntBounded(0, array.length - 1)];
   }

   public void writeString(String value) {
      this.writeString(value, StandardCharsets.UTF_8);
   }

   public String readString() {
      return this.readString(StandardCharsets.UTF_8);
   }

   public void writeString(String value, Charset charset) {
      byte[] bytes = value.getBytes(charset);
      this.writeIntSegmented(bytes.length, 7);
      this.writeBytes(bytes);
   }

   public String readString(Charset charset) {
      int size = this.readIntSegmented(7);
      byte[] bytes = this.readBytes(new byte[size]);
      return new String(bytes, charset);
   }

   public void writeUUID(UUID uuid) {
      this.writeLong(uuid.getMostSignificantBits());
      this.writeLong(uuid.getLeastSignificantBits());
   }

   public UUID readUUID() {
      return new UUID(this.readLong(), this.readLong());
   }

   public void writeIdentifier(ResourceLocation value) {
      String string = value.getNamespace().equals("minecraft") ? value.getPath() : value.toString();
      byte[] bytes = string.getBytes(StandardCharsets.US_ASCII);
      this.writeIntSegmented(bytes.length, 7);

      for (byte b : bytes) {
         int h = b >>> 5 ^ 2;
         int v = h < 2 ? h * (b & 31) : 25 + (b >>> 3 & 2) + (b & 15);
         this.writeIntBounded(v, 0, 40);
      }
   }

   public ResourceLocation readIdentifier() {
      byte[] bytes = new byte[this.readIntSegmented(7)];

      for (int i = 0; i < bytes.length; i++) {
         int id = this.readIntBounded(0, 40);
         if (id == 0) {
            bytes[i] = 95;
         } else if (id < 27) {
            bytes[i] = (byte)(96 | id);
         } else if (id < 38) {
            bytes[i] = (byte)(48 | id - 27);
         } else {
            bytes[i] = (byte)(32 | id - 25);
         }
      }

      return new ResourceLocation(new String(bytes, StandardCharsets.US_ASCII));
   }

   public <T> void writeCollection(Collection<T> collection, BiConsumer<T, BitBuffer> elementWriter) {
      this.writeIntSegmented(collection.size(), 7);

      for (T element : collection) {
         elementWriter.accept(element, this);
      }
   }

   public <T, C extends Collection<T>> C readCollection(IntFunction<C> supplier, Function<BitBuffer, T> elementReader) {
      int size = this.readIntSegmented(7);
      C collection = (C)supplier.apply(size);

      for (int i = 0; i < size; i++) {
         collection.add(elementReader.apply(this));
      }

      return collection;
   }

   public <T> void writeNullable(@Nullable T value, BiConsumer<BitBuffer, T> writer) {
      this.writeBoolean(value != null);
      if (value != null) {
         writer.accept(this, value);
      }
   }

   @Nullable
   public <T> T readNullable(Function<BitBuffer, T> reader) {
      return this.readBoolean() ? reader.apply(this) : null;
   }

   public <T> Supplier<T> read(Function<BitBuffer, T> fn) {
      return () -> fn.apply(this);
   }

   public <T> Consumer<T> write(BiConsumer<BitBuffer, T> fn) {
      return value -> fn.accept(this, value);
   }
}
