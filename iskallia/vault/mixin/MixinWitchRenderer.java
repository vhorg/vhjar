package iskallia.vault.mixin;

import iskallia.vault.VaultMod;
import iskallia.vault.easteregg.Witchskall;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Witch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({WitchRenderer.class})
public abstract class MixinWitchRenderer {
   private static final ResourceLocation WITCHSKALL_TEXTURE = VaultMod.id("textures/entity/witchskall.png");

   @Inject(
      method = {"getTextureLocation(Lnet/minecraft/world/entity/monster/Witch;)Lnet/minecraft/resources/ResourceLocation;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getEntityTexture(Witch entity, CallbackInfoReturnable<ResourceLocation> ci) {
      if (Witchskall.isWitchskall(entity)) {
         ci.setReturnValue(WITCHSKALL_TEXTURE);
      }
   }
}
