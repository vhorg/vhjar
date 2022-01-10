package iskallia.vault.client.util;

import iskallia.vault.Vault;
import iskallia.vault.client.util.color.ColorThief;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.util.MiscUtils;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
      if (stack.func_190926_b()) {
         return Optional.empty();
      } else {
         Optional<Color> override = getCustomColorOverride(stack);
         if (override.isPresent()) {
            return override;
         } else {
            Item i = stack.func_77973_b();
            if (!itemColors.containsKey(i)) {
               TextureAtlasSprite tas = getParticleTexture(stack);
               if (tas != null) {
                  itemColors.put(i, getDominantColor(tas));
               } else {
                  itemColors.put(i, Optional.empty());
               }
            }

            return itemColors.get(i).map(c -> MiscUtils.overlayColor(c, new Color(MiscUtils.getOverlayColor(stack))));
         }
      }
   }

   public static Optional<Color> getCustomColorOverride(ItemStack stack) {
      Item i = stack.func_77973_b();
      if (i == ModItems.VAULT_PLATINUM) {
         return Optional.of(new Color(16705664));
      } else if (i == ModItems.BANISHED_SOUL) {
         return Optional.of(new Color(9972223));
      } else {
         return i instanceof VaultGear ? Optional.of(Color.getHSBColor(rand.nextFloat(), 1.0F, 1.0F)) : Optional.empty();
      }
   }

   @Nullable
   private static TextureAtlasSprite getParticleTexture(ItemStack stack) {
      if (stack.func_190926_b()) {
         return null;
      } else {
         ItemModelMesher imm = Minecraft.func_71410_x().func_175599_af().func_175037_a();
         IBakedModel mdl = imm.func_178089_a(stack);
         return mdl.equals(imm.func_178083_a().func_174951_a()) ? null : mdl.getParticleTexture(EmptyModelData.INSTANCE);
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
            Vault.LOGGER.error("Item Colorization Helper: Ignoring non-resolvable image " + tas.func_195668_m().toString());
            var4.printStackTrace();
            return Optional.empty();
         }
      }
   }

   @Nullable
   private static BufferedImage extractImage(TextureAtlasSprite tas) {
      int w = tas.func_94211_a();
      int h = tas.func_94216_b();
      int count = tas.func_110970_k();
      if (w > 0 && h > 0 && count > 0) {
         BufferedImage bufferedImage = new BufferedImage(w, h * count, 6);

         for (int i = 0; i < count; i++) {
            int[] pxArray = new int[tas.func_94211_a() * tas.func_94216_b()];

            for (int xx = 0; xx < tas.func_94211_a(); xx++) {
               for (int zz = 0; zz < tas.func_94216_b(); zz++) {
                  int argb = tas.getPixelRGBA(0, xx, zz + i * tas.func_94216_b());
                  pxArray[zz * tas.func_94211_a() + xx] = argb & -16711936 | (argb & 0xFF0000) >> 16 | (argb & 0xFF) << 16;
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
