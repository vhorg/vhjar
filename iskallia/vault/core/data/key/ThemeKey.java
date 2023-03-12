package iskallia.vault.core.data.key;

import iskallia.vault.core.world.generator.theme.Theme;
import net.minecraft.resources.ResourceLocation;

public class ThemeKey extends NamedKey<ThemeKey, Theme> {
   protected int color;

   protected ThemeKey(ResourceLocation id, String name, int color) {
      super(id, name);
      this.color = color;
   }

   public int getColor() {
      return this.color;
   }

   public static ThemeKey empty() {
      return new ThemeKey(null, null, 0);
   }

   public static ThemeKey create(ResourceLocation id, String name, int color) {
      return new ThemeKey(id, name, color);
   }
}
