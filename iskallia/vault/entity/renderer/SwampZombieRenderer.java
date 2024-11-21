package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.SwampZombieEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.SwampZombieModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class SwampZombieRenderer extends AbstractZombieRenderer<SwampZombieEntity, ZombieModel<SwampZombieEntity>> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/swamp_zombie.png");

   public SwampZombieRenderer(Context context) {
      super(
         context,
         new SwampZombieModel(context.bakeLayer(ModModelLayers.SWAMP_ZOMBIE)),
         new ZombieModel(context.bakeLayer(ModelLayers.HUSK_INNER_ARMOR)),
         new ZombieModel(context.bakeLayer(ModelLayers.HUSK_OUTER_ARMOR))
      );
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull SwampZombieEntity entity) {
      return TEXTURE_LOCATION;
   }

   protected void scale(@Nonnull SwampZombieEntity entity, @Nonnull PoseStack poseStack, float partialTickTime) {
      super.scale(entity, poseStack, partialTickTime);
      float scale = 1.3F;
      poseStack.scale(scale, scale, scale);
   }
}
