package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.ICompound;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import java.util.function.Supplier;

public class CompoundAdapter<T extends ICompound<?>> extends Adapter<T> {
   private final Supplier<T> supplier;
   private final boolean nullable;

   public CompoundAdapter(Supplier<T> supplier, boolean nullable) {
      this.supplier = supplier;
      this.nullable = nullable;
   }

   public CompoundAdapter<T> asNullable() {
      return this.nullable ? this : new CompoundAdapter<>(this.supplier, true);
   }

   public Supplier<T> getSupplier() {
      return this.supplier;
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public T validate(T value, SyncContext context) {
      if (!this.nullable && value == null) {
         throw new UnsupportedOperationException("Value cannot be null");
      } else {
         return value;
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, T value) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         value.write(buffer, context);
      }
   }

   public T readValue(BitBuffer buffer, SyncContext context, T value) {
      if (this.nullable && buffer.readBoolean()) {
         return null;
      } else {
         if (this.supplier != null) {
            value = this.supplier.get();
         }

         value.read(buffer, context);
         return value;
      }
   }
}
