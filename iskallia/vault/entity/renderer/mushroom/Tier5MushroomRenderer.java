package iskallia.vault.entity.renderer.mushroom;

import iskallia.vault.entity.entity.mushroom.Tier5MushroomEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.mushroom.Tier5MushroomModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class Tier5MushroomRenderer extends MushroomRenderer<Tier5MushroomEntity> {
   public Tier5MushroomRenderer(Context ctx) {
      super(ctx, () -> new Tier5MushroomModel(ctx.bakeLayer(ModModelLayers.T5_MUSHROOM)));
   }
}
