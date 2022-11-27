package iskallia.vault.core.data.key;

import iskallia.vault.core.world.generator.theme.Theme;
import net.minecraft.resources.ResourceLocation;

public class ThemeKey extends NamedKey<ThemeKey, Theme> {
   protected ThemeKey(ResourceLocation id, String name) {
      super(id, name);
   }

   public static ThemeKey empty() {
      return new ThemeKey(null, null);
   }

   public static ThemeKey create(ResourceLocation id, String name) {
      return new ThemeKey(id, name);
   }
}
