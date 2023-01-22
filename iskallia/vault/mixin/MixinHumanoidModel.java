package iskallia.vault.mixin;

import iskallia.vault.entity.renderer.EternalSpiritRenderer;
import iskallia.vault.entity.renderer.SpiritRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HumanoidModel.class})
public abstract class MixinHumanoidModel {
   @Shadow
   @Final
   public ModelPart leftArm;

   @Inject(
      method = {"setupAnim"},
      at = {@At("TAIL")}
   )
   private void overrideArmAnimation(
      LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callbackInfo
   ) {
      if (entity instanceof Player player) {
         SpiritRenderer.handleStaticHandHoldingSpirit(player, this.leftArm);
         EternalSpiritRenderer.handleStaticHandHoldingSpirit(player, this.leftArm);
      }
   }
}
