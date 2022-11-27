package iskallia.vault.core.data.key;

import iskallia.vault.core.world.processor.Palette;
import net.minecraft.resources.ResourceLocation;

public class PaletteKey extends NamedKey<PaletteKey, Palette> {
   protected PaletteKey(ResourceLocation id, String name) {
      super(id, name);
   }

   public static PaletteKey empty() {
      return new PaletteKey(null, null);
   }

   public static PaletteKey create(ResourceLocation id, String name) {
      return new PaletteKey(id, name);
   }
}
