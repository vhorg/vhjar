package iskallia.vault.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Supplier;
import net.minecraft.client.renderer.ShaderInstance;

public abstract class AbstractShader {
   protected final ShaderInstance shaderInstance;
   protected final Supplier<ShaderInstance> supplier;

   protected AbstractShader(ShaderInstance shaderInstance) {
      this.shaderInstance = shaderInstance;
      this.supplier = () -> this.shaderInstance;
   }

   public void enable() {
      RenderSystem.setShader(this.supplier);
   }
}
