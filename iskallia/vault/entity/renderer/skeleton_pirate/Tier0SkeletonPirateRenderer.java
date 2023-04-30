package iskallia.vault.entity.renderer.skeleton_pirate;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.skeleton_pirate.Tier0SkeletonPirateEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.skeleton_pirate.Tier0SkeletonPirateModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier0SkeletonPirateRenderer extends HumanoidMobRenderer<Tier0SkeletonPirateEntity, Tier0SkeletonPirateModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/skeleton_pirate/t0.png");

   public Tier0SkeletonPirateRenderer(Context context) {
      super(context, new Tier0SkeletonPirateModel(context.bakeLayer(ModModelLayers.T0_SKELETON_PIRATE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier0SkeletonPirateEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier0SkeletonPirateEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
