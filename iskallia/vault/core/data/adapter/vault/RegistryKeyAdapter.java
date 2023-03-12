package iskallia.vault.core.data.adapter.vault;

import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import java.util.Optional;
import java.util.function.Supplier;

public class RegistryKeyAdapter<K extends VersionedKey<? extends K, ? extends V>, V> implements IBitAdapter<K, SyncContext> {
   private final Supplier<KeyRegistry<K, V>> registry;
   private final boolean nullable;

   public RegistryKeyAdapter(Supplier<KeyRegistry<K, V>> registry, boolean nullable) {
      this.registry = registry;
      this.nullable = nullable;
   }

   public static <K extends VersionedKey<? extends K, ? extends V>, V> RegistryKeyAdapter<K, V> of(Supplier<KeyRegistry<K, V>> registry) {
      return new RegistryKeyAdapter<>(registry, false);
   }

   public RegistryKeyAdapter<K, V> asNullable() {
      return new RegistryKeyAdapter<>(this.registry, true);
   }

   public void writeBits(K value, BitBuffer buffer, SyncContext context) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeIdentifier(value.getId());
      }
   }

   public Optional<K> readBits(BitBuffer buffer, SyncContext context) {
      return this.nullable && buffer.readBoolean() ? Optional.empty() : Optional.ofNullable(this.registry.get().getKey(buffer.readIdentifier()));
   }
}
