package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public class LongAdapter extends Adapter<Long> {
   public Long validate(Long value, SyncContext context) {
      return value;
   }

   public void writeValue(BitBuffer buffer, SyncContext context, Long value) {
      buffer.writeLong(value);
   }

   public Long readValue(BitBuffer buffer, SyncContext context, Long value) {
      return buffer.readLong();
   }
}
