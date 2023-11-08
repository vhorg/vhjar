package iskallia.vault.entity.renderer.bloodmoon;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodmoon.BloodSilverfishEntity;
import iskallia.vault.entity.model.ModModelLayers;
import javax.annotation.Nonnull;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class BloodSilverfishRenderer extends MobRenderer<BloodSilverfishEntity, SilverfishModel<BloodSilverfishEntity>> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodmoon/silverfish.png");

   public BloodSilverfishRenderer(Context ctx) {
      super(ctx, new SilverfishModel(ctx.bakeLayer(ModModelLayers.BLOOD_SILVERFISH)), 0.3F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull BloodSilverfishEntity entity) {
      return TEXTURE_LOCATION;
   }
}
