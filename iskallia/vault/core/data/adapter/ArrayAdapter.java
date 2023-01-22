package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import java.util.function.IntFunction;

public class ArrayAdapter<T> extends Adapter<T[]> {
   private final IntFunction<T[]> constructor;
   private final Adapter<T> elementAdapter;
   private final boolean nullable;

   public ArrayAdapter(IntFunction<T[]> constructor, Adapter<T> elementAdapter, boolean nullable) {
      this.constructor = constructor;
      this.elementAdapter = elementAdapter;
      this.nullable = nullable;
   }

   public ArrayAdapter<T> asNullable() {
      return this.nullable ? this : new ArrayAdapter<>(this.constructor, this.elementAdapter, true);
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public T[] validate(T[] value, SyncContext context) {
      return value;
   }

   public void writeValue(BitBuffer buffer, SyncContext context, T[] value) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeIntSegmented(value.length, 3);

         for (T element : value) {
            this.elementAdapter.writeValue(buffer, context, element);
         }
      }
   }

   public T[] readValue(BitBuffer buffer, SyncContext context, T[] value) {
      if (this.nullable && buffer.readBoolean()) {
         return null;
      } else {
         value = (T[])((Object[])this.constructor.apply(buffer.readIntSegmented(3)));

         for (int i = 0; i < value.length; i++) {
            value[i] = this.elementAdapter.readValue(buffer, context, value[i]);
         }

         return value;
      }
   }
}
