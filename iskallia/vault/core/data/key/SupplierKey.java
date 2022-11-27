package iskallia.vault.core.data.key;

import iskallia.vault.VaultMod;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class SupplierKey<T> extends VersionedKey<SupplierKey<T>, Supplier<T>> {
   protected SupplierKey(ResourceLocation id) {
      super(id);
   }

   public static <T> SupplierKey<T> of(String id, Class<T> type) {
      return new SupplierKey<>(VaultMod.id(id));
   }

   public static <T> SupplierKey<T> of(ResourceLocation id, Class<T> type) {
      return new SupplierKey<>(id);
   }
}
