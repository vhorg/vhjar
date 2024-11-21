package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RangedAttackGoal.class})
public class MixinRangedAttackGoal {
   @Mutable
   @Shadow
   @Final
   private float attackRadiusSqr;

   @Inject(
      method = {"<init>(Lnet/minecraft/world/entity/monster/RangedAttackMob;DIIF)V"},
      at = {@At("RETURN")}
   )
   public void init(RangedAttackMob rangedAttackMob, double speedModifier, int attackIntervalMin, int attackIntervalMax, float attackRadius, CallbackInfo ci) {
      if (rangedAttackMob instanceof Entity entity && ServerVaults.get(entity.level).isPresent()) {
         this.attackRadiusSqr = 2304.0F;
      }
   }
}
