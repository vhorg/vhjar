package iskallia.vault.core.data.key.registry;

import iskallia.vault.core.data.key.SupplierKey;
import java.util.function.Supplier;

public class SupplierRegistry<T> extends KeyRegistry<SupplierKey<? extends T>, Supplier<? extends T>> {
   public SupplierRegistry<T> add(SupplierKey<? extends T> key) {
      this.register(key);
      return this;
   }
}
