package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public class SegmentedIntAdapter extends Adapter<Integer> {
   protected final int segment;

   public SegmentedIntAdapter(int segment) {
      this.segment = segment;
   }

   public int getSegment() {
      return this.segment;
   }

   public Integer validate(Integer value, SyncContext context) {
      return value;
   }

   public void writeValue(BitBuffer buffer, SyncContext context, Integer value) {
      buffer.writeIntSegmented(value, this.segment);
   }

   public Integer readValue(BitBuffer buffer, SyncContext context, Integer value) {
      return buffer.readIntSegmented(this.segment);
   }
}
