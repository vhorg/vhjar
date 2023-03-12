package iskallia.vault.core.data.adapter.vault;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.ICompound;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryValueAdapter<T extends ICompound<T>, K extends VersionedKey<? extends K, ? extends V>, V> implements IBitAdapter<T, SyncContext> {
   private final Supplier<KeyRegistry<K, V>> registry;
   private final Function<T, K> serializer;
   private final Function<V, T> deserializer;

   public RegistryValueAdapter(Supplier<KeyRegistry<K, V>> registry, Function<T, K> serializer, Function<V, T> deserializer) {
      this.registry = registry;
      this.serializer = serializer;
      this.deserializer = deserializer;
   }

   public static <T extends ICompound<T>, K extends VersionedKey<? extends K, ? extends V>, V> RegistryValueAdapter<T, K, V> of(
      Supplier<KeyRegistry<K, V>> registry, Function<T, K> serializer, Function<V, T> deserializer
   ) {
      return new RegistryValueAdapter<>(registry, serializer, deserializer);
   }

   public void writeBits(T value, BitBuffer buffer, SyncContext context) {
      int index = this.registry.get().getIndex(this.serializer.apply(value), context.getVersion());
      buffer.writeIntBounded(index, 0, this.registry.get().getSize(context.getVersion()) - 1);
      value.write(buffer, context);
   }

   public Optional<T> readBits(BitBuffer buffer, SyncContext context) {
      Version version = context.getVersion();
      int size = this.registry.get().getSize(version);
      int index = buffer.readIntBounded(0, size - 1);
      return Optional.of(this.deserializer.apply((V)this.registry.get().getKey(index, version).get(version)).read(buffer, context));
   }
}
