package iskallia.vault.mixin;

import iskallia.vault.VaultMod;
import iskallia.vault.item.tool.MergingAtlasSprite;
import java.util.regex.Pattern;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite.Info;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TextureAtlas.class})
public abstract class MixinTextureAtlas {
   @Inject(
      method = {"load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;IIIII)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void load(ResourceManager manager, Info info, int storageX, int storageY, int mipLevel, int x, int y, CallbackInfoReturnable<TextureAtlasSprite> ci) {
      ResourceLocation id = new ResourceLocation(info.name().getNamespace(), String.format("textures/%s%s", info.name().getPath(), ".png"));
      if (id.getNamespace().equals("the_vault") && id.getPath().startsWith("textures/item/tool/") && id.getPath().contains("head")) {
         String[] data = id.getPath().replace("textures/item/tool/", "").split(Pattern.quote("/"));
         ResourceLocation tool = VaultMod.id("textures/item/tool/" + data[0] + "/head.png");
         ResourceLocation material = VaultMod.id("textures/item/tool/material/" + data[2]);
         ci.setReturnValue(MergingAtlasSprite.create((TextureAtlas)this, info, mipLevel, storageX, storageY, x, y, material, tool));
      }
   }

   @Inject(
      method = {"getResourceLocation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getBasicSpriteInfos(ResourceLocation raw, CallbackInfoReturnable<ResourceLocation> ci) {
      if (raw.getNamespace().equals("the_vault") && raw.getPath().startsWith("item/tool/") && raw.getPath().contains("head")) {
         String material = raw.getPath().substring(raw.getPath().lastIndexOf("/") + 1);
         ci.setReturnValue(VaultMod.id("textures/item/tool/material/" + material + ".png"));
      }
   }
}
