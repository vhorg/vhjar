package iskallia.vault.entity.renderer.tier2;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier2.Tier2HuskEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tier2.Tier2HuskModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class Tier2HuskRenderer extends AbstractZombieRenderer<Tier2HuskEntity, ZombieModel<Tier2HuskEntity>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier2/husk.png");

   public Tier2HuskRenderer(Context context) {
      super(
         context,
         new Tier2HuskModel(context.bakeLayer(ModModelLayers.T2_HUSK)),
         new ZombieModel(context.bakeLayer(ModelLayers.HUSK_INNER_ARMOR)),
         new ZombieModel(context.bakeLayer(ModelLayers.HUSK_OUTER_ARMOR))
      );
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Zombie entity) {
      return TEXTURE;
   }
}
