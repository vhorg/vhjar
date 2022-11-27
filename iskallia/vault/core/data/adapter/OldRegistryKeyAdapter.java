package iskallia.vault.core.data.adapter;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import java.util.function.Supplier;

public class OldRegistryKeyAdapter<K extends VersionedKey<? extends K, ? extends V>, V> extends Adapter<K> {
   private final Supplier<KeyRegistry<K, V>> registry;
   private final boolean nullable;

   public OldRegistryKeyAdapter(Supplier<KeyRegistry<K, V>> registry, boolean nullable) {
      this.registry = registry;
      this.nullable = nullable;
   }

   public OldRegistryKeyAdapter<K, V> asNullable() {
      return this.nullable ? this : new OldRegistryKeyAdapter<>(this.registry, true);
   }

   public K validate(K value, SyncContext context) {
      if (this.nullable && value == null) {
         return null;
      } else {
         int index = this.registry.get().getIndex(value, context.getVersion());
         if (index < 0) {
            throw new UnsupportedOperationException("Value not present in the registry");
         } else {
            return value;
         }
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, K value) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         int index = this.registry.get().getIndex(value, context.getVersion());
         buffer.writeIntBounded(index, 0, this.registry.get().getSize(context.getVersion()) - 1);
      }
   }

   public K readValue(BitBuffer buffer, SyncContext context, K value) {
      if (this.nullable && buffer.readBoolean()) {
         return null;
      } else {
         Version version = context.getVersion();
         int size = this.registry.get().getSize(version);
         int index = buffer.readIntBounded(0, size - 1);
         return this.registry.get().getKey(index, version);
      }
   }
}
