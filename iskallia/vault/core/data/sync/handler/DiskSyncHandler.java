package iskallia.vault.core.data.sync.handler;

import iskallia.vault.core.data.sync.context.DiskSyncContext;
import iskallia.vault.core.data.sync.context.SyncContext;

public class DiskSyncHandler<T> implements SyncHandler<T> {
   @Override
   public boolean canSync(T value, SyncContext context) {
      return context instanceof DiskSyncContext;
   }

   public static class Factory {
      public <T> DiskSyncHandler<T> all() {
         return new DiskSyncHandler<>();
      }
   }
}
