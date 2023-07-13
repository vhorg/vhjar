package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.screen.bestiary.BestiaryScreen;
import iskallia.vault.init.ModAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.content.mobs.client.model.WraithModel;

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

   @Redirect(
      method = {"render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/EntityModel;setupAnim(Lnet/minecraft/world/entity/Entity;FFFFF)V"
      )
   )
   public <T extends Entity> void noAnimInBestiary(
      EntityModel<T> instance, T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch
   ) {
      if (Minecraft.getInstance().screen instanceof BestiaryScreen && instance instanceof WraithModel wraithModel) {
         this.setupWraithAnim(wraithModel);
      } else {
         instance.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      }
   }

   public void setupWraithAnim(WraithModel wraithModel) {
      try {
         ReflectionUtil.setFieldValue(wraithModel.getClass().getDeclaredField("alphaMult"), wraithModel, 0.75F);
         ReflectionUtil.setFieldValue(wraithModel.getClass().getDeclaredField("offset"), wraithModel, 0.1F);
      } catch (NoSuchFieldException var3) {
      }
   }
}
