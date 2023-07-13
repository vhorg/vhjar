package iskallia.vault.entity.renderer.dungeon;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class DungeonSkeletonRenderer extends SkeletonRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/dungeon/skeleton.png");

   public DungeonSkeletonRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull AbstractSkeleton entity) {
      return TEXTURE;
   }
}
