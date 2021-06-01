package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.util.ResourceLocation;

public class BlueBlazeRenderer extends BlazeRenderer {
   public static final ResourceLocation TEXTURE = Vault.id("textures/entity/blue_blaze.png");

   public BlueBlazeRenderer(EntityRendererManager renderManager) {
      super(renderManager);
   }

   protected void preRenderCallback(BlazeEntity entitylivingbase, MatrixStack matrixStack, float partialTickTime) {
      super.func_225620_a_(entitylivingbase, matrixStack, partialTickTime);
      matrixStack.func_227862_a_(2.0F, 2.0F, 2.0F);
   }

   public ResourceLocation func_110775_a(BlazeEntity entity) {
      return TEXTURE;
   }
}
