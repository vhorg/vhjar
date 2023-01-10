package iskallia.vault.entity.renderer.tier2;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class Tier2StrayRenderer extends SkeletonRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier2/stray.png");

   public Tier2StrayRenderer(Context context) {
      super(context, ModelLayers.STRAY, ModelLayers.STRAY_INNER_ARMOR, ModelLayers.STRAY_OUTER_ARMOR);
      this.addLayer(new Tier2StrayClothingLayer(this, context.getModelSet()));
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull AbstractSkeleton entity) {
      return TEXTURE;
   }
}
