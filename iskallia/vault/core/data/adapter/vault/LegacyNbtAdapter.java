package iskallia.vault.core.data.adapter.vault;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class LegacyNbtAdapter<T extends Tag> implements ISimpleAdapter<T, Tag, JsonElement> {
   public static final LegacyNbtAdapter<CompoundTag> COMPOUND = new LegacyNbtAdapter((Class<T>)CompoundTag.class, false);
   protected final Class<T> type;
   protected final boolean nullable;

   public LegacyNbtAdapter(Class<T> type, boolean nullable) {
      this.type = type;
      this.nullable = nullable;
   }

   public LegacyNbtAdapter<T> asNullable() {
      return new LegacyNbtAdapter<>(this.type, true);
   }

   protected void write(T value, DataOutput output) {
      try {
         output.writeByte(value.getId());
         if (value.getId() != 0) {
            output.writeUTF("");
            value.write(output);
         }
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }
   }

   private T read(DataInput input) {
      try {
         byte type = input.readByte();
         if (type == 0) {
            return (T)EndTag.INSTANCE;
         } else {
            NbtAccounter.UNLIMITED.readUTF(input.readUTF());
            return (T)TagTypes.getType(type).load(input, 0, NbtAccounter.UNLIMITED);
         }
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void writeBits(@Nullable T value, BitBuffer buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.write(value, new LegacyNbtAdapter.Interface(buffer));
      }
   }

   @Override
   public Optional<T> readBits(BitBuffer buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(this.read(new LegacyNbtAdapter.Interface(buffer)));
   }

   public void writeBytes(@Nullable T value, ByteBuf buffer) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         this.write(value, new ByteBufOutputStream(buffer));
      }
   }

   @Override
   public Optional<T> readBytes(ByteBuf buffer) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.of(this.read(new ByteBufInputStream(buffer)));
   }

   public void writeData(@Nullable T value, DataOutput data) throws IOException {
      if (this.nullable) {
         data.writeBoolean(value == null);
      }

      if (value != null) {
         this.write(value, data);
      }
   }

   @Override
   public Optional<T> readData(DataInput data) throws IOException {
      return this.nullable && data.readBoolean() ? Optional.empty() : Optional.of(this.read(data));
   }

   public Optional<Tag> writeNbt(@Nullable T value) {
      return value == null ? Optional.empty() : Optional.of(value);
   }

   @Override
   public Optional<T> readNbt(@Nullable Tag nbt) {
      return nbt != null && this.type.isAssignableFrom(nbt.getClass()) ? Optional.of((T)nbt) : Optional.empty();
   }

   private static class Interface implements DataInput, DataOutput {
      private final BitBuffer parent;

      public Interface(BitBuffer parent) {
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
