package iskallia.vault.entity.renderer.tier1;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class Tier1WitherSkeletonRenderer extends WitherSkeletonRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier1/wither_skeleton.png");

   public Tier1WitherSkeletonRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull AbstractSkeleton entity) {
      return TEXTURE;
   }
}
