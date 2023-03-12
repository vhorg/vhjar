package iskallia.vault.entity.renderer.tier3;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier3.Tier3StrayEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tier3.Tier3StrayModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3StrayRenderer extends HumanoidMobRenderer<Tier3StrayEntity, Tier3StrayModel> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier3/stray.png");

   public Tier3StrayRenderer(Context context) {
      super(context, new Tier3StrayModel(context.bakeLayer(ModModelLayers.T3_STRAY)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3StrayEntity entity) {
      return TEXTURE;
   }
}
