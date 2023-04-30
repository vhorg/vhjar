package iskallia.vault.core.vault.modifier.modifier.deprecated;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import net.minecraft.resources.ResourceLocation;

@Deprecated
public class ScaleModifier extends VaultModifier<ScaleModifier.Properties> {
   public ScaleModifier(ResourceLocation id, ScaleModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
   }

   public static class Properties {
      @Expose
      private final float scale;

      public Properties(float scale) {
         this.scale = scale;
      }

      public float getScale() {
         return this.scale;
      }
   }
}
