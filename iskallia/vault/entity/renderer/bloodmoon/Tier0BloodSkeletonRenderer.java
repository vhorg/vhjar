package iskallia.vault.entity.renderer.bloodmoon;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodmoon.Tier0BloodSkeletonEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodmoon.Tier0BloodSkeletonModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier0BloodSkeletonRenderer extends HumanoidMobRenderer<Tier0BloodSkeletonEntity, Tier0BloodSkeletonModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodmoon/skeleton/t0.png");

   public Tier0BloodSkeletonRenderer(Context context) {
      super(context, new Tier0BloodSkeletonModel(context.bakeLayer(ModModelLayers.T0_BLOOD_SKELETON)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier0BloodSkeletonEntity entity) {
      return TEXTURE_LOCATION;
   }
}
