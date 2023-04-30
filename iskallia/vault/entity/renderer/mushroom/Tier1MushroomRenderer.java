package iskallia.vault.entity.renderer.mushroom;

import iskallia.vault.entity.entity.mushroom.Tier1MushroomEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.mushroom.Tier1MushroomModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;

public class Tier1MushroomRenderer extends MushroomRenderer<Tier1MushroomEntity> {
   public Tier1MushroomRenderer(Context ctx) {
      super(ctx, () -> new Tier1MushroomModel(ctx.bakeLayer(ModModelLayers.T1_MUSHROOM)));
   }
}
