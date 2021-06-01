package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;

public class BoogiemanRenderer extends ZombieRenderer {
   public static final ResourceLocation TEXTURE = Vault.id("textures/entity/boogieman.png");

   public BoogiemanRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
      this.field_177097_h.remove(this.field_177097_h.size() - 1);
   }

   protected void preRenderCallback(ZombieEntity entitylivingbase, MatrixStack matrixStack, float partialTickTime) {
      super.func_225620_a_(entitylivingbase, matrixStack, partialTickTime);
      matrixStack.func_227862_a_(2.0F, 2.0F, 2.0F);
   }

   public ResourceLocation func_110775_a(ZombieEntity entity) {
      return TEXTURE;
   }
}
