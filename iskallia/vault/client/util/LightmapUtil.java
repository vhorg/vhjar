package iskallia.vault.client.util;

import iskallia.vault.client.gui.helper.LightmapHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class LightmapUtil {
   public static float getLightmapBrightness(int packedLight) {
      DynamicTexture lightTex = Minecraft.func_71410_x().field_71460_t.func_228384_l_().field_205110_a;
      if (lightTex.func_195414_e() == null) {
         return 1.0F;
      } else {
         int block = LightmapHelper.getUnpackedBlockCoords(packedLight);
         int sky = LightmapHelper.getUnpackedSkyCoords(packedLight);
         int lightPx = lightTex.func_195414_e().func_195709_a(block, sky);
         return (lightPx & 0xFF) / 255.0F;
      }
   }
}
