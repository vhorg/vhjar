package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import java.util.function.ToIntFunction;

public class OrdinalAdapter<T> extends Adapter<T> {
   private final ToIntFunction<T> mapper;
   private final boolean nullable;
   private final T[] array;

   public OrdinalAdapter(ToIntFunction<T> mapper, boolean nullable, T... array) {
      this.mapper = mapper;
      this.nullable = nullable;
      this.array = array;
   }

   public OrdinalAdapter<T> asNullable() {
      return this.nullable ? this : new OrdinalAdapter<>(this.mapper, true, this.array);
   }

   @Override
   public T validate(T value, SyncContext context) {
      if (!this.nullable && value == null) {
         throw new UnsupportedOperationException("Value cannot be null");
      } else {
         int index = this.mapper.applyAsInt(value);
         if (index >= 0 && index < this.array.length) {
            return value;
         } else {
            throw new UnsupportedOperationException("Value does not have a matching index");
         }
      }
   }

   @Override
   public void writeValue(BitBuffer buffer, SyncContext context, T value) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeOrdinal(value, this.mapper, this.array);
      }
   }

   @Override
   public T readValue(BitBuffer buffer, SyncContext context, T value) {
      return this.nullable && buffer.readBoolean() ? null : buffer.readOrdinal(this.array);
   }
}
