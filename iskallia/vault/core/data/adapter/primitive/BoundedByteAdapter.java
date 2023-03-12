package iskallia.vault.core.data.adapter.primitive;

import iskallia.vault.core.net.BitBuffer;

public class BoundedByteAdapter extends ByteAdapter {
   protected final byte min;
   protected final byte max;
   protected final int bits;

   public BoundedByteAdapter(byte min, byte max, boolean nullable) {
      super(nullable);
      this.min = min;
      this.max = max;
      this.bits = 8 - Integer.numberOfLeadingZeros(this.max - this.min);
   }

   public byte getMin() {
      return this.min;
   }

   public byte getMax() {
      return this.max;
   }

   public int getBits() {
      return this.bits;
   }

   @Override
   protected void writeNumberBits(Byte value, BitBuffer buffer) {
      buffer.writeIntBits(value - this.min, this.bits);
   }

   @Override
   protected Byte readNumberBits(BitBuffer buffer) {
      return (byte)(this.min + buffer.readByteBits(this.bits));
   }
}
