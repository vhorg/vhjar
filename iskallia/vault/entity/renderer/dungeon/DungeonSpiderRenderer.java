package iskallia.vault.entity.renderer.dungeon;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.renderer.VaultSpiderRenderer;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.CaveSpider;

public class DungeonSpiderRenderer extends VaultSpiderRenderer {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/dungeon/gray_spider.png");

   public DungeonSpiderRenderer(Context context) {
      super(context);
   }

   @Nonnull
   @Override
   public ResourceLocation getTextureLocation(@Nonnull CaveSpider entity) {
      return TEXTURE_LOCATION;
   }
}
