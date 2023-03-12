package iskallia.vault.entity.renderer.tier3;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier3.Tier3SkeletonEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tier3.Tier3SkeletonModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3SkeletonRenderer extends HumanoidMobRenderer<Tier3SkeletonEntity, Tier3SkeletonModel> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier3/skeleton.png");

   public Tier3SkeletonRenderer(Context context) {
      super(context, new Tier3SkeletonModel(context.bakeLayer(ModModelLayers.T3_SKELETON)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3SkeletonEntity entity) {
      return TEXTURE;
   }
}
