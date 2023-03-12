package iskallia.vault.core.data.adapter.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.primitive.BoundedByteAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.Optional;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class NbtAdapter<T extends Tag> implements ISimpleAdapter<T, Tag, JsonElement> {
   protected static final NbtAdapter[] ADAPTERS = new NbtAdapter[]{Adapters.INT_TAG};
   protected static final BoundedByteAdapter TAG_ID = new BoundedByteAdapter((byte)0, (byte)(ADAPTERS.length - 1), false);
   private final Class<T> type;
   private final boolean nullable;

   public NbtAdapter(Class<T> type, boolean nullable) {
      this.type = type;
      this.nullable = nullable;
   }

   public Class<T> getType() {
      return this.type;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   protected abstract void writeTagBits(T var1, BitBuffer var2);

   protected abstract T readTagBits(BitBuffer var1);

   protected abstract void writeTagBytes(T var1, ByteBuf var2);

   protected abstract T readTagBytes(ByteBuf var1);

   protected abstract void writeTagData(T var1, DataOutput var2) throws IOException;

   protected abstract T readTagData(DataInput var1) throws IOException;

   protected abstract JsonElement writeTagJson(T var1);

   @Nullable
   protected abstract T readTagJson(JsonElement var1);

   public void writeBits(@Nullable T value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeTagBits(value, buffer);
      }
   }

   @Override
   public Optional<T> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(this.readTagBits(buffer));
   }

   public void writeBytes(@Nullable T value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeTagBytes(value, buffer);
      }
   }

   @Override
   public Optional<T> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(this.readTagBytes(buffer));
   }

   public void writeData(@Nullable T value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         this.writeTagData(value, data);
      }
   }

   @Override
   public Optional<T> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.of(this.readTagData(data));
   }

   public Optional<Tag> writeNbt(@Nullable T value) {
      return value == null ? Optional.empty() : Optional.of(value);
   }

   @Override
   public Optional<T> readNbt(@Nullable Tag nbt) {
      return nbt != null && this.type.isAssignableFrom(nbt.getClass()) ? Optional.of((T)nbt) : Optional.empty();
   }

   public Optional<JsonElement> writeJson(@Nullable T value) {
      return value == null ? Optional.empty() : Optional.ofNullable(this.writeTagJson(value));
   }

   @Override
   public Optional<T> readJson(@Nullable JsonElement json) {
      return json != null && !(json instanceof JsonNull) ? Optional.ofNullable(this.readTagJson(json)) : Optional.empty();
   }

   public static class BitInterface implements DataInput, DataOutput {
      private final BitBuffer parent;

      public BitInterface(BitBuffer parent) {
         this.parent = parent;
      }

      @Override
      public void readFully(@NotNull byte[] bytes) throws IOException {
         this.parent.readBytes(bytes);
      }

      @Override
      public void readFully(@NotNull byte[] bytes, int off, int len) throws IOException {
         this.parent.readBytes(bytes, off, len);
      }

      @Override
      public int skipBytes(int n) throws IOException {
         for (int i = 0; i < n; i++) {
            this.parent.readByte();
         }

         return n;
      }

      @Override
      public boolean readBoolean() throws IOException {
         return this.parent.readBoolean();
      }

      @Override
      public byte readByte() throws IOException {
         return this.parent.readByte();
      }

      @Override
      public int readUnsignedByte() throws IOException {
         return this.parent.readIntBits(8);
      }

      @Override
      public short readShort() throws IOException {
         return this.parent.readShort();
      }

      @Override
      public int readUnsignedShort() throws IOException {
         return this.parent.readIntBits(16);
      }

      @Override
      public char readChar() throws IOException {
         return this.parent.readChar();
      }

      @Override
      public int readInt() throws IOException {
         return this.parent.readInt();
      }

      @Override
      public long readLong() throws IOException {
         return this.parent.readLong();
      }

      @Override
      public float readFloat() throws IOException {
         return this.parent.readFloat();
      }

      @Override
      public double readDouble() throws IOException {
         return this.parent.readDouble();
      }

      @Override
      public String readLine() throws IOException {
         throw new UnsupportedOperationException();
      }

      @NotNull
      @Override
      public String readUTF() throws IOException {
         int size = this.parent.readIntBits(8) << 8 | this.parent.readIntBits(8);
         byte[] bytes = new byte[size];
         char[] chars = new char[size];
         int bytesSize = 0;
         int charsSize = 0;
         this.readFully(bytes, 0, size);

         while (bytesSize < size) {
            int c = bytes[bytesSize] & 255;
            if (c > 127) {
               break;
            }

            bytesSize++;
            chars[charsSize++] = (char)c;
         }

         while (bytesSize < size) {
            int c = bytes[bytesSize] & 255;
            switch (c >> 4) {
               case 0:
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
                  bytesSize++;
                  chars[charsSize++] = (char)c;
                  break;
               case 8:
               case 9:
               case 10:
               case 11:
               default:
                  throw new UTFDataFormatException("malformed input around byte " + bytesSize);
               case 12:
               case 13:
                  bytesSize += 2;
                  if (bytesSize > size) {
                     throw new UTFDataFormatException("malformed input: partial character at end");
                  }

                  int char2 = bytes[bytesSize - 1];
                  if ((char2 & 192) != 128) {
                     throw new UTFDataFormatException("malformed input around byte " + bytesSize);
                  }

                  chars[charsSize++] = (char)((c & 31) << 6 | char2 & 63);
                  break;
               case 14:
                  bytesSize += 3;
                  if (bytesSize > size) {
                     throw new UTFDataFormatException("malformed input: partial character at end");
                  }

                  int char2 = bytes[bytesSize - 2];
                  int char3 = bytes[bytesSize - 1];
                  if ((char2 & 192) != 128 || (char3 & 192) != 128) {
                     throw new UTFDataFormatException("malformed input around byte " + (bytesSize - 1));
                  }

                  chars[charsSize++] = (char)((c & 15) << 12 | (char2 & 63) << 6 | (char3 & 63) << 0);
            }
         }

         return new String(chars, 0, charsSize);
      }

      @Override
      public void write(int b) throws IOException {
         this.parent.writeIntBits(b, 8);
      }

      @Override
      public void write(@NotNull byte[] bytes) throws IOException {
         this.parent.writeBytes(bytes);
      }

      @Override
      public void write(@NotNull byte[] bytes, int off, int len) throws IOException {
         this.parent.writeBytes(bytes, off, len);
      }

      @Override
      public void writeBoolean(boolean v) throws IOException {
         this.parent.writeBoolean(v);
      }

      @Override
      public void writeByte(int v) throws IOException {
         this.parent.writeByte((byte)v);
      }

      @Override
      public void writeShort(int v) throws IOException {
         this.parent.writeShort((short)v);
      }

      @Override
      public void writeChar(int v) throws IOException {
         this.parent.writeChar((char)v);
      }

      @Override
      public void writeInt(int v) throws IOException {
         this.parent.writeInt(v);
      }

      @Override
      public void writeLong(long v) throws IOException {
         this.parent.writeLong(v);
      }

      @Override
      public void writeFloat(float v) throws IOException {
         this.parent.writeFloat(v);
      }

      @Override
      public void writeDouble(double v) throws IOException {
         this.parent.writeDouble(v);
      }

      @Override
      public void writeBytes(@NotNull String s) throws IOException {
         for (int i = 0; i < s.length(); i++) {
            this.parent.writeByte((byte)s.charAt(i));
         }
      }

      @Override
      public void writeChars(@NotNull String s) throws IOException {
         for (int i = 0; i < s.length(); i++) {
            int v = s.charAt(i);
            this.parent.writeByte((byte)(v >>> 8));
            this.parent.writeByte((byte)v);
         }
      }

      @Override
      public void writeUTF(@NotNull String s) throws IOException {
         int size = s.length();
         int utfSize = size;

         for (int i = 0; i < size; i++) {
            int c = s.charAt(i);
            if (c >= 128 || c == 0) {
               utfSize += c >= 2048 ? 2 : 1;
            }
         }

         if (utfSize <= 65535 && utfSize >= size) {
            byte[] bytes = new byte[utfSize + 2];
            int count = 0;
            bytes[count++] = (byte)(utfSize >>> 8 & 0xFF);
            bytes[count++] = (byte)(utfSize & 0xFF);

            int ix;
            for (ix = 0; ix < size; ix++) {
               int c = s.charAt(ix);
               if (c >= 128 || c == 0) {
                  break;
               }

               bytes[count++] = (byte)c;
            }

            for (; ix < size; ix++) {
               int c = s.charAt(ix);
               if (c < 128 && c != 0) {
                  bytes[count++] = (byte)c;
               } else if (c >= 2048) {
                  bytes[count++] = (byte)(224 | c >> 12 & 15);
                  bytes[count++] = (byte)(128 | c >> 6 & 63);
                  bytes[count++] = (byte)(128 | c & 63);
               } else {
                  bytes[count++] = (byte)(192 | c >> 6 & 31);
                  bytes[count++] = (byte)(128 | c & 63);
               }
            }

            this.parent.writeBytes(bytes, 0, utfSize + 2);
         } else {
            throw new UTFDataFormatException(tooLongMsg(s, utfSize));
         }
      }

      private static String tooLongMsg(String s, int bits32) {
         int slen = s.length();
         String head = s.substring(0, 8);
         String tail = s.substring(slen - 8, slen);
         long actualLength = slen + Integer.toUnsignedLong(bits32 - slen);
         return "encoded string (" + head + "..." + tail + ") too long: " + actualLength + " bytes";
      }
   }
}
