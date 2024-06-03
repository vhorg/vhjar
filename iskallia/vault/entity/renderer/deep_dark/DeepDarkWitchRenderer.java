package iskallia.vault.entity.renderer.deep_dark;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.deepdark.DeepDarkWitchEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.deep_dark.DeepDarkWitchModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.WitchModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class DeepDarkWitchRenderer extends MobRenderer<DeepDarkWitchEntity, WitchModel<DeepDarkWitchEntity>> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/deep_dark/witch.png");

   public DeepDarkWitchRenderer(Context ctx) {
      super(ctx, new DeepDarkWitchModel(ctx.bakeLayer(ModModelLayers.DEEP_DARK_WITCH)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull DeepDarkWitchEntity entity) {
      return TEXTURE_LOCATION;
   }
}
