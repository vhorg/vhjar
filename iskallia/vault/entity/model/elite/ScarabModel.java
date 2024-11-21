package iskallia.vault.entity.model.elite;

import iskallia.vault.entity.entity.elite.ScarabEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ScarabModel extends AnimatedGeoModel<ScarabEntity> {
   private static final ResourceLocation modelResource = new ResourceLocation("the_vault", "geo/scarab.geo.json");
   private static final ResourceLocation textureResource = new ResourceLocation("the_vault", "textures/entity/elite/scarab.png");
   private static final ResourceLocation animationResource = new ResourceLocation("the_vault", "animations/scarab.animation.json");

   public ResourceLocation getModelLocation(ScarabEntity object) {
      return modelResource;
   }

   public ResourceLocation getTextureLocation(ScarabEntity object) {
      return textureResource;
   }

   public ResourceLocation getAnimationFileLocation(ScarabEntity animatable) {
      return animationResource;
   }
}
