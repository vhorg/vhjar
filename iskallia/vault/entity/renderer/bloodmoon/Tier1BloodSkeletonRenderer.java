package iskallia.vault.entity.renderer.bloodmoon;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodmoon.Tier1BloodSkeletonEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodmoon.Tier1BloodSkeletonModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier1BloodSkeletonRenderer extends HumanoidMobRenderer<Tier1BloodSkeletonEntity, Tier1BloodSkeletonModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodmoon/skeleton/t1.png");

   public Tier1BloodSkeletonRenderer(Context context) {
      super(context, new Tier1BloodSkeletonModel(context.bakeLayer(ModModelLayers.T1_BLOOD_SKELETON)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier1BloodSkeletonEntity entity) {
      return TEXTURE_LOCATION;
   }
}
