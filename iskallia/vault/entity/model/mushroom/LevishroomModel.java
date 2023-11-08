package iskallia.vault.entity.model.mushroom;

import iskallia.vault.entity.entity.mushroom.LevishroomEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LevishroomModel extends AnimatedGeoModel<LevishroomEntity> {
   private static final ResourceLocation modelResource = new ResourceLocation("the_vault", "geo/levishroom.geo.json");
   private static final ResourceLocation textureResource = new ResourceLocation("the_vault", "textures/entity/mushroom/levishroom.png");
   private static final ResourceLocation animationResource = new ResourceLocation("the_vault", "animations/levishroom.animation.json");

   public ResourceLocation getModelLocation(LevishroomEntity object) {
      return modelResource;
   }

   public ResourceLocation getTextureLocation(LevishroomEntity object) {
      return textureResource;
   }

   public ResourceLocation getAnimationFileLocation(LevishroomEntity animatable) {
      return animationResource;
   }
}
