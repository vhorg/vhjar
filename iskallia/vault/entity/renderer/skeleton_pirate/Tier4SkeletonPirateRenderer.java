package iskallia.vault.entity.renderer.skeleton_pirate;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.skeleton_pirate.Tier4SkeletonPirateEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.skeleton_pirate.Tier4SkeletonPirateModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier4SkeletonPirateRenderer extends HumanoidMobRenderer<Tier4SkeletonPirateEntity, Tier4SkeletonPirateModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/skeleton_pirate/t4.png");

   public Tier4SkeletonPirateRenderer(Context context) {
      super(context, new Tier4SkeletonPirateModel(context.bakeLayer(ModModelLayers.T4_SKELETON_PIRATE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier4SkeletonPirateEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier4SkeletonPirateEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
