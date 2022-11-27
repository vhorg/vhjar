package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public class EnumAdapter<E extends Enum<E>> extends Adapter<E> {
   private final Class<E> type;
   private final boolean nullable;

   public EnumAdapter(Class<E> type, boolean nullable) {
      this.type = type;
      this.nullable = nullable;
   }

   public EnumAdapter<E> asNullable() {
      return this.nullable ? this : new EnumAdapter<>(this.type, true);
   }

   public E validate(E value, SyncContext context) {
      if (!this.nullable && value == null) {
         throw new UnsupportedOperationException("Value cannot be null");
      } else {
         return value;
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, E value) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeEnum(value);
      }
   }

   public E readValue(BitBuffer buffer, SyncContext context, E value) {
      return this.nullable && buffer.readBoolean() ? null : buffer.readEnum(this.type);
   }
}
