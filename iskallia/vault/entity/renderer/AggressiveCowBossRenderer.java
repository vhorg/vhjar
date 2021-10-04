package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.passive.CowEntity;

public class AggressiveCowBossRenderer extends CowRenderer {
   public AggressiveCowBossRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected void preRenderCallback(CowEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
      super.func_225620_a_(entitylivingbaseIn, matrixStackIn, partialTickTime);
      matrixStackIn.func_227862_a_(3.0F, 3.0F, 3.0F);
   }
}
