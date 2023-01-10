package iskallia.vault.entity.renderer.tier2;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier2.Tier2DrownedEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier2DrownedRenderer extends AbstractZombieRenderer<Tier2DrownedEntity, DrownedModel<Tier2DrownedEntity>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier2/drowned.png");

   public Tier2DrownedRenderer(Context context) {
      super(
         context,
         new DrownedModel(context.bakeLayer(ModelLayers.DROWNED)),
         new DrownedModel(context.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)),
         new DrownedModel(context.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR))
      );
      this.addLayer(new Tier2DrownedOuterLayer(this, context.getModelSet()));
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier2DrownedEntity entity) {
      return TEXTURE;
   }
}
