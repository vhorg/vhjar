package iskallia.vault.entity.renderer.cave;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.cave.CaveSkeletonEntity;
import iskallia.vault.entity.model.cave.CaveSkeletonModel;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class CaveSkeletonRenderer extends HumanoidMobRenderer<CaveSkeletonEntity, CaveSkeletonModel> {
   public static final Map<Integer, ResourceLocation> TEXTURES = Map.of(
      0,
      VaultMod.id("textures/entity/cave/cave_skeleton_t0.png"),
      1,
      VaultMod.id("textures/entity/cave/cave_skeleton_t1.png"),
      2,
      VaultMod.id("textures/entity/cave/cave_skeleton_t2.png"),
      3,
      VaultMod.id("textures/entity/cave/cave_skeleton_t3.png"),
      4,
      VaultMod.id("textures/entity/cave/cave_skeleton_t4.png"),
      5,
      VaultMod.id("textures/entity/cave/cave_skeleton_t5.png")
   );

   public CaveSkeletonRenderer(Context context, ModelLayerLocation modelLayerLocation) {
      super(context, new CaveSkeletonModel(context.bakeLayer(modelLayerLocation)), 0.25F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull CaveSkeletonEntity skeleton) {
      return TEXTURES.get(skeleton.getTier());
   }
}
