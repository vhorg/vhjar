package iskallia.vault.entity.renderer.dungeon;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vindicator;

public class DungeonVindicatorRenderer extends VindicatorRenderer {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/dungeon/vindicator.png");

   public DungeonVindicatorRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Vindicator entity) {
      return TEXTURE_LOCATION;
   }
}
