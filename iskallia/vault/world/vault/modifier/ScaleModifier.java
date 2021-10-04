package iskallia.vault.world.vault.modifier;

import com.google.gson.annotations.Expose;
import net.minecraft.util.ResourceLocation;

public class ScaleModifier extends TexturedVaultModifier {
   @Expose
   private final float scale;

   public ScaleModifier(String name, ResourceLocation icon, float scale) {
      super(name, icon);
      this.scale = scale;
   }

   public float getScale() {
      return this.scale;
   }
}
