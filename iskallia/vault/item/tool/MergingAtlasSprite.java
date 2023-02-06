package iskallia.vault.item.tool;

import com.mojang.blaze3d.platform.NativeImage;
import iskallia.vault.VaultMod;
import iskallia.vault.mixin.AccessorInfo;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite.Info;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   modid = "the_vault",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class MergingAtlasSprite extends TextureAtlasSprite {
   public MergingAtlasSprite(TextureAtlas atlas, Info info, int mipLevel, int storageX, int storageZ, int x, int y, NativeImage image) {
      super(atlas, info, mipLevel, storageX, storageZ, x, y, image);
   }

   public static MergingAtlasSprite create(
      TextureAtlas atlas, Info info, int mipLevel, int storageX, int storageZ, int x, int y, ResourceLocation material, ResourceLocation baseId
   ) {
      AnimationMetadataSection meta = ((AccessorInfo)info).getMetaData();
      int frameWidth = meta.getFrameWidth(info.width());
      int frameHeight = meta.getFrameHeight(info.height());

      NativeImage image;
      NativeImage baseImage;
      try {
         image = NativeImage.read(Minecraft.getInstance().getResourceManager().getResource(material).getInputStream());
         baseImage = NativeImage.read(Minecraft.getInstance().getResourceManager().getResource(baseId).getInputStream());
      } catch (IOException var24) {
         var24.printStackTrace();
         throw new RuntimeException(var24);
      }

      for (int offsetY = 0; offsetY < image.getHeight(); offsetY += frameHeight) {
         for (int frameX = 0; frameX < frameWidth; frameX++) {
            for (int frameY = 0; frameY < frameHeight; frameY++) {
               int pixel1 = image.getPixelRGBA(frameX, frameY + offsetY);
               int pixel2 = baseImage.getPixelRGBA(frameX, frameY);
               int red = (pixel1 & 0xFF) * (pixel2 & 0xFF) / 255;
               int green = (pixel1 >>> 8 & 0xFF) * (pixel2 >>> 8 & 0xFF) / 255;
               int blue = (pixel1 >>> 16 & 0xFF) * (pixel2 >>> 16 & 0xFF) / 255;
               int alpha = (pixel1 >> 24 & 0xFF) * (pixel2 >> 24 & 0xFF) / 255;
               int color = alpha << 24 | blue << 16 | green << 8 | red;
               image.setPixelRGBA(frameX, frameY + offsetY, color);
            }
         }
      }

      return new MergingAtlasSprite(atlas, info, mipLevel, storageX, storageZ, x, y, image);
   }

   @SubscribeEvent
   public static void onStitchPre(Pre event) {
      for (ToolMaterial material : ToolMaterial.values()) {
         event.addSprite(VaultMod.id("item/tool/material/" + material.getId()));
      }
   }
}
