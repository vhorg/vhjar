package iskallia.vault.entity.renderer;

import iskallia.vault.entity.boss.ArtifactBossEntity;
import iskallia.vault.entity.model.ArtifactBossModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ArtifactBossRenderer extends GeoEntityRenderer<ArtifactBossEntity> {
   public ArtifactBossRenderer(Context context) {
      super(context, new ArtifactBossModel());
   }
}
