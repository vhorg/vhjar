package iskallia.vault.client.shader;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;

public class GrayscalePositionTexShader extends AbstractShader {
   public static final String UNIFORM_GRAYSCALE = "Grayscale";
   public static final String UNIFORM_BRIGHTNESS = "Brightness";

   public GrayscalePositionTexShader(ShaderInstance shaderInstance) {
      super(shaderInstance);
   }

   public GrayscalePositionTexShader withGrayscale(float value) {
      Uniform uniform = this.shaderInstance.getUniform("Grayscale");
      if (uniform != null) {
         uniform.set(value);
      }

      return this;
   }

   public GrayscalePositionTexShader withBrightness(float value) {
      Uniform uniform = this.shaderInstance.getUniform("Brightness");
      if (uniform != null) {
         uniform.set(value);
      }

      return this;
   }
}
