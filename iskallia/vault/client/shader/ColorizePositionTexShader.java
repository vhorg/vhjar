package iskallia.vault.client.shader;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;

public class ColorizePositionTexShader extends AbstractShader {
   public static final String UNIFORM_COLORIZE = "Colorize";
   public static final String UNIFORM_GRAYSCALE = "Grayscale";
   public static final String UNIFORM_BRIGHTNESS = "Brightness";

   public ColorizePositionTexShader(ShaderInstance shaderInstance) {
      super(shaderInstance);
   }

   public ColorizePositionTexShader withColorize(float red, float green, float blue) {
      Uniform uniform = this.shaderInstance.getUniform("Colorize");
      if (uniform != null) {
         uniform.set(red, green, blue);
      }

      return this;
   }

   public ColorizePositionTexShader withGrayscale(float value) {
      Uniform uniform = this.shaderInstance.getUniform("Grayscale");
      if (uniform != null) {
         uniform.set(value);
      }

      return this;
   }

   public ColorizePositionTexShader withBrightness(float value) {
      Uniform uniform = this.shaderInstance.getUniform("Brightness");
      if (uniform != null) {
         uniform.set(value);
      }

      return this;
   }
}
