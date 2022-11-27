package iskallia.vault.config.core;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.world.template.data.TemplatePool;

public class TemplatePoolsConfig extends KeyRegistryConfig<KeyRegistry<TemplatePoolKey, TemplatePool>, TemplatePoolKey> {
   @Override
   public KeyRegistry<TemplatePoolKey, TemplatePool> create() {
      return new KeyRegistry<>();
   }

   @Override
   public String getSimpleName() {
      return "template_pools";
   }

   @Override
   protected void reset() {
      this.keys.add(TemplatePoolKey.create(VaultMod.id("desert_rooms"), "Desert Rooms").with(Version.v1_0, null));
   }
}
