package iskallia.vault.entity.renderer.bloodmoon;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.bloodmoon.Tier5BloodSkeletonEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.bloodmoon.Tier5BloodSkeletonModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier5BloodSkeletonRenderer extends HumanoidMobRenderer<Tier5BloodSkeletonEntity, Tier5BloodSkeletonModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/bloodmoon/skeleton/t5.png");

   public Tier5BloodSkeletonRenderer(Context context) {
      super(context, new Tier5BloodSkeletonModel(context.bakeLayer(ModModelLayers.T5_BLOOD_SKELETON)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier5BloodSkeletonEntity entity) {
      return TEXTURE_LOCATION;
   }
}
