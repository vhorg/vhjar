package iskallia.vault.entity.renderer.tier3;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier3.Tier3HuskEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tier3.Tier3HuskModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3HuskRenderer extends HumanoidMobRenderer<Tier3HuskEntity, ZombieModel<Tier3HuskEntity>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier3/husk.png");

   public Tier3HuskRenderer(Context context) {
      super(context, new Tier3HuskModel(context.bakeLayer(ModModelLayers.T3_HUSK)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3HuskEntity entity) {
      return TEXTURE;
   }

   protected boolean isShaking(@Nonnull Tier3HuskEntity entity) {
      return super.isShaking(entity) || entity.isUnderWaterConverting();
   }
}
