package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.entity.EtchingVendorEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.util.ResourceLocation;

public class EtchingVendorRenderer extends MobRenderer<EtchingVendorEntity, VillagerModel<EtchingVendorEntity>> {
   private static final ResourceLocation TEXTURES = Vault.id("textures/entity/etching_trader.png");

   public EtchingVendorRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new VillagerModel(0.0F), 0.5F);
      this.func_177094_a(new HeadLayer(this));
      this.func_177094_a(new CrossedArmsItemLayer(this));
   }

   public ResourceLocation getEntityTexture(EtchingVendorEntity entity) {
      return TEXTURES;
   }

   protected void preRenderCallback(EtchingVendorEntity entity, MatrixStack matrixStack, float pTicks) {
      this.field_76989_e = 0.5F;
      float size = 0.9375F;
      matrixStack.func_227862_a_(size, size, size);
   }
}
