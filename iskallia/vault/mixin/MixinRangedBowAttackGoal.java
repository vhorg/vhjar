package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RangedBowAttackGoal.class})
public class MixinRangedBowAttackGoal {
   @Mutable
   @Shadow
   @Final
   private float attackRadiusSqr;

   @Inject(
      method = {"<init>(Lnet/minecraft/world/entity/Mob;DIF)V"},
      at = {@At("RETURN")}
   )
   public void init(Mob mob, double speedModifier, int attackIntervalMin, float attackRadius, CallbackInfo ci) {
      if (ServerVaults.get(mob.level).isPresent()) {
         this.attackRadiusSqr = 2304.0F;
      }
   }
}
