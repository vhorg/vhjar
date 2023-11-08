package iskallia.vault.entity.renderer.bloodmoon;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodmoon.Tier4BloodSkeletonEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodmoon.Tier4BloodSkeletonModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier4BloodSkeletonRenderer extends HumanoidMobRenderer<Tier4BloodSkeletonEntity, Tier4BloodSkeletonModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodmoon/skeleton/t4.png");

   public Tier4BloodSkeletonRenderer(Context context) {
      super(context, new Tier4BloodSkeletonModel(context.bakeLayer(ModModelLayers.T4_BLOOD_SKELETON)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier4BloodSkeletonEntity entity) {
      return TEXTURE_LOCATION;
   }
}
