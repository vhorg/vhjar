package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public class IntAdapter extends Adapter<Integer> {
   public Integer validate(Integer value, SyncContext context) {
      return value;
   }

   public void writeValue(BitBuffer buffer, SyncContext context, Integer value) {
      buffer.writeInt(value);
   }

   public Integer readValue(BitBuffer buffer, SyncContext context, Integer value) {
      return buffer.readInt();
   }
}
