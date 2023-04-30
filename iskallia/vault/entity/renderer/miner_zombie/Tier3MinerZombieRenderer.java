package iskallia.vault.entity.renderer.miner_zombie;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.miner_zombie.Tier3MinerZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.miner_zombie.Tier3MinerZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3MinerZombieRenderer extends HumanoidMobRenderer<Tier3MinerZombieEntity, Tier3MinerZombieModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/miner_zombie/t3.png");

   public Tier3MinerZombieRenderer(Context context) {
      super(context, new Tier3MinerZombieModel(context.bakeLayer(ModModelLayers.T3_MINER_ZOMBIE)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3MinerZombieEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull Tier3MinerZombieEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
   }
}
