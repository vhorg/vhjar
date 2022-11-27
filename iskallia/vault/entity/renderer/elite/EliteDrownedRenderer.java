package iskallia.vault.entity.renderer.elite;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.elite.EliteDrownedEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.elite.EliteDrownedModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class EliteDrownedRenderer extends AbstractZombieRenderer<EliteDrownedEntity, DrownedModel<EliteDrownedEntity>> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/elite/drowned.png");

   public EliteDrownedRenderer(Context context) {
      super(
         context,
         new EliteDrownedModel(context.bakeLayer(ModModelLayers.ELITE_DROWNED)),
         new DrownedModel(context.bakeLayer(ModelLayers.DROWNED_INNER_ARMOR)),
         new DrownedModel(context.bakeLayer(ModelLayers.DROWNED_OUTER_ARMOR))
      );
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull EliteDrownedEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull EliteDrownedEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
      float scale = 1.4F;
      poseStack.scale(scale, scale, scale);
   }
}
