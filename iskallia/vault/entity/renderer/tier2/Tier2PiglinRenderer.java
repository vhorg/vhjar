package iskallia.vault.entity.renderer.tier2;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

public class Tier2PiglinRenderer extends PiglinRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier2/piglin.png");

   public Tier2PiglinRenderer(Context context) {
      super(context, ModelLayers.PIGLIN, ModelLayers.PIGLIN_INNER_ARMOR, ModelLayers.PIGLIN_OUTER_ARMOR, false);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Mob pEntity) {
      return TEXTURE;
   }
}
