package iskallia.vault.entity.renderer.skeleton_pirate;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.skeleton_pirate.Tier3SkeletonPirateEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.skeleton_pirate.Tier3SkeletonPirateModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3SkeletonPirateRenderer extends HumanoidMobRenderer<Tier3SkeletonPirateEntity, Tier3SkeletonPirateModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/skeleton_pirate/t3.png");

   public Tier3SkeletonPirateRenderer(Context context) {
      super(context, new Tier3SkeletonPirateModel(context.bakeLayer(ModModelLayers.T3_SKELETON_PIRATE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3SkeletonPirateEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier3SkeletonPirateEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
