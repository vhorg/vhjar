package iskallia.vault.entity.renderer.tier1;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public class Tier1CreeperRenderer extends CreeperRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier1/creeper.png");

   public Tier1CreeperRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Creeper entity) {
      return TEXTURE;
   }
}
