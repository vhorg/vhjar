package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;

public class RobotRenderer extends IronGolemRenderer {
   public static final ResourceLocation TEXTURE = Vault.id("textures/entity/robot.png");

   public RobotRenderer(EntityRendererManager renderManager) {
      super(renderManager);
   }

   protected void preRenderCallback(IronGolemEntity entitylivingbase, MatrixStack matrixStack, float partialTickTime) {
      super.func_225620_a_(entitylivingbase, matrixStack, partialTickTime);
      matrixStack.func_227862_a_(2.0F, 2.0F, 2.0F);
   }

   public ResourceLocation func_110775_a(IronGolemEntity entity) {
      return TEXTURE;
   }
}
