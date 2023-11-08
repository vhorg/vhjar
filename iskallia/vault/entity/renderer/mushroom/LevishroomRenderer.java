package iskallia.vault.entity.renderer.mushroom;

import iskallia.vault.entity.entity.mushroom.LevishroomEntity;
import iskallia.vault.entity.model.mushroom.LevishroomModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class LevishroomRenderer extends GeoProjectilesRenderer<LevishroomEntity> {
   public LevishroomRenderer(Context renderManager) {
      super(renderManager, new LevishroomModel());
   }
}
