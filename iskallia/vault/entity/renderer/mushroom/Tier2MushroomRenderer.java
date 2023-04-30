package iskallia.vault.entity.renderer.mushroom;

import iskallia.vault.entity.entity.mushroom.Tier2MushroomEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.mushroom.Tier2MushroomModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class Tier2MushroomRenderer extends MushroomRenderer<Tier2MushroomEntity> {
   public Tier2MushroomRenderer(Context ctx) {
      super(ctx, () -> new Tier2MushroomModel(ctx.bakeLayer(ModModelLayers.T2_MUSHROOM)));
   }
}
