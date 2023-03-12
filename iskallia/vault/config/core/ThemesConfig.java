package iskallia.vault.config.core;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.ThemeKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.world.generator.theme.Theme;

public class ThemesConfig extends KeyRegistryConfig<KeyRegistry<ThemeKey, Theme>, ThemeKey> {
   @Override
   public KeyRegistry<ThemeKey, Theme> create() {
      return new KeyRegistry<>();
   }

   @Override
   public String getSimpleName() {
      return "themes";
   }

   @Override
   protected void reset() {
      this.keys.add(ThemeKey.create(VaultMod.id("classic_desert"), "Desert", 16777215).with(Version.v1_0, null));
   }
}
