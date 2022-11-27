package iskallia.vault.entity.renderer.elite;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.elite.EliteWitherSkeletonModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.WitherSkeleton;

public class EliteWitherSkeletonRenderer extends HumanoidMobRenderer<WitherSkeleton, EliteWitherSkeletonModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/elite/wither_skeleton.png");

   public EliteWitherSkeletonRenderer(Context context) {
      super(context, new EliteWitherSkeletonModel(context.bakeLayer(ModModelLayers.ELITE_WITHER_SKELETON)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull WitherSkeleton entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull WitherSkeleton entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
      float scale = 1.4F;
      poseStack.scale(scale, scale, scale);
   }
}
