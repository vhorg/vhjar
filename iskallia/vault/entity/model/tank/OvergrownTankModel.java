package iskallia.vault.entity.model.tank;

import iskallia.vault.entity.entity.tank.OvergrownTankEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class OvergrownTankModel extends AnimatedGeoModel<OvergrownTankEntity> {
   private static final ResourceLocation modelResource = new ResourceLocation("the_vault", "geo/overgrown_tank.geo.json");
   private static final ResourceLocation textureResource = new ResourceLocation("the_vault", "textures/entity/tank/overgrown.png");
   private static final ResourceLocation animationResource = new ResourceLocation("the_vault", "animations/overgrown_tank.animation.json");

   public ResourceLocation getModelLocation(OvergrownTankEntity object) {
      return modelResource;
   }

   public ResourceLocation getTextureLocation(OvergrownTankEntity object) {
      return textureResource;
   }

   public ResourceLocation getAnimationFileLocation(OvergrownTankEntity animatable) {
      return animationResource;
   }
}
