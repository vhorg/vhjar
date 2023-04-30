package iskallia.vault.entity.renderer.mushroom;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.mushroom.DeathcapEntity;
import iskallia.vault.entity.model.ModModelLayers;
import iskallia.vault.entity.model.mushroom.DeathcapModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class DeathcapRenderer extends MobRenderer<DeathcapEntity, DeathcapModel> {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/mushroom/deathcap.png");

   public DeathcapRenderer(Context ctx) {
      super(ctx, new DeathcapModel(ctx.bakeLayer(ModModelLayers.DEATHCAP)), 0.5F);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(DeathcapEntity entity) {
      return TEXTURE;
   }
}
