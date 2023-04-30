package iskallia.vault.entity.renderer.miner_zombie;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.miner_zombie.Tier4MinerZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.miner_zombie.Tier4MinerZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier4MinerZombieRenderer extends HumanoidMobRenderer<Tier4MinerZombieEntity, Tier4MinerZombieModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/miner_zombie/t4.png");

   public Tier4MinerZombieRenderer(Context context) {
      super(context, new Tier4MinerZombieModel(context.bakeLayer(ModModelLayers.T4_MINER_ZOMBIE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier4MinerZombieEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier4MinerZombieEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
