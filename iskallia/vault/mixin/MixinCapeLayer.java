package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.gear.trinket.TrinketHelper;
import iskallia.vault.gear.trinket.effects.WingsTrinket;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CapeLayer.class})
public abstract class MixinCapeLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
   public MixinCapeLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> pRenderer) {
      super(pRenderer);
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void render(
      PoseStack pMatrixStack,
      MultiBufferSource pBuffer,
      int pPackedLight,
      AbstractClientPlayer pLivingEntity,
      float pLimbSwing,
      float pLimbSwingAmount,
      float pPartialTicks,
      float pAgeInTicks,
      float pNetHeadYaw,
      float pHeadPitch,
      CallbackInfo ci
   ) {
      TrinketHelper.getTrinkets(pLivingEntity, WingsTrinket.class).forEach(wings -> {
         if (wings.isUsable(pLivingEntity)) {
            ci.cancel();
         }
      });
   }
}
