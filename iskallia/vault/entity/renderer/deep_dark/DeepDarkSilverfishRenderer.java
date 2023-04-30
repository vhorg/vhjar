package iskallia.vault.entity.renderer.deep_dark;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.deepdark.DeepDarkSilverfishEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.deep_dark.DeepDarkSilverfishModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class DeepDarkSilverfishRenderer extends MobRenderer<DeepDarkSilverfishEntity, DeepDarkSilverfishModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/deep_dark/silverfish.png");

   public DeepDarkSilverfishRenderer(Context ctx) {
      super(ctx, new DeepDarkSilverfishModel(ctx.bakeLayer(ModModelLayers.DEEP_DARK_SILVERFISH)), 0.3F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull DeepDarkSilverfishEntity entity) {
      return TEXTURE_LOCATION;
   }
}
