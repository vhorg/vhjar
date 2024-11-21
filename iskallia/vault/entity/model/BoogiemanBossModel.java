package iskallia.vault.entity.model;

import iskallia.vault.entity.boss.VaultBossEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BoogiemanBossModel extends AnimatedGeoModel<VaultBossEntity> {
   private static final ResourceLocation modelResource = new ResourceLocation("the_vault", "geo/boogieman_boss.geo.json");
   private static final ResourceLocation textureResource = new ResourceLocation("the_vault", "textures/entity/boss/boogieman_boss.png");
   private static final ResourceLocation animationResource = new ResourceLocation("the_vault", "animations/boogieman_boss.animation.json");

   public ResourceLocation getModelLocation(VaultBossEntity object) {
      return modelResource;
   }

   public ResourceLocation getTextureLocation(VaultBossEntity object) {
      return textureResource;
   }

   public ResourceLocation getAnimationFileLocation(VaultBossEntity object) {
      return animationResource;
   }
}
