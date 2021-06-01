package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;

public class MonsterEyeRenderer extends SlimeRenderer {
   public static final ResourceLocation TEXTURE = Vault.id("textures/entity/monster_eye.png");

   public MonsterEyeRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected void func_225620_a_(SlimeEntity entitylivingbase, MatrixStack matrixStack, float partialTickTime) {
      super.func_225620_a_(entitylivingbase, matrixStack, partialTickTime);
      matrixStack.func_227862_a_(2.0F, 2.0F, 2.0F);
   }

   public ResourceLocation func_110775_a(SlimeEntity entity) {
      return TEXTURE;
   }
}
