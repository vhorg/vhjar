package iskallia.vault.entity.model;

import iskallia.vault.entity.boss.GolemHandProjectileEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.provider.GeoModelProvider;

public class GolemHandProjectileModel extends GeoModelProvider<GolemHandProjectileEntity> {
   private static final ResourceLocation modelResourceLeft = new ResourceLocation("the_vault", "geo/golem_projectile_right.geo.json");
   private static final ResourceLocation modelResourceRight = new ResourceLocation("the_vault", "geo/golem_projectile_left.geo.json");
   private static final ResourceLocation textureResource = new ResourceLocation("the_vault", "textures/entity/boss/golem_boss.png");

   public ResourceLocation getModelLocation(GolemHandProjectileEntity golemHand) {
      return golemHand.isRightHand() ? modelResourceRight : modelResourceLeft;
   }

   public ResourceLocation getTextureLocation(GolemHandProjectileEntity object) {
      return textureResource;
   }
}
