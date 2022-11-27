package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;

public class StringAdapter extends Adapter<String> {
   private final boolean nullable;

   public StringAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public StringAdapter asNullable() {
      return this.nullable ? this : new StringAdapter(true);
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public String validate(String value, SyncContext context) {
      if (!this.nullable && value == null) {
         throw new UnsupportedOperationException("Value cannot be null");
      } else {
         return value;
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, String value) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeString(value);
      }
   }

   public String readValue(BitBuffer buffer, SyncContext context, String value) {
      return this.nullable && buffer.readBoolean() ? null : buffer.readString();
   }
}
