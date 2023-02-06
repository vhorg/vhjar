package iskallia.vault.mixin;

import iskallia.vault.client.render.IVaultOptions;
import net.minecraft.client.Options;
import net.minecraft.client.Options.FieldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Options.class})
public class MixinOptions implements IVaultOptions {
   public boolean doVanillaPotionDamageEffects = false;

   @Inject(
      method = {"processOptions"},
      at = {@At("HEAD")}
   )
   private void processVaultOptions(FieldAccess pAccessor, CallbackInfo ci) {
      this.doVanillaPotionDamageEffects = pAccessor.process("doVanillaPotionDamageEffects", this.doVanillaPotionDamageEffects);
   }

   @Override
   public boolean doVanillaPotionDamageEffects() {
      return this.doVanillaPotionDamageEffects;
   }

   @Override
   public void setVanillaPotionDamageEffects(boolean vanillaPotionDamageEffects) {
      this.doVanillaPotionDamageEffects = vanillaPotionDamageEffects;
   }
}
