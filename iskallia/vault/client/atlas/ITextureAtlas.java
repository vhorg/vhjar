package iskallia.vault.client.atlas;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface ITextureAtlas extends PreparableReloadListener {
   ResourceLocation getAtlasResourceLocation();

   TextureAtlasSprite getSprite(ResourceLocation var1);
}
