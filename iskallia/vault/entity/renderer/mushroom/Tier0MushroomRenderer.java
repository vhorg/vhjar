package iskallia.vault.entity.renderer.mushroom;

import iskallia.vault.entity.entity.mushroom.Tier0MushroomEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.mushroom.Tier0MushroomModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class Tier0MushroomRenderer extends MushroomRenderer<Tier0MushroomEntity> {
   public Tier0MushroomRenderer(Context ctx) {
      super(ctx, () -> new Tier0MushroomModel(ctx.bakeLayer(ModModelLayers.T0_MUSHROOM)));
   }
}
