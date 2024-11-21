package iskallia.vault.entity.renderer.plastic;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.plastic.PlasticSkeletonEntity;
import iskallia.vault.entity.model.ModModelLayers;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;

public class PlasticSkeletonRenderer extends SkeletonRenderer {
   public static final Map<Integer, ResourceLocation> TEXTURES = Map.of(
      1,
      VaultMod.id("textures/entity/plastic/skeleton/skeleton_plastic_t1.png"),
      2,
      VaultMod.id("textures/entity/plastic/skeleton/skeleton_plastic_t2.png"),
      3,
      VaultMod.id("textures/entity/plastic/skeleton/skeleton_plastic_t3.png")
   );

   public PlasticSkeletonRenderer(Context context) {
      super(context, ModModelLayers.PLASTIC_SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull AbstractSkeleton skeleton) {
      return skeleton instanceof PlasticSkeletonEntity plasticSkeleton ? TEXTURES.get(plasticSkeleton.getTier()) : TEXTURES.get(1);
   }
}
