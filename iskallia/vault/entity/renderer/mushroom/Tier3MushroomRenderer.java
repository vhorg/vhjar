package iskallia.vault.entity.renderer.mushroom;

import iskallia.vault.entity.entity.mushroom.Tier3MushroomEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.mushroom.Tier3MushroomModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class Tier3MushroomRenderer extends MushroomRenderer<Tier3MushroomEntity> {
   public Tier3MushroomRenderer(Context ctx) {
      super(ctx, () -> new Tier3MushroomModel(ctx.bakeLayer(ModModelLayers.T3_MUSHROOM)));
   }
}
