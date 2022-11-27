package iskallia.vault.entity.renderer.tier1;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class Tier1SkeletonRenderer extends SkeletonRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier1/skeleton.png");

   public Tier1SkeletonRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull AbstractSkeleton entity) {
      return TEXTURE;
   }
}
