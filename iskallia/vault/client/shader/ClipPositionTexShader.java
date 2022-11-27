package iskallia.vault.client.shader;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;

public class ClipPositionTexShader extends AbstractShader {
   public static final String UNIFORM_CLIP = "Clip";

   public ClipPositionTexShader(ShaderInstance shaderInstance) {
      super(shaderInstance);
   }

   public ClipPositionTexShader withClip(float x, float y, float width, float height) {
      Uniform uniform = this.shaderInstance.getUniform("Clip");
      if (uniform != null) {
         uniform.set(x, y, width, height);
      }

      return this;
   }
}
