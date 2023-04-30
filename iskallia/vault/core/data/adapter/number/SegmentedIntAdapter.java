package iskallia.vault.core.data.adapter.number;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SegmentedIntAdapter extends IntAdapter {
   private final int bitSegment;
   private final int byteSegment;

   public SegmentedIntAdapter(int segment, boolean nullable) {
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
      } else {
         this.byteSegment = 5;
      }
   }

   public int getBitSegment() {
      return this.bitSegment;
   }

   public int getByteSegment() {
      return this.byteSegment;
   }

   public SegmentedIntAdapter asNullable() {
      return new SegmentedIntAdapter(this.bitSegment, true);
   }

   @Override
   protected void writeNumberBits(Integer value, BitBuffer buffer) {
      int segment = this.bitSegment;
      long mask = (1L << segment) - 1L;

      for (int shift = 0; shift < 32; shift += segment) {
         long bits = value.intValue() & mask;
         value = value >>> segment;
         if (32 - shift <= segment) {
            buffer.writeLongBits(bits, 32 - shift);
            break;
         }

         if (value == 0) {
            buffer.writeLongBits(1L << segment | bits, segment + 1);
            break;
         }

         buffer.writeLongBits(bits, segment + 1);
      }
   }

   @Override
   protected Integer readNumberBits(BitBuffer buffer) {
      int segment = this.bitSegment;
      long mask = 1L << segment;
      int value = 0;

      for (int shift = 0; shift < 32; shift += segment) {
         if (32 - shift <= segment) {
            value = (int)(value | buffer.readLongBits(32 - shift) << shift);
            break;
         }

         long bits = buffer.readLongBits(segment + 1);
         if ((bits & mask) != 0L) {
            value = (int)(value | bits - mask << shift);
            break;
         }

         value = (int)(value | bits << shift);
      }

      return value;
   }

   @Override
   protected void writeNumberBytes(Integer value, ByteBuf buffer) {
      int segment = this.byteSegment * 8 - 1;
      long mask = (1L << segment) - 1L;

      for (int shift = 0; shift < 32; shift += segment) {
         long bits = value.intValue() & mask;
         value = value >>> segment;
         if (32 - shift <= segment) {
            for (int i = 0; i < 32 - shift; i += 8) {
               buffer.writeByte((byte)(bits >>> i));
            }
            break;
         }

         if (value == 0) {
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
   protected Integer readNumberBytes(ByteBuf buffer) {
      int segment = this.byteSegment * 8 - 1;
      long mask = 1L << segment;
      int value = 0;

      for (int shift = 0; shift < 32; shift += segment) {
         if (32 - shift <= segment) {
            int bits = 0;

            for (int i = 0; i < 32 - shift; i += 8) {
               bits |= Byte.toUnsignedInt(buffer.readByte()) << i;
            }

            value |= bits << shift;
            break;
         }

         long bits = 0L;

         for (int i = 0; i <= segment; i += 8) {
            bits |= Byte.toUnsignedLong(buffer.readByte()) << i;
         }

         if ((bits & mask) != 0L) {
            value = (int)(value | bits - mask << shift);
            break;
         }

         value = (int)(value | bits << shift);
      }

      return value;
   }

   @Override
   protected void writeNumberData(Integer value, DataOutput data) throws IOException {
      int segment = this.byteSegment * 8 - 1;
      long mask = (1L << segment) - 1L;

      for (int shift = 0; shift < 32; shift += segment) {
         long bits = value.intValue() & mask;
         value = value >>> segment;
         if (32 - shift <= segment) {
            for (int i = 0; i < 32 - shift; i += 8) {
               data.writeByte((byte)(bits >>> i));
            }
            break;
         }

         if (value == 0) {
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
   protected Integer readNumberData(DataInput data) throws IOException {
      int segment = this.byteSegment * 8 - 1;
      long mask = 1L << segment;
      int value = 0;

      for (int shift = 0; shift < 32; shift += segment) {
         if (32 - shift <= segment) {
            int bits = 0;

            for (int i = 0; i < 32 - shift; i += 8) {
               bits |= Byte.toUnsignedInt(data.readByte()) << i;
            }

            value |= bits << shift;
            break;
         }

         long bits = 0L;

         for (int i = 0; i <= segment; i += 8) {
            bits |= Byte.toUnsignedLong(data.readByte()) << i;
         }

         if ((bits & mask) != 0L) {
            value = (int)(value | bits - mask << shift);
            break;
         }

         value = (int)(value | bits << shift);
      }

      return value;
   }
}
