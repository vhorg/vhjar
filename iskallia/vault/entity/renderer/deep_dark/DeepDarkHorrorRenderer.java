package iskallia.vault.entity.renderer.deep_dark;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.deepdark.DeepDarkHorrorEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.deep_dark.DeepDarkHorrorModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class DeepDarkHorrorRenderer extends HumanoidMobRenderer<DeepDarkHorrorEntity, DeepDarkHorrorModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/deep_dark/horror.png");

   public DeepDarkHorrorRenderer(Context ctx) {
      super(ctx, new DeepDarkHorrorModel(ctx.bakeLayer(ModModelLayers.DEEP_DARK_HORROR)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull DeepDarkHorrorEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull DeepDarkHorrorEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
      float scale = 1.6F;
      poseStack.scale(scale, scale, scale);
   }
}
