package iskallia.vault.entity.renderer.tier1;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;

public class Tier1ZombieRenderer extends ZombieRenderer {
   public static final ResourceLocation TEXTURE = VaultMod.id("textures/entity/tier1/zombie.png");

   public Tier1ZombieRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Zombie entity) {
      return TEXTURE;
   }
}
