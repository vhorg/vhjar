package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.overlay.SpecialHealthOverlay;
import iskallia.vault.init.ModEffects;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Gui.class})
public abstract class MixinGui {
   @Redirect(
      method = {"renderEffects"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/effect/MobEffectInstance;getDuration()I",
         ordinal = 0
      )
   )
   public int getDurationRedirect(MobEffectInstance mobEffectInstance) {
      return ModEffects.PREVENT_DURATION_FLASH.contains(mobEffectInstance.getEffect()) ? 32767 : mobEffectInstance.getDuration();
   }

   @Inject(
      method = {"renderHearts"},
      at = {@At("TAIL")}
   )
   public void renderSpecialHearts(
      PoseStack poseStack,
      Player player,
      int left,
      int top,
      int rowHeight,
      int regen,
      float healthMax,
      int health,
      int healthLast,
      int absorb,
      boolean highlight,
      CallbackInfo ci
   ) {
      SpecialHealthOverlay.renderSpecialHearts(poseStack, player, left, top, rowHeight, regen, healthMax, health, healthLast, absorb, highlight);
   }

   @Redirect(
      method = {"renderPlayerHealth"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D",
         ordinal = 0
      )
   )
   public double getAllOfHealthPoints(Player instance, Attribute attribute) {
      return attribute != Attributes.MAX_HEALTH
         ? instance.getAttributeValue(attribute)
         : instance.getAttributeValue(Attributes.MAX_HEALTH) + SpecialHealthOverlay.getSpecialHealthPoints(instance);
   }
}
