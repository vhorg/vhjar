package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.render.IVaultOptions;
import iskallia.vault.core.event.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public abstract class MixinGameRenderer {
   @Final
   @Shadow
   private Minecraft minecraft;

   @Inject(
      method = {"bobHurt"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hurtByDurationNoBobDamage(PoseStack pMatrixStack, float pPartialTicks, CallbackInfo ci) {
      if (!((IVaultOptions)this.minecraft.options).doVanillaPotionDamageEffects()) {
         LivingEntity livingEntity = (LivingEntity)((GameRenderer)this).getMinecraft().getCameraEntity();
         if (livingEntity != null
            && !livingEntity.isDeadOrDying()
            && livingEntity == this.minecraft.player
            && !this.isNotCorrectDamageSourceOrDoesntHaveEffect(livingEntity)) {
            ci.cancel();
         }
      }
   }

   private boolean isNotCorrectDamageSourceOrDoesntHaveEffect(LivingEntity livingEntity) {
      return !this.isNoBobDamageSource(livingEntity.getLastDamageSource())
         && (!this.isApplicablePotionDamageSource(livingEntity.getLastDamageSource()) || !this.hasApplicableEffect(livingEntity));
   }

   private boolean isNoBobDamageSource(DamageSource damageSource) {
      return damageSource == DamageSource.ON_FIRE;
   }

   private boolean isApplicablePotionDamageSource(DamageSource damageSource) {
      return damageSource == DamageSource.MAGIC || damageSource == DamageSource.WITHER;
   }

   private boolean hasApplicableEffect(LivingEntity livingEntity) {
      for (MobEffectInstance activeEffect : livingEntity.getActiveEffects()) {
         if (activeEffect.getEffect().getCategory() == MobEffectCategory.HARMFUL && activeEffect.duration > 0) {
            return true;
         }
      }

      return false;
   }

   @Inject(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraftforge/client/ForgeHooksClient;dispatchRenderLast(Lnet/minecraft/client/renderer/LevelRenderer;Lcom/mojang/blaze3d/vertex/PoseStack;FLcom/mojang/math/Matrix4f;J)V"
      )}
   )
   private void doRenderLevelLastEvent(float pTicks, long nanoTime, PoseStack poseStack, CallbackInfo ci) {
      ClientEvents.RENDER_LEVEL_LAST.invoke(pTicks, nanoTime, poseStack);
   }
}
