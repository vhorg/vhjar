package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import java.util.function.Supplier;

public class RegistryKeyAdapter<K extends VersionedKey<? extends K, ? extends V>, V> extends Adapter<K> {
   private final Supplier<KeyRegistry<K, V>> registry;
   private final boolean nullable;

   public RegistryKeyAdapter(Supplier<KeyRegistry<K, V>> registry, boolean nullable) {
      this.registry = registry;
      this.nullable = nullable;
   }

   public RegistryKeyAdapter<K, V> asNullable() {
      return this.nullable ? this : new RegistryKeyAdapter<>(this.registry, true);
   }

   public K validate(K value, SyncContext context) {
      if (this.nullable && value == null) {
         return null;
      } else if (this.registry.get().getKey(value.getId()) == null) {
         throw new UnsupportedOperationException("Value not present in the registry");
      } else {
         return value;
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, K value) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeIdentifier(value.getId());
      }
   }

   public K readValue(BitBuffer buffer, SyncContext context, K value) {
      return this.nullable && buffer.readBoolean() ? null : this.registry.get().getKey(buffer.readIdentifier());
   }
}
