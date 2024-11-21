package iskallia.vault.entity.renderer.plastic;

import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.plastic.PlasticSkeletonEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.plastic.AdjustableSkeletonModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class PlasticSkeletonTier4Renderer extends HumanoidMobRenderer<PlasticSkeletonEntity, AdjustableSkeletonModel> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/plastic/skeleton/skeleton_plastic_t4.png");

   public PlasticSkeletonTier4Renderer(Context context) {
      super(
         context,
         new AdjustableSkeletonModel(context.bakeLayer(ModModelLayers.PLASTIC_SKELETON_TIER_4))
            .setHeadPos(new Vector3f(0.0F, -6.0F, 0.0F))
            .setHatPos(new Vector3f(0.0F, -6.0F, 0.0F))
            .setBodyPos(new Vector3f(0.0F, -6.0F, 0.0F))
            .setRightArmPos(new Vector3f(-7.0F, -4.0F, 0.0F))
            .setLeftArmPos(new Vector3f(7.0F, -4.0F, 0.0F))
            .setRightLegPos(new Vector3f(0.0F, 9.0F, 0.1F))
            .setLeftLegPos(new Vector3f(0.0F, 9.0F, 0.1F)),
         0.5F
      );
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull PlasticSkeletonEntity skeleton) {
      return TEXTURE;
   }
}
