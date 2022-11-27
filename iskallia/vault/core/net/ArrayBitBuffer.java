package iskallia.vault.core.net;

public class ArrayBitBuffer extends BitBuffer {
   protected long[] buffer;
   protected int position;

   protected ArrayBitBuffer(long[] buffer, int position) {
      this.buffer = buffer;
      this.position = position;
   }

   public static ArrayBitBuffer empty() {
      return new ArrayBitBuffer(new long[16], 0);
   }

   public static ArrayBitBuffer backing(long[] array, int position) {
      return new ArrayBitBuffer(array, position);
   }

   public int getPosition() {
      return this.position;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   @Override
   protected void writeBits(long value, int bits) {
      int bucket = this.position >>> 6;
      int index = this.position & 63;
      this.ensureCapacity(bucket + 1);
      value &= bits == 64 ? -1L : (1L << bits) - 1L;
      this.buffer[bucket] = this.buffer[bucket] | value << index;
      if (bits + index >= 64 && index != 0) {
         this.buffer[bucket + 1] = this.buffer[bucket + 1] | value >>> 64 - index;
      }

      this.position += bits;
   }

   @Override
   protected long readBits(int bits) {
      int bucket = this.position >>> 6;
      int index = this.position & 63;
      this.ensureCapacity(bucket + 1);
      int current = Math.min(bits, 64 - index);
      long mask1 = current == 64 ? -1L : (1L << current) - 1L;
      long value = this.buffer[bucket] >>> index & mask1;
      if (bits + index >= 64) {
         long mask2 = (1L << bits - current) - 1L;
         value |= (this.buffer[bucket + 1] & mask2) << current;
      }

      this.position += bits;
      return value;
   }

   protected void ensureCapacity(int size) {
      if (size >= this.buffer.length) {
         long[] newBuffer = new long[this.buffer.length << 1];
         System.arraycopy(this.buffer, 0, newBuffer, 0, this.buffer.length);
         this.buffer = newBuffer;
      }
   }

   public long[] toLongArray() {
      long[] array = new long[(this.position >>> 6) + ((this.position & 63) == 0 ? 0 : 1)];
      System.arraycopy(this.buffer, 0, array, 0, array.length);
      return array;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      int start = this.getPosition();
      this.setPosition(0);

      while (this.getPosition() < start) {
         int b = this.readIntBits(8);

         for (int i = 0; i < 8; i++) {
            sb.append((b << i & 128) == 0 ? "0" : "1");
         }

         sb.append("-");
      }

      this.setPosition(start);
      return sb.toString();
   }
}
