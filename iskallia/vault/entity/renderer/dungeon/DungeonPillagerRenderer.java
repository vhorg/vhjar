package iskallia.vault.entity.renderer.dungeon;

import iskallia.vault.VaultMod;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Pillager;

public class DungeonPillagerRenderer extends PillagerRenderer {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/dungeon/pillager.png");

   public DungeonPillagerRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull Pillager entity) {
      return TEXTURE_LOCATION;
   }
}
