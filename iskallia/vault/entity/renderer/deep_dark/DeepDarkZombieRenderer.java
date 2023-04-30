package iskallia.vault.entity.renderer.deep_dark;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.deepdark.DeepDarkZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.deep_dark.DeepDarkZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class DeepDarkZombieRenderer extends HumanoidMobRenderer<DeepDarkZombieEntity, DeepDarkZombieModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/deep_dark/zombie.png");

   public DeepDarkZombieRenderer(Context ctx) {
      super(ctx, new DeepDarkZombieModel(ctx.bakeLayer(ModModelLayers.DEEP_DARK_ZOMBIE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull DeepDarkZombieEntity entity) {
      return TEXTURE_LOCATION;
   }
}
