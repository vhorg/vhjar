package iskallia.vault.entity.renderer.elite;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.elite.EliteZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.elite.EliteZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class EliteZombieRenderer extends AbstractZombieRenderer<EliteZombieEntity, ZombieModel<EliteZombieEntity>> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/elite/zombie.png");

   public EliteZombieRenderer(Context context) {
      super(
         context,
         new EliteZombieModel(context.bakeLayer(ModModelLayers.ELITE_ZOMBIE)),
         new ZombieModel(context.bakeLayer(ModelLayers.HUSK_INNER_ARMOR)),
         new ZombieModel(context.bakeLayer(ModelLayers.HUSK_OUTER_ARMOR))
      );
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull EliteZombieEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull EliteZombieEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
      float scale = 1.4F;
      poseStack.scale(scale, scale, scale);
   }
}
