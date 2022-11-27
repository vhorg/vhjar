package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public class BoundedIntAdapter extends Adapter<Integer> {
   protected final int min;
   protected final int max;
   protected final int bits;

   public BoundedIntAdapter(int min, int max) {
      this.min = min;
      this.max = max;
      this.bits = 32 - Integer.numberOfLeadingZeros(this.max - this.min);
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   public int getBits() {
      return this.bits;
   }

   public Integer validate(Integer value, SyncContext context) {
      if (value >= this.min && value <= this.max) {
         return value;
      } else {
         throw new UnsupportedOperationException(String.format("Value %d is not between %d and %d", value, this.min, this.max));
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, Integer value) {
      buffer.writeIntBits(value - this.min, this.bits);
   }

   public Integer readValue(BitBuffer buffer, SyncContext context, Integer value) {
      return this.min + buffer.readIntBits(this.bits);
   }
}
