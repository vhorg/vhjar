package iskallia.vault.entity.renderer;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.CaveSpiderRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.CaveSpider;

public class VaultSpiderRenderer extends CaveSpiderRenderer {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/spider/spider.png");

   public VaultSpiderRenderer(Context context) {
      super(context);
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull CaveSpider entity) {
      return TEXTURE_LOCATION;
   }
}
