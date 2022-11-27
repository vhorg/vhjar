package iskallia.vault.util.damage;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DamageUtil {
   public static <T extends Entity> void shotgunAttack(T e, Consumer<T> attackFn) {
      shotgunAttackApply(e, entity -> {
         attackFn.accept((T)entity);
         return null;
      });
   }

   public static <T extends Entity, R> R shotgunAttackApply(T e, Function<T, R> attackFn) {
      int prevHurtTicks = e.invulnerableTime;
      if (e instanceof LivingEntity le) {
         float prevDamage = le.lastHurt;
         e.invulnerableTime = 0;
         le.lastHurt = 0.0F;

         Object var5;
         try {
            var5 = attackFn.apply(e);
         } finally {
            e.invulnerableTime = prevHurtTicks;
            le.lastHurt = prevDamage;
         }

         return (R)var5;
      } else {
         e.invulnerableTime = 0;

         Object prevDamage;
         try {
            prevDamage = attackFn.apply(e);
         } finally {
            e.invulnerableTime = prevHurtTicks;
         }

         return (R)prevDamage;
      }
   }
}
