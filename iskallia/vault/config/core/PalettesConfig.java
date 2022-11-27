package iskallia.vault.config.core;

import iskallia.vault.VaultMod;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.PaletteKey;
import iskallia.vault.core.data.key.registry.KeyRegistry;
import iskallia.vault.core.world.processor.Palette;

public class PalettesConfig extends KeyRegistryConfig<KeyRegistry<PaletteKey, Palette>, PaletteKey> {
   @Override
   public KeyRegistry<PaletteKey, Palette> create() {
      return new KeyRegistry<>();
   }

   @Override
   public String getSimpleName() {
      return "palettes";
   }

   @Override
   protected void reset() {
      this.keys.add(PaletteKey.create(VaultMod.id("desert"), "Desert").with(Version.v1_0, null));
   }
}
