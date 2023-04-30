package iskallia.vault.entity.renderer.deep_dark;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.deepdark.DeepDarkPiglinEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.deep_dark.DeepDarkPiglinModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class DeepDarkPiglinRenderer extends HumanoidMobRenderer<DeepDarkPiglinEntity, DeepDarkPiglinModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/deep_dark/piglin.png");

   public DeepDarkPiglinRenderer(Context ctx) {
      super(ctx, new DeepDarkPiglinModel(ctx.bakeLayer(ModModelLayers.DEEP_DARK_PIGLIN)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull DeepDarkPiglinEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected boolean isShaking(@Nonnull DeepDarkPiglinEntity entity) {
      return super.isShaking(entity) || entity.isConverting();
   }
}
