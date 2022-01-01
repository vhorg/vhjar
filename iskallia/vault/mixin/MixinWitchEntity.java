package iskallia.vault.mixin;

import iskallia.vault.easteregg.Witchskall;
import iskallia.vault.init.ModSounds;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({WitchEntity.class})
public abstract class MixinWitchEntity {
   @Inject(
      method = {"registerData"},
      at = {@At("TAIL")}
   )
   protected void registerData(CallbackInfo ci) {
      WitchEntity thiz = (WitchEntity)this;
      if (Witchskall.WITCHSKALL_TICKS == null) {
         Witchskall.WITCHSKALL_TICKS = EntityDataManager.func_187226_a(WitchEntity.class, DataSerializers.field_187192_b);
      }

      thiz.func_184212_Q().func_187214_a(Witchskall.WITCHSKALL_TICKS, -1);
      if (Witchskall.IS_WITCHSKALL == null) {
         Witchskall.IS_WITCHSKALL = EntityDataManager.func_187226_a(WitchEntity.class, DataSerializers.field_187198_h);
      }

      thiz.func_184212_Q().func_187214_a(Witchskall.IS_WITCHSKALL, false);
   }

   @Inject(
      method = {"getAmbientSound"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void getAmbientSound(CallbackInfoReturnable<SoundEvent> ci) {
      WitchEntity thiz = (WitchEntity)this;
      if (Witchskall.isWitchskall(thiz)) {
         ci.setReturnValue(ModSounds.WITCHSKALL_IDLE);
      }
   }
}
