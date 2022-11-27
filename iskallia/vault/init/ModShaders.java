package iskallia.vault.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import iskallia.vault.VaultMod;
import iskallia.vault.client.shader.ClipPositionTexShader;
import iskallia.vault.client.shader.ColorizePositionTexShader;
import iskallia.vault.client.shader.GrayscalePositionTexShader;
import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;

public class ModShaders {
   private static ClipPositionTexShader clipPositionTexShader;
   private static GrayscalePositionTexShader grayscalePositionTexShader;
   private static ColorizePositionTexShader colorizePositionTexShader;

   public static ClipPositionTexShader getClipPositionTexShader() {
      return clipPositionTexShader;
   }

   public static GrayscalePositionTexShader getGrayscalePositionTexShader() {
      return grayscalePositionTexShader;
   }

   public static ColorizePositionTexShader getColorizePositionTexShader() {
      return colorizePositionTexShader;
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
