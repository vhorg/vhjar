package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.entity.EyestalkEntity;
import iskallia.vault.entity.model.EyestalkModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class EyestalkRenderer extends MobRenderer<EyestalkEntity, EyestalkModel> {
   public static final ResourceLocation DEFAULT_TEXTURE = Vault.id("textures/entity/eyesore/eyestalk.png");

   public EyestalkRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new EyestalkModel(), 0.2F);
   }

   protected void preRenderCallback(EyestalkEntity entitylivingbaseIn, MatrixStack matrixStack, float partialTickTime) {
      super.func_225620_a_(entitylivingbaseIn, matrixStack, partialTickTime);
      float scale = 4.0F;
      matrixStack.func_227862_a_(scale, scale, scale);
      matrixStack.func_227861_a_(0.0, 0.7F, 0.0);
   }

   protected void renderName(EyestalkEntity entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
   }

   protected boolean canRenderName(EyestalkEntity entity) {
      return false;
   }

   public ResourceLocation getEntityTexture(EyestalkEntity entity) {
      return DEFAULT_TEXTURE;
   }
}
