package iskallia.vault.client.util.color;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ColorUtil {
   private ColorUtil() {
   }

   public static double fastPerceptualColorDistanceSquared(int[] color1, int[] color2) {
      int red1 = color1[0];
      int red2 = color2[0];
      int redMean = red1 + red2 >> 1;
      int r = red1 - red2;
      int g = color1[1] - color2[1];
      int b = color1[2] - color2[2];
      return ((512 + redMean) * r * r >> 8) + 4 * g * g + ((767 - redMean) * b * b >> 8);
   }

   public static double slowPerceptualColorDistanceSquared(int[] color1, int[] color2) {
      double colorDistance = fastPerceptualColorDistanceSquared(color1, color2);
      double grey1 = (color1[0] + color1[1] + color1[2]) / 3;
      double grey2 = (color2[0] + color2[1] + color2[2]) / 3;
      double greyDistance1 = Math.abs(grey1 - color1[0]) + Math.abs(grey1 - color1[1]) + Math.abs(grey1 - color1[2]);
      double greyDistance2 = Math.abs(grey2 - color2[0]) + Math.abs(grey2 - color2[1]) + Math.abs(grey2 - color2[2]);
      double greyDistance = greyDistance1 - greyDistance2;
      return colorDistance + greyDistance * greyDistance / 10.0;
   }

   public static double slowPerceptualColorDistanceSquared(Color color1, Color color2) {
      int[] colorInts1 = new int[]{color1.getRed(), color1.getGreen(), color1.getBlue()};
      int[] colorInts2 = new int[]{color2.getRed(), color2.getGreen(), color2.getBlue()};
      return slowPerceptualColorDistanceSquared(colorInts1, colorInts2);
   }

   public static Color blendColors(Color color1, Color color2, float color1Ratio) {
      return new Color(blendColors(color1.getRGB(), color2.getRGB(), color1Ratio), true);
   }

   public static int blendColors(int color1, int color2, float color1Ratio) {
      float ratio1 = Mth.clamp(color1Ratio, 0.0F, 1.0F);
      float ratio2 = 1.0F - ratio1;
      int a1 = (color1 & 0xFF000000) >> 24;
      int r1 = (color1 & 0xFF0000) >> 16;
      int g1 = (color1 & 0xFF00) >> 8;
      int b1 = color1 & 0xFF;
      int a2 = (color2 & 0xFF000000) >> 24;
      int r2 = (color2 & 0xFF0000) >> 16;
      int g2 = (color2 & 0xFF00) >> 8;
      int b2 = color2 & 0xFF;
      int a = Mth.clamp(Math.round(a1 * ratio1 + a2 * ratio2), 0, 255);
      int r = Mth.clamp(Math.round(r1 * ratio1 + r2 * ratio2), 0, 255);
      int g = Mth.clamp(Math.round(g1 * ratio1 + g2 * ratio2), 0, 255);
      int b = Mth.clamp(Math.round(b1 * ratio1 + b2 * ratio2), 0, 255);
      return a << 24 | r << 16 | g << 8 | b;
   }

   public static Color overlayColor(Color base, Color overlay) {
      return new Color(overlayColor(base.getRGB(), overlay.getRGB()), true);
   }

   public static int overlayColor(int base, int overlay) {
      int alpha = (base & 0xFF000000) >> 24;
      int baseR = (base & 0xFF0000) >> 16;
      int baseG = (base & 0xFF00) >> 8;
      int baseB = base & 0xFF;
      int overlayR = (overlay & 0xFF0000) >> 16;
      int overlayG = (overlay & 0xFF00) >> 8;
      int overlayB = overlay & 0xFF;
      int r = Math.round(baseR * (overlayR / 255.0F)) & 0xFF;
      int g = Math.round(baseG * (overlayG / 255.0F)) & 0xFF;
      int b = Math.round(baseB * (overlayB / 255.0F)) & 0xFF;
      return alpha << 24 | r << 16 | g << 8 | b;
   }

   @OnlyIn(Dist.CLIENT)
   public static int getOverlayColor(ItemStack stack) {
      if (stack.isEmpty()) {
         return -1;
      } else if (stack.getItem() instanceof BlockItem) {
         Block b = Block.byItem(stack.getItem());
         if (b == Blocks.AIR) {
            return -1;
         } else {
            BlockState state = b.defaultBlockState();
            return Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
         }
      } else {
         return Minecraft.getInstance().getItemColors().getColor(stack, 0);
      }
   }
}
