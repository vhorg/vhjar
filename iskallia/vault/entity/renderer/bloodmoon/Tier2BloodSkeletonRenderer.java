package iskallia.vault.entity.renderer.bloodmoon;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodmoon.Tier2BloodSkeletonEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodmoon.Tier2BloodSkeletonModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier2BloodSkeletonRenderer extends HumanoidMobRenderer<Tier2BloodSkeletonEntity, Tier2BloodSkeletonModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodmoon/skeleton/t2.png");

   public Tier2BloodSkeletonRenderer(Context context) {
      super(context, new Tier2BloodSkeletonModel(context.bakeLayer(ModModelLayers.T2_BLOOD_SKELETON)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier2BloodSkeletonEntity entity) {
      return TEXTURE_LOCATION;
   }
}
