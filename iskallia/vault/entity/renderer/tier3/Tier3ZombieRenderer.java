package iskallia.vault.entity.renderer.tier3;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier3.Tier3ZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tier3.Tier3ZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3ZombieRenderer extends HumanoidMobRenderer<Tier3ZombieEntity, Tier3ZombieModel> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier3/zombie.png");

   public Tier3ZombieRenderer(Context context) {
      super(context, new Tier3ZombieModel(context.bakeLayer(ModModelLayers.T3_ZOMBIE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3ZombieEntity entity) {
      return TEXTURE;
   }

   protected boolean isShaking(@Nonnull Tier3ZombieEntity entity) {
      return super.isShaking(entity) || entity.isUnderWaterConverting();
   }
}
