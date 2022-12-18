package iskallia.vault.entity.renderer;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.WinterWolfEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.WinterWolfModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class WinterWolfRenderer extends MobRenderer<WinterWolfEntity, WinterWolfModel> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/winter_wolf.png");

   public WinterWolfRenderer(Context context) {
      super(context, new WinterWolfModel(context.bakeLayer(ModModelLayers.WINTER_WOLF)), 0.5F);
      this.addLayer(new WinterWolfHeldItemLayer(this));
   }

   protected float getBob(WinterWolfEntity pLivingBase, float pPartialTicks) {
      return pLivingBase.getTailAngle();
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull WinterWolfEntity entity) {
      return TEXTURE_LOCATION;
   }
}
