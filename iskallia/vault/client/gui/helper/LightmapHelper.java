package iskallia.vault.client.gui.helper;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

public class LightmapHelper {
   public static int getPackedFullbrightCoords() {
      return 15728880;
   }

   public static int getPackedLightCoords(int lightValue) {
      return getPackedLightCoords(lightValue, lightValue);
   }

   public static int getPackedLightCoords(int skyLight, int blockLight) {
      return skyLight << 20 | blockLight << 4;
   }

   public static int getPackedLightCoords(BlockAndTintGetter world, BlockPos at) {
      return LevelRenderer.getLightColor(world, at);
   }

   public static int getUnpackedSkyCoords(int packed) {
      return packed >> 20 & 15;
   }

   public static int getUnpackedBlockCoords(int packed) {
      return packed >> 4 & 15;
   }

   public static int getUnpackedBrightestCoords(int packed) {
      return Math.max(getUnpackedSkyCoords(packed), getUnpackedBlockCoords(packed));
   }
}
