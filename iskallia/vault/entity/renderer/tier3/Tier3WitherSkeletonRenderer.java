package iskallia.vault.entity.renderer.tier3;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier3.Tier3WitherSkeletonEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tier3.Tier3WitherSkeletonModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3WitherSkeletonRenderer extends HumanoidMobRenderer<Tier3WitherSkeletonEntity, Tier3WitherSkeletonModel> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier3/wither_skeleton.png");

   public Tier3WitherSkeletonRenderer(Context context) {
      super(context, new Tier3WitherSkeletonModel(context.bakeLayer(ModModelLayers.T3_WITHER_SKELETON)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3WitherSkeletonEntity entity) {
      return TEXTURE;
   }
}
