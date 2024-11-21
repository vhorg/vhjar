package iskallia.vault.entity.renderer.elite;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class RaisedZombieRenderer extends AbstractZombieRenderer<Zombie, ZombieModel<Zombie>> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/elite/zombie.png");

   public RaisedZombieRenderer(Context context) {
      super(
         context,
         new ZombieModel(context.bakeLayer(ModelLayers.ZOMBIE)),
         new ZombieModel(context.bakeLayer(ModelLayers.HUSK_INNER_ARMOR)),
         new ZombieModel(context.bakeLayer(ModelLayers.HUSK_OUTER_ARMOR))
      );
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Zombie entity) {
      return TEXTURE_LOCATION;
   }
}
