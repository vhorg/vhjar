package iskallia.vault.entity.renderer.mushroom;

import iskallia.vault.entity.entity.mushroom.Tier4MushroomEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.mushroom.Tier4MushroomModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class Tier4MushroomRenderer extends MushroomRenderer<Tier4MushroomEntity> {
   public Tier4MushroomRenderer(Context ctx) {
      super(ctx, () -> new Tier4MushroomModel(ctx.bakeLayer(ModModelLayers.T4_MUSHROOM)));
   }
}
