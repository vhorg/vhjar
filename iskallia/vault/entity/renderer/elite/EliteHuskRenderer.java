package iskallia.vault.entity.renderer.elite;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.elite.EliteHuskEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.elite.EliteHuskModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class EliteHuskRenderer extends AbstractZombieRenderer<EliteHuskEntity, ZombieModel<EliteHuskEntity>> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/elite/husk.png");

   public EliteHuskRenderer(Context context) {
      super(
         context,
         new EliteHuskModel(context.bakeLayer(ModModelLayers.ELITE_HUSK)),
         new ZombieModel(context.bakeLayer(ModelLayers.HUSK_INNER_ARMOR)),
         new ZombieModel(context.bakeLayer(ModelLayers.HUSK_OUTER_ARMOR))
      );
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Zombie entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull EliteHuskEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
      float scale = 1.4F;
      poseStack.scale(scale, scale, scale);
   }
}
