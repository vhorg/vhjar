package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import java.util.UUID;

public class UUIDAdapter extends Adapter<UUID> {
   private final boolean nullable;

   public UUIDAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public UUIDAdapter asNullable() {
      return this.nullable ? this : new UUIDAdapter(true);
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public UUID validate(UUID value, SyncContext context) {
      if (!this.nullable && value == null) {
         throw new UnsupportedOperationException("Value cannot be null");
      } else {
         return value;
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, UUID value) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeLong(value.getMostSignificantBits());
         buffer.writeLong(value.getLeastSignificantBits());
      }
   }

   public UUID readValue(BitBuffer buffer, SyncContext context, UUID value) {
      return this.nullable && buffer.readBoolean() ? null : new UUID(buffer.readLong(), buffer.readLong());
   }
}
