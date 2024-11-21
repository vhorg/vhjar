package iskallia.vault.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import iskallia.vault.VaultMod;
import iskallia.vault.client.shader.ClipPositionTexShader;
import iskallia.vault.client.shader.ColorizePositionTexShader;
import iskallia.vault.client.shader.GrayscalePositionTexShader;
import iskallia.vault.client.shader.ShaderChain;
import iskallia.vault.client.shader.glsl.NativeGrayscaleShader;
import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;

@OnlyIn(Dist.CLIENT)
public class ModShaders {
   private static ClipPositionTexShader clipPositionTexShader;
   private static GrayscalePositionTexShader grayscalePositionTexShader;
   private static ColorizePositionTexShader colorizePositionTexShader;
   private static NativeGrayscaleShader nativeGrayscaleShader;
   private static ShaderChain grayscaleShaderChain;

   public static ClipPositionTexShader getClipPositionTexShader() {
      return clipPositionTexShader;
   }

   public static GrayscalePositionTexShader getGrayscalePositionTexShader() {
      return grayscalePositionTexShader;
   }

   public static ColorizePositionTexShader getColorizePositionTexShader() {
      return colorizePositionTexShader;
   }

   public static NativeGrayscaleShader getNativeGrayscaleShader() {
      return nativeGrayscaleShader;
   }

   public static ShaderChain getGrayscaleShaderChain() {
      return grayscaleShaderChain;
   }

   public static void register(RegisterShadersEvent event) {
      registerShader(
         event,
         VaultMod.id("position_tex_clip"),
         DefaultVertexFormat.POSITION_TEX,
         shaderInstance -> clipPositionTexShader = new ClipPositionTexShader(shaderInstance)
      );
      registerShader(
         event,
         VaultMod.id("position_tex_grayscale"),
         DefaultVertexFormat.POSITION_TEX,
         shaderInstance -> grayscalePositionTexShader = new GrayscalePositionTexShader(shaderInstance)
      );
      registerShader(
         event,
         VaultMod.id("position_tex_colorize"),
         DefaultVertexFormat.POSITION_TEX,
         shaderInstance -> colorizePositionTexShader = new ColorizePositionTexShader(shaderInstance)
      );
      if (nativeGrayscaleShader != null) {
         nativeGrayscaleShader.destroy();
      }

      nativeGrayscaleShader = new NativeGrayscaleShader(event.getResourceManager()).withGrayscale(0.9F).withBrightness(0.7F);
      if (grayscaleShaderChain != null) {
         grayscaleShaderChain.destroy();
      }

      grayscaleShaderChain = ShaderChain.builder().addShader(nativeGrayscaleShader).build();
   }

   private static void registerShader(
      RegisterShadersEvent event, ResourceLocation resourceLocation, VertexFormat vertexFormat, Consumer<ShaderInstance> onLoaded
   ) {
      try {
         event.registerShader(new ShaderInstance(event.getResourceManager(), resourceLocation, vertexFormat), onLoaded);
      } catch (IOException var5) {
         VaultMod.LOGGER.error("Error loading shader %s".formatted(resourceLocation), var5);
      }
   }
}
