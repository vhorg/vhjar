package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public class BooleanAdapter extends Adapter<Boolean> {
   public Boolean validate(Boolean value, SyncContext context) {
      return value;
   }

   public void writeValue(BitBuffer buffer, SyncContext context, Boolean value) {
      buffer.writeBoolean(value);
   }

   public Boolean readValue(BitBuffer buffer, SyncContext context, Boolean value) {
      return buffer.readBoolean();
   }
}
