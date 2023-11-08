package iskallia.vault.core.data.adapter.number;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SegmentedLongAdapter extends LongAdapter {
   private final int bitSegment;
   private final int byteSegment;

   public SegmentedLongAdapter(int segment, boolean nullable) {
      super(nullable);
      this.bitSegment = segment;
      if (segment < 8) {
         this.byteSegment = 1;
      } else if (segment < 16) {
         this.byteSegment = 2;
      } else if (segment < 24) {
         this.byteSegment = 3;
      } else if (segment < 32) {
         this.byteSegment = 4;
      } else if (segment < 48) {
         this.byteSegment = 5;
      } else if (segment < 64) {
         this.byteSegment = 6;
      } else {
         this.byteSegment = 7;
      }
   }

   public int getBitSegment() {
      return this.bitSegment;
   }

   public int getByteSegment() {
      return this.byteSegment;
   }

   public SegmentedLongAdapter asNullable() {
      return new SegmentedLongAdapter(this.bitSegment, true);
   }

   @Override
   protected void writeNumberBits(Long value, BitBuffer buffer) {
      int segment = this.bitSegment;
      long mask = (1L << segment) - 1L;

      for (int shift = 0; shift < 64; shift += segment) {
         long bits = value & mask;
         value = value >>> segment;
         if (64 - shift <= segment) {
            buffer.writeLongBits(bits, 64 - shift);
            break;
         }

         if (value == 0L) {
            buffer.writeLongBits(1L << segment | bits, segment + 1);
            break;
         }

         buffer.writeLongBits(bits, segment + 1);
      }
   }

   @Override
   protected Long readNumberBits(BitBuffer buffer) {
      int segment = this.bitSegment;
      long mask = 1L << segment;
      long value = 0L;

      for (int shift = 0; shift < 64; shift += segment) {
         if (64 - shift <= segment) {
            value |= buffer.readLongBits(64 - shift) << shift;
            break;
         }

         long bits = buffer.readLongBits(segment + 1);
         if ((bits & mask) != 0L) {
            value |= bits - mask << shift;
            break;
         }

         value |= bits << shift;
      }

      return value;
   }

   @Override
   protected void writeNumberBytes(Long value, ByteBuf buffer) {
      int segment = this.byteSegment * 8 - 1;
      long mask = (1L << segment) - 1L;

      for (int shift = 0; shift < 64; shift += segment) {
         long bits = value & mask;
         value = value >>> segment;
         if (64 - shift <= segment) {
            for (int i = 0; i < 64 - shift; i += 8) {
               buffer.writeByte((byte)(bits >>> i));
            }
            break;
         }

         if (value == 0L) {
            bits |= 1L << segment;

            for (int i = 0; i <= segment; i += 8) {
               buffer.writeByte((byte)(bits >>> i));
            }
            break;
         }

         for (int i = 0; i <= segment; i += 8) {
            buffer.writeByte((byte)(bits >>> i));
         }
      }
   }

   @Override
   protected Long readNumberBytes(ByteBuf buffer) {
      int segment = this.byteSegment * 8 - 1;
      long mask = 1L << segment;
      long value = 0L;

      for (int shift = 0; shift < 64; shift += segment) {
         if (64 - shift <= segment) {
            long bits = 0L;

            for (int i = 0; i < 64 - shift; i += 8) {
               bits |= Byte.toUnsignedLong(buffer.readByte()) << i;
            }

            value |= bits << shift;
            break;
         }

         long bits = 0L;

         for (int i = 0; i <= segment; i += 8) {
            bits |= Byte.toUnsignedLong(buffer.readByte()) << i;
         }

         if ((bits & mask) != 0L) {
            value |= bits - mask << shift;
            break;
         }

         value |= bits << shift;
      }

      return value;
   }

   @Override
   protected void writeNumberData(Long value, DataOutput data) throws IOException {
      int segment = this.byteSegment * 8 - 1;
      long mask = (1L << segment) - 1L;

      for (int shift = 0; shift < 64; shift += segment) {
         long bits = value & mask;
         value = value >>> segment;
         if (64 - shift <= segment) {
            for (int i = 0; i < 64 - shift; i += 8) {
               data.writeByte((byte)(bits >>> i));
            }
            break;
         }

         if (value == 0L) {
            bits |= 1L << segment;

            for (int i = 0; i <= segment; i += 8) {
               data.writeByte((byte)(bits >>> i));
            }
            break;
         }

         for (int i = 0; i <= segment; i += 8) {
            data.writeByte((byte)(bits >>> i));
         }
      }
   }

   @Override
   protected Long readNumberData(DataInput data) throws IOException {
      int segment = this.byteSegment * 8 - 1;
      long mask = 1L << segment;
      long value = 0L;

      for (int shift = 0; shift < 32; shift += segment) {
         if (32 - shift <= segment) {
            long bits = 0L;

            for (int i = 0; i < 32 - shift; i += 8) {
               bits |= Byte.toUnsignedLong(data.readByte()) << i;
            }

            value |= bits << shift;
            break;
         }

         long bits = 0L;

         for (int i = 0; i <= segment; i += 8) {
            bits |= Byte.toUnsignedLong(data.readByte()) << i;
         }

         if ((bits & mask) != 0L) {
            value |= bits - mask << shift;
            break;
         }

         value |= bits << shift;
      }

      return value;
   }
}
