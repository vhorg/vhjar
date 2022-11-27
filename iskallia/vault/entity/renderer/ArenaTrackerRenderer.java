package iskallia.vault.entity.renderer;

import iskallia.vault.entity.entity.ArenaTrackerEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class ArenaTrackerRenderer extends EntityRenderer<ArenaTrackerEntity> {
   public ArenaTrackerRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(ArenaTrackerEntity entity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
