package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public class VoidAdapter<T> extends Adapter<T> {
   @Override
   public T validate(T value, SyncContext context) {
      return value;
   }

   @Override
   public void writeValue(BitBuffer buffer, SyncContext context, T value) {
   }

   @Override
   public T readValue(BitBuffer buffer, SyncContext context, T value) {
      return null;
   }
}
