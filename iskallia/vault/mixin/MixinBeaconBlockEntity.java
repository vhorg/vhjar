package iskallia.vault.mixin;

import iskallia.vault.init.ModEffects;
import java.util.Set;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BeaconBlockEntity.class})
public class MixinBeaconBlockEntity {
   @Shadow
   @Final
   public static MobEffect[][] BEACON_EFFECTS;
   @Shadow
   @Final
   private static Set<MobEffect> VALID_EFFECTS;

   @Inject(
      method = {"<clinit>"},
      at = {@At("RETURN")}
   )
   private static void clinit(CallbackInfo ci) {
      MobEffect[] thirdRow = BEACON_EFFECTS[2];
      thirdRow = new MobEffect[]{thirdRow[0], ModEffects.REACH};
      BEACON_EFFECTS[2] = thirdRow;
      VALID_EFFECTS.add(ModEffects.REACH);
   }
}
