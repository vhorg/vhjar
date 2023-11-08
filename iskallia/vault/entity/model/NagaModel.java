package iskallia.vault.entity.model;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.NagaEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class NagaModel extends AnimatedGeoModel<NagaEntity> {
   public ResourceLocation getModelLocation(NagaEntity entity) {
      return VaultMod.id("geo/champion.geo.json");
   }

   public ResourceLocation getTextureLocation(NagaEntity entity) {
      return VaultMod.id("textures/entity/champion.png");
   }

   public ResourceLocation getAnimationFileLocation(NagaEntity entity) {
      return VaultMod.id("animations/champion.animation.json");
   }
}
