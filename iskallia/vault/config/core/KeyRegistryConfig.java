package iskallia.vault.config.core;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.core.data.key.VersionedKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class KeyRegistryConfig<R extends KeyRegistry<T, ?>, T extends VersionedKey<? extends T, ?>> extends Config {
   @Expose
   protected List<T> keys = new ArrayList<>();

   public abstract R create();

   public abstract String getSimpleName();

   @Override
   public String getName() {
      return "gen%s%s".formatted(File.separator, this.getSimpleName());
   }

   public R toRegistry() {
      R registry = this.create();
      this.keys.forEach(registry::register);
      this.keys.clear();
      return registry;
   }
}
