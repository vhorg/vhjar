package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.EtchingVendorEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

public class EtchingVendorRenderer extends MobRenderer<EtchingVendorEntity, VillagerModel<EtchingVendorEntity>> {
   private static final ResourceLocation TEXTURES = VaultMod.id("textures/entity/etching_trader.png");

   public EtchingVendorRenderer(Context context) {
      super(context, new VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5F);
      this.addLayer(new CustomHeadLayer(this, context.getModelSet()));
      this.addLayer(new CrossedArmsItemLayer(this));
   }

   @Nonnull
   public ResourceLocation getTextureLocation(@Nonnull EtchingVendorEntity entity) {
      return TEXTURES;
   }

   protected void scale(@Nonnull EtchingVendorEntity entity, PoseStack matrixStack, float pTicks) {
      this.shadowRadius = 0.5F;
      float size = 0.9375F;
      matrixStack.scale(size, size, size);
   }
}
