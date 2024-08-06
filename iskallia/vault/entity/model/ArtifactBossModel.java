package iskallia.vault.entity.model;

import iskallia.vault.entity.boss.ArtifactBossEntity;
import iskallia.vault.entity.boss.stage.IBossStage;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ArtifactBossModel extends AnimatedGeoModel<ArtifactBossEntity> {
   private static final ResourceLocation modelResource = new ResourceLocation("the_vault", "geo/artifact_boss.geo.json");
   private static final ResourceLocation textureResource = new ResourceLocation("the_vault", "textures/entity/boss/artifact_boss.png");
   private static final ResourceLocation animationResource = new ResourceLocation("the_vault", "animations/artifact_boss.animation.json");

   public ResourceLocation getModelLocation(ArtifactBossEntity object) {
      return modelResource;
   }

   public ResourceLocation getTextureLocation(ArtifactBossEntity object) {
      return object.getCurrentStage().flatMap(IBossStage::getTextureLocation).orElse(textureResource);
   }

   public ResourceLocation getAnimationFileLocation(ArtifactBossEntity object) {
      return animationResource;
   }
}
