package iskallia.vault.entity.renderer.dungeon;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.renderer.VaultSpiderRenderer;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.CaveSpider;

public class DungeonBlackWidowSpiderRenderer extends VaultSpiderRenderer {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/dungeon/spider.png");

   public DungeonBlackWidowSpiderRenderer(Context context) {
      super(context);
   }

   @Nonnull
   @Override
   public ResourceLocation getTextureLocation(@Nonnull CaveSpider entity) {
      return TEXTURE_LOCATION;
   }
}
