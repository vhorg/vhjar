package iskallia.vault.core.data;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.data.sync.handler.SyncHandler;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import java.util.function.Supplier;

public class Field<T> {
   private final IBitAdapter<T, SyncContext> adapter;
   private final SyncHandler<T> handler;
   private final Supplier<T> defaultValue;

   public Field(IBitAdapter<T, SyncContext> adapter, SyncHandler<T> handler, Supplier<T> defaultValue) {
      this.adapter = adapter;
      this.handler = handler;
      this.defaultValue = defaultValue;
   }

   public IBitAdapter<T, SyncContext> getAdapter() {
      return this.adapter;
   }

   public SyncHandler<T> getHandler() {
      return this.handler;
   }

   public Supplier<T> getDefaultValue() {
      return this.defaultValue;
   }

   public void writeValue(BitBuffer buffer, SyncContext context, T value) {
      this.adapter.writeBits(value, buffer, context);
   }

   public T readValue(BitBuffer buffer, SyncContext context) {
      return this.adapter.readBits(buffer, context).orElseGet(this.defaultValue);
   }

   public boolean canSync(T value, SyncContext context) {
      return this.handler.canSync(value, context);
   }
}
