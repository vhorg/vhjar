package iskallia.vault.entity.renderer.skeleton_pirate;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.skeleton_pirate.Tier2SkeletonPirateEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.skeleton_pirate.Tier2SkeletonPirateModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier2SkeletonPirateRenderer extends HumanoidMobRenderer<Tier2SkeletonPirateEntity, Tier2SkeletonPirateModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/skeleton_pirate/t2.png");

   public Tier2SkeletonPirateRenderer(Context context) {
      super(context, new Tier2SkeletonPirateModel(context.bakeLayer(ModModelLayers.T2_SKELETON_PIRATE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier2SkeletonPirateEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier2SkeletonPirateEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
