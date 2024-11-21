package iskallia.vault.entity.renderer.elite;

import iskallia.vault.entity.entity.elite.ScarabEntity;
import iskallia.vault.entity.model.elite.ScarabModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ScarabRenderer extends GeoEntityRenderer<ScarabEntity> {
   public ScarabRenderer(Context renderManager) {
      super(renderManager, new ScarabModel());
   }
}
