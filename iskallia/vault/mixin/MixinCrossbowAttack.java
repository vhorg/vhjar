package iskallia.vault.mixin;

import iskallia.vault.world.data.ServerVaults;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.CrossbowAttack;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.item.CrossbowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({CrossbowAttack.class})
public abstract class MixinCrossbowAttack<E extends Mob & CrossbowAttackMob, T extends LivingEntity> {
   @Shadow
   private int attackDelay;

   @Shadow
   private static LivingEntity getAttackTarget(LivingEntity p_22785_) {
      throw new UnsupportedOperationException();
   }

   @Inject(
      method = {"checkExtraStartConditions(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void checkExtraStartConditions(ServerLevel world, E pOwner, CallbackInfoReturnable<Boolean> cir) {
      if (ServerVaults.get(world).isPresent()) {
         LivingEntity livingentity = getAttackTarget(pOwner);
         cir.setReturnValue(pOwner.isHolding(is -> is.getItem() instanceof CrossbowItem) && BehaviorUtils.canSee(pOwner, livingentity));
      }
   }

   @Inject(
      method = {"tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;J)V"},
      at = {@At("RETURN")}
   )
   protected void tick(ServerLevel world, E pOwner, long pGameTime, CallbackInfo ci) {
      if (ServerVaults.get(world).isPresent()) {
         this.attackDelay = 1;
      }
   }
}
