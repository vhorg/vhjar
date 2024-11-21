package iskallia.vault.entity.renderer.tank;

import iskallia.vault.entity.entity.tank.OvergrownTankEntity;
import iskallia.vault.entity.model.tank.OvergrownTankModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class OvergrownTankRenderer extends GeoEntityRenderer<OvergrownTankEntity> {
   public OvergrownTankRenderer(Context renderManager) {
      super(renderManager, new OvergrownTankModel());
   }
}
