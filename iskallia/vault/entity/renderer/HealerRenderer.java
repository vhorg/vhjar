package iskallia.vault.entity.renderer;

import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.HealerEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class HealerRenderer extends IllagerRenderer<HealerEntity> {
   private static final ResourceLocation TEXTURE_LOCATION = VaultMod.id("textures/entity/healer.png");

   public HealerRenderer(Context context) {
      super(context, new IllagerModel(context.bakeLayer(ModelLayers.ILLUSIONER)), 0.5F);
      this.addLayer(new CustomHeadLayer(this, context.getModelSet()));
      this.addLayer(new ItemInHandLayer(this));
      ((IllagerModel)this.model).getHat().visible = true;
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull HealerEntity entity) {
      return TEXTURE_LOCATION;
   }
}
