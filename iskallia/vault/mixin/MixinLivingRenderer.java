package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.init.ModAttributes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntityRenderer.class})
public class MixinLivingRenderer {
   @Inject(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
         ordinal = 0
      )}
   )
   public void render(
      LivingEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci
   ) {
      AttributeInstance attribute = entity.getAttribute(ModAttributes.SIZE_SCALE);
      if (attribute != null) {
         float scale = (float)attribute.getValue();
         matrixStack.scale(scale, scale, scale);
      }
   }
}
