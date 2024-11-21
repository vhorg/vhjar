package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RangedCrossbowAttackGoal.class})
public class MixinRangedCrossbowAttackGoal<T extends Monster & RangedAttackMob & CrossbowAttackMob> {
   @Mutable
   @Shadow
   @Final
   private float attackRadiusSqr;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   public void init(T mob, double pSpeedModifier, float pAttackRadius, CallbackInfo ci) {
      if (ServerVaults.get(mob.level).isPresent()) {
         this.attackRadiusSqr = 2304.0F;
      }
   }
}
