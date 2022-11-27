package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import net.minecraft.resources.ResourceLocation;

public class IdentifierAdapter extends Adapter<ResourceLocation> {
   private final boolean nullable;

   public IdentifierAdapter(boolean nullable) {
      this.nullable = nullable;
   }

   public IdentifierAdapter asNullable() {
      return this.nullable ? this : new IdentifierAdapter(true);
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public ResourceLocation validate(ResourceLocation value, SyncContext context) {
      if (!this.nullable && value == null) {
         throw new UnsupportedOperationException("Value cannot be null");
      } else {
         return value;
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, ResourceLocation value) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeIdentifier(value);
      }
   }

   public ResourceLocation readValue(BitBuffer buffer, SyncContext context, ResourceLocation value) {
      return this.nullable && buffer.readBoolean() ? null : buffer.readIdentifier();
   }
}
