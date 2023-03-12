package iskallia.vault.entity.renderer.tier3;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier3.Tier3DrownedEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tier3.Tier3DrownedModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.DrownedModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3DrownedRenderer extends HumanoidMobRenderer<Tier3DrownedEntity, DrownedModel<Tier3DrownedEntity>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier3/drowned_outer_layer.png");

   public Tier3DrownedRenderer(Context context) {
      super(context, new Tier3DrownedModel(context.bakeLayer(ModModelLayers.T3_DROWNED)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3DrownedEntity entity) {
      return TEXTURE;
   }

   protected boolean isShaking(@Nonnull Tier3DrownedEntity entity) {
      return super.isShaking(entity) || entity.isUnderWaterConverting();
   }
}
