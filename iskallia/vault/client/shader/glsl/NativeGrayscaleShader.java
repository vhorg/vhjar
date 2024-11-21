package iskallia.vault.client.shader.glsl;

import iskallia.vault.VaultMod;
import net.minecraft.server.packs.resources.ResourceProvider;

public class NativeGrayscaleShader extends NativeShader {
   public NativeGrayscaleShader(ResourceProvider resourceLoader) {
      super(resourceLoader, VaultMod.id("grayscale"));
   }

   public NativeGrayscaleShader withGrayscale(float value) {
      this.applyFloatValue("Grayscale", value);
      return this;
   }

   public NativeGrayscaleShader withBrightness(float value) {
      this.applyFloatValue("Brightness", value);
      return this;
   }
}
