package iskallia.vault.core.data.sync.handler;

import iskallia.vault.core.data.sync.context.SyncContext;

@FunctionalInterface
public interface SyncHandler<T> {
   boolean canSync(T var1, SyncContext var2);

   default SyncHandler<T> or(SyncHandler<T> other) {
      return (value, context) -> this.canSync(value, context) || other.canSync(value, context);
   }

   default SyncHandler<T> and(SyncHandler<T> other) {
      return (value, context) -> this.canSync(value, context) && other.canSync(value, context);
   }
}
