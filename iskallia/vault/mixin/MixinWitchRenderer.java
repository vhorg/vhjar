package iskallia.vault.mixin;

import iskallia.vault.Vault;
import iskallia.vault.easteregg.Witchskall;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({WitchRenderer.class})
public class MixinWitchRenderer {
   private static final ResourceLocation WITCHSKALL_TEXTURE = Vault.id("textures/entity/witchskall.png");

   @Inject(
      method = {"getEntityTexture"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getEntityTexture(WitchEntity entity, CallbackInfoReturnable<ResourceLocation> ci) {
      if (Witchskall.isWitchskall(entity)) {
         ci.setReturnValue(WITCHSKALL_TEXTURE);
      }
   }
}
