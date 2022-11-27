package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ResourceKeyAdapter<T> extends Adapter<ResourceKey<T>> {
   private final ResourceKey<Registry<T>> registry;
   private final boolean nullable;

   public ResourceKeyAdapter(ResourceKey<Registry<T>> registry, boolean nullable) {
      this.registry = registry;
      this.nullable = nullable;
   }

   public ResourceKeyAdapter<T> asNullable() {
      return this.nullable ? this : new ResourceKeyAdapter<>(this.registry, true);
   }

   public boolean isNullable() {
      return this.nullable;
   }

   public ResourceKey<T> validate(ResourceKey<T> value, SyncContext context) {
      if (!this.nullable && value == null) {
         throw new UnsupportedOperationException("Value cannot be null");
      } else {
         return value;
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, ResourceKey<T> value) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeIdentifier(value.location());
      }
   }

   public ResourceKey<T> readValue(BitBuffer buffer, SyncContext context, ResourceKey<T> value) {
      return this.nullable && buffer.readBoolean() ? null : ResourceKey.create(this.registry, buffer.readIdentifier());
   }
}
