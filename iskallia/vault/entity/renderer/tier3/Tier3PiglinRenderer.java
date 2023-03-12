package iskallia.vault.entity.renderer.tier3;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.tier3.Tier3PiglinEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.tier3.Tier3PiglinModel;
import javax.annotation.Nonnull;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class Tier3PiglinRenderer extends HumanoidMobRenderer<Tier3PiglinEntity, PiglinModel<Tier3PiglinEntity>> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier3/piglin.png");

   public Tier3PiglinRenderer(Context ctx) {
      super(ctx, new Tier3PiglinModel(ctx.bakeLayer(ModModelLayers.T3_PIGLIN)), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Tier3PiglinEntity pEntity) {
      return TEXTURE;
   }

   protected boolean isShaking(@Nonnull Tier3PiglinEntity entity) {
      return super.isShaking(entity) || entity.isConverting();
   }
}
