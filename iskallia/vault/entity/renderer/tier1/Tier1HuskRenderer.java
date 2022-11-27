package iskallia.vault.entity.renderer.tier1;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class Tier1HuskRenderer extends HuskRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier1/husk.png");

   public Tier1HuskRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Zombie entity) {
      return TEXTURE;
   }
}
