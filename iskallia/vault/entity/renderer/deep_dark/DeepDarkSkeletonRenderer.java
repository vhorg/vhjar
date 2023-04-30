package iskallia.vault.entity.renderer.deep_dark;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.deepdark.DeepDarkSkeletonEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.deep_dark.DeepDarkSkeletonModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class DeepDarkSkeletonRenderer extends HumanoidMobRenderer<DeepDarkSkeletonEntity, DeepDarkSkeletonModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/deep_dark/skeleton.png");

   public DeepDarkSkeletonRenderer(Context ctx) {
      super(ctx, new DeepDarkSkeletonModel(ctx.bakeLayer(ModModelLayers.DEEP_DARK_SKELETON)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull DeepDarkSkeletonEntity entity) {
      return TEXTURE_LOCATION;
   }
}
