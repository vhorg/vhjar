package iskallia.vault.entity.renderer.miner_zombie;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.miner_zombie.Tier2MinerZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.miner_zombie.Tier2MinerZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier2MinerZombieRenderer extends HumanoidMobRenderer<Tier2MinerZombieEntity, Tier2MinerZombieModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/miner_zombie/t2.png");

   public Tier2MinerZombieRenderer(Context context) {
      super(context, new Tier2MinerZombieModel(context.bakeLayer(ModModelLayers.T2_MINER_ZOMBIE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier2MinerZombieEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier2MinerZombieEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
