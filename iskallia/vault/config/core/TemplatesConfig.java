package iskallia.vault.config.core;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.TemplateKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.world.template.Template;

public class TemplatesConfig extends KeyRegistryConfig<KeyRegistry<TemplateKey, Template>, TemplateKey> {
   @Override
   public KeyRegistry<TemplateKey, Template> create() {
      return new KeyRegistry<>();
   }

   @Override
   public String getSimpleName() {
      return "templates";
   }

   @Override
   protected void reset() {
      this.keys.add(TemplateKey.create(VaultMod.id("test_room"), "Test Room").with(Version.v1_0, null));
   }
}
