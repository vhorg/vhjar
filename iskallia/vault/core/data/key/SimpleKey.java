package iskallia.vault.core.data.key;

import iskallia.vault.VaultMod;
import net.minecraft.resources.ResourceLocation;

public class SimpleKey<T> extends VersionedKey<SimpleKey<T>, T> {
   protected SimpleKey(ResourceLocation id) {
      super(id);
   }

   public static <T> SimpleKey<T> of(String id, Class<T> type) {
      return new SimpleKey<>(VaultMod.id(id));
   }

   public static <T> SimpleKey<T> of(ResourceLocation id, Class<T> type) {
      return new SimpleKey<>(id);
   }
}
