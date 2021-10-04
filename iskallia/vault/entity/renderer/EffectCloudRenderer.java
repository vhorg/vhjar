package iskallia.vault.entity.renderer;

import iskallia.vault.entity.EffectCloudEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;

public class EffectCloudRenderer extends EntityRenderer<EffectCloudEntity> {
   public EffectCloudRenderer(EntityRendererManager manager) {
      super(manager);
   }

   public ResourceLocation getEntityTexture(EffectCloudEntity entity) {
      return AtlasTexture.field_110575_b;
   }
}
