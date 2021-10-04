package iskallia.vault.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.init.ModAttributes;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingRenderer.class})
public class MixinLivingRenderer {
   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/matrix/MatrixStack;push()V",
         ordinal = 0
      )}
   )
   public void render(
      LivingEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, CallbackInfo ci
   ) {
      ModifiableAttributeInstance attribute = entity.func_110148_a(ModAttributes.SIZE_SCALE);
      if (attribute != null) {
         float scale = (float)attribute.func_111126_e();
         matrixStack.func_227862_a_(scale, scale, scale);
      }
   }
}
