package iskallia.vault.client.util;

import iskallia.vault.VaultMod;
import iskallia.vault.client.util.color.ColorThief;
import iskallia.vault.client.util.color.ColorUtil;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class ColorizationHelper {
   private static final Random rand = new Random();
   private static final Map<Item, Optional<Color>> itemColors = new HashMap<>();

   private ColorizationHelper() {
   }

   @Nonnull
   public static Optional<Color> getColor(ItemStack stack) {
      if (stack.isEmpty()) {
         return Optional.empty();
      } else {
         Item i = stack.getItem();
         if (!itemColors.containsKey(i)) {
            TextureAtlasSprite tas = getParticleTexture(stack);
            if (tas != null) {
               itemColors.put(i, getDominantColor(tas));
            } else {
               itemColors.put(i, Optional.empty());
            }
         }

         return itemColors.get(i).map(c -> ColorUtil.overlayColor(c, new Color(ColorUtil.getOverlayColor(stack))));
      }
   }

   @Nullable
   private static TextureAtlasSprite getParticleTexture(ItemStack stack) {
      if (stack.isEmpty()) {
         return null;
      } else {
         ItemModelShaper imm = Minecraft.getInstance().getItemRenderer().getItemModelShaper();
         BakedModel mdl = imm.getItemModel(stack);
         return mdl.equals(imm.getModelManager().getMissingModel()) ? null : mdl.getParticleIcon(EmptyModelData.INSTANCE);
      }
   }

   private static Optional<Color> getDominantColor(TextureAtlasSprite tas) {
      if (tas == null) {
         return Optional.empty();
      } else {
         try {
            BufferedImage extractedImage = extractImage(tas);
            int[] dominantColor = ColorThief.getColor(extractedImage);
            int color = (dominantColor[0] & 0xFF) << 16 | (dominantColor[1] & 0xFF) << 8 | dominantColor[2] & 0xFF;
            return Optional.of(new Color(color));
         } catch (Exception var4) {
            VaultMod.LOGGER.error("Item Colorization Helper: Ignoring non-resolvable image " + tas.getName().toString());
            var4.printStackTrace();
            return Optional.empty();
         }
      }
   }

   @Nullable
   private static BufferedImage extractImage(TextureAtlasSprite tas) {
      int w = tas.getWidth();
      int h = tas.getHeight();
      int count = tas.getFrameCount();
      if (w > 0 && h > 0 && count > 0) {
         BufferedImage bufferedImage = new BufferedImage(w, h * count, 6);

         for (int i = 0; i < count; i++) {
            int[] pxArray = new int[tas.getWidth() * tas.getHeight()];

            for (int xx = 0; xx < tas.getWidth(); xx++) {
               for (int zz = 0; zz < tas.getHeight(); zz++) {
                  int argb = tas.getPixelRGBA(0, xx, zz + i * tas.getHeight());
                  pxArray[zz * tas.getWidth() + xx] = argb & -16711936 | (argb & 0xFF0000) >> 16 | (argb & 0xFF) << 16;
               }
            }

            bufferedImage.setRGB(0, i * h, w, h, pxArray, 0, w);
         }

         return bufferedImage;
      } else {
         return null;
      }
   }
}
