package iskallia.vault.world.vault.modifier;

import net.minecraft.util.ResourceLocation;

public class NoExitModifier extends TexturedVaultModifier {
   public NoExitModifier(String name, ResourceLocation icon) {
      super(name, icon);
      this.format(this.getColor(), "F");
   }
}
