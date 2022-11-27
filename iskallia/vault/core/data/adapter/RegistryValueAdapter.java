package iskallia.vault.core.data.adapter;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.ICompound;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryValueAdapter<T extends ICompound<T>, K extends VersionedKey<? extends K, ? extends V>, V> extends Adapter<T> {
   private final Supplier<KeyRegistry<K, V>> registry;
   private final Function<T, K> serializer;
   private final Function<V, T> deserializer;

   public RegistryValueAdapter(Supplier<KeyRegistry<K, V>> registry, Function<T, K> serializer, Function<V, T> deserializer) {
      this.registry = registry;
      this.serializer = serializer;
      this.deserializer = deserializer;
   }

   public T validate(T value, SyncContext context) {
      int index = this.registry.get().getIndex(this.serializer.apply(value), context.getVersion());
      if (index < 0) {
         throw new UnsupportedOperationException("Value not present in the registry");
      } else {
         return value;
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, T value) {
      int index = this.registry.get().getIndex(this.serializer.apply(value), context.getVersion());
      buffer.writeIntBounded(index, 0, this.registry.get().getSize(context.getVersion()) - 1);
      value.write(buffer, context);
   }

   public T readValue(BitBuffer buffer, SyncContext context, T value) {
      Version version = context.getVersion();
      int size = this.registry.get().getSize(version);
      int index = buffer.readIntBounded(0, size - 1);
      return this.deserializer.apply((V)this.registry.get().getKey(index, version).get(version)).read(buffer, context);
   }
}
