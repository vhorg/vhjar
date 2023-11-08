package iskallia.vault.entity.renderer.bloodmoon;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodmoon.Tier3BloodSkeletonEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodmoon.Tier3BloodSkeletonModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3BloodSkeletonRenderer extends HumanoidMobRenderer<Tier3BloodSkeletonEntity, Tier3BloodSkeletonModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodmoon/skeleton/t3.png");

   public Tier3BloodSkeletonRenderer(Context context) {
      super(context, new Tier3BloodSkeletonModel(context.bakeLayer(ModModelLayers.T3_BLOOD_SKELETON)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3BloodSkeletonEntity entity) {
      return TEXTURE_LOCATION;
   }
}
