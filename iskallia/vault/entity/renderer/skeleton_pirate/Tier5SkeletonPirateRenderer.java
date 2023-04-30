package iskallia.vault.entity.renderer.skeleton_pirate;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.skeleton_pirate.Tier5SkeletonPirateEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.skeleton_pirate.Tier5SkeletonPirateModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier5SkeletonPirateRenderer extends HumanoidMobRenderer<Tier5SkeletonPirateEntity, Tier5SkeletonPirateModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/skeleton_pirate/t5.png");

   public Tier5SkeletonPirateRenderer(Context context) {
      super(context, new Tier5SkeletonPirateModel(context.bakeLayer(ModModelLayers.T5_SKELETON_PIRATE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier5SkeletonPirateEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier5SkeletonPirateEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
