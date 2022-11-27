package iskallia.vault.entity.renderer.tier1;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier1.Tier1DrownedEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier1DrownedRenderer extends AbstractZombieRenderer<Tier1DrownedEntity, DrownedModel<Tier1DrownedEntity>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier1/drowned.png");

   public Tier1DrownedRenderer(Context context) {
      super(
         context,
         new DrownedModel(context.bakeLayer(ModelLayers.DROWNED)),
         new DrownedModel(context.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)),
         new DrownedModel(context.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR))
      );
      this.addLayer(new Tier1DrownedOuterLayer(this, context.getModelSet()));
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier1DrownedEntity entity) {
      return TEXTURE;
   }
}
