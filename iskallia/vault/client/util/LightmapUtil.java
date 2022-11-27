package iskallia.vault.client.util;

import iskallia.vault.client.gui.helper.LightmapHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class LightmapUtil {
   public static float getLightmapBrightness(int packedLight) {
      DynamicTexture lightTex = Minecraft.getInstance().gameRenderer.lightTexture().lightTexture;
      if (lightTex.getPixels() == null) {
         return 1.0F;
      } else {
         int block = LightmapHelper.getUnpackedBlockCoords(packedLight);
         int sky = LightmapHelper.getUnpackedSkyCoords(packedLight);
         int lightPx = lightTex.getPixels().getPixelRGBA(block, sky);
         return (lightPx & 0xFF) / 255.0F;
      }
   }
}
