package iskallia.vault.mixin;

import iskallia.vault.easteregg.Witchskall;
import iskallia.vault.init.ModSounds;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.monster.Witch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Witch.class})
public abstract class MixinWitchEntity {
   @Inject(
      method = {"defineSynchedData"},
      at = {@At("TAIL")}
   )
   protected void registerData(CallbackInfo ci) {
      Witch thiz = (Witch)this;
      if (Witchskall.WITCHSKALL_TICKS == null) {
         Witchskall.WITCHSKALL_TICKS = SynchedEntityData.defineId(Witch.class, EntityDataSerializers.INT);
      }

      thiz.getEntityData().define(Witchskall.WITCHSKALL_TICKS, -1);
      if (Witchskall.IS_WITCHSKALL == null) {
         Witchskall.IS_WITCHSKALL = SynchedEntityData.defineId(Witch.class, EntityDataSerializers.BOOLEAN);
      }

      thiz.getEntityData().define(Witchskall.IS_WITCHSKALL, false);
   }

   @Inject(
      method = {"getAmbientSound"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void getAmbientSound(CallbackInfoReturnable<SoundEvent> ci) {
      Witch thiz = (Witch)this;
      if (Witchskall.isWitchskall(thiz)) {
         ci.setReturnValue(ModSounds.WITCHSKALL_IDLE);
      }
   }
}
