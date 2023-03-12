package iskallia.vault.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ItemPickupParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemPickupParticle.class})
public abstract class MixinItemPickupParticle {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void cancelParticle(VertexConsumer vertexConsumer, Camera camera, float partialTicks, CallbackInfo ci) {
      ci.cancel();
   }
}
