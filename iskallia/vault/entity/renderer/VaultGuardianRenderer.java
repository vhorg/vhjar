package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

public class VaultGuardianRenderer extends PiglinRenderer {
   public static final ResourceLocation TEXTURE = Vault.id("textures/entity/vault_guardian.png");

   public VaultGuardianRenderer(EntityRendererManager renderManager) {
      super(renderManager, false);
   }

   protected void preRenderCallback(MobEntity entity, MatrixStack matrixStack, float partialTickTime) {
      super.func_225620_a_(entity, matrixStack, partialTickTime);
      matrixStack.func_227862_a_(1.5F, 1.5F, 1.5F);
   }

   public ResourceLocation func_110775_a(MobEntity entity) {
      return TEXTURE;
   }
}
