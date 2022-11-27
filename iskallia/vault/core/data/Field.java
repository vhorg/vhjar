package iskallia.vault.core.data;

import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.data.sync.handler.SyncHandler;
import iskallia.vault.core.net.BitBuffer;
import java.util.function.Supplier;

public class Field<T> {
   private final Adapter<T> adapter;
   private final SyncHandler<T> handler;
   private final Supplier<T> defaultValue;

   public Field(Adapter<T> adapter, SyncHandler<T> handler, Supplier<T> defaultValue) {
      this.adapter = adapter;
      this.handler = handler;
      this.defaultValue = defaultValue;
   }

   public Adapter<T> getAdapter() {
      return this.adapter;
   }

   public SyncHandler<T> getHandler() {
      return this.handler;
   }

   public Supplier<T> getDefaultValue() {
      return this.defaultValue;
   }

   public T validate(T value, SyncContext context) {
      return this.adapter.validate(value, context);
   }

   public void writeValue(BitBuffer buffer, SyncContext context, T value) {
      this.adapter.writeValue(buffer, context, value);
   }

   public T readValue(BitBuffer buffer, SyncContext context) {
      return this.adapter.readValue(buffer, context, this.defaultValue.get());
   }

   public boolean canSync(T value, SyncContext context) {
      return this.handler.canSync(value, context);
   }
}
