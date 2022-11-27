package iskallia.vault.entity.renderer;

import iskallia.vault.entity.entity.EffectCloudEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class EffectCloudRenderer extends EntityRenderer<EffectCloudEntity> {
   public EffectCloudRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(EffectCloudEntity entity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
