package iskallia.vault.entity.model;

import iskallia.vault.entity.boss.GolemBossEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class GolemBossModel extends AnimatedGeoModel<GolemBossEntity> {
   private static final ResourceLocation modelResource = new ResourceLocation("the_vault", "geo/golem_boss.geo.json");
   private static final ResourceLocation textureResource = new ResourceLocation("the_vault", "textures/entity/boss/golem_boss.png");
   private static final ResourceLocation animationResource = new ResourceLocation("the_vault", "animations/golem_boss.animation.json");

   public ResourceLocation getModelLocation(GolemBossEntity object) {
      return modelResource;
   }

   public ResourceLocation getTextureLocation(GolemBossEntity object) {
      return textureResource;
   }

   public ResourceLocation getAnimationFileLocation(GolemBossEntity object) {
      return animationResource;
   }
}
