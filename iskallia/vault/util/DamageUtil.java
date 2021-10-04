package iskallia.vault.util;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class DamageUtil {
   public static <T extends Entity> void shotgunAttack(T e, Consumer<T> attackFn) {
      shotgunAttackApply(e, entity -> {
         attackFn.accept((T)entity);
         return null;
      });
   }

   public static <T extends Entity, R> R shotgunAttackApply(T e, Function<T, R> attackFn) {
      int prevHurtTicks = e.field_70172_ad;
      if (e instanceof LivingEntity) {
         LivingEntity le = (LivingEntity)e;
         float prevDamage = le.field_110153_bc;
         e.field_70172_ad = 0;
         le.field_110153_bc = 0.0F;

         Object var5;
         try {
            var5 = attackFn.apply(e);
         } finally {
            e.field_70172_ad = prevHurtTicks;
            le.field_110153_bc = prevDamage;
         }

         return (R)var5;
      } else {
         e.field_70172_ad = 0;

         Object le;
         try {
            le = attackFn.apply(e);
         } finally {
            e.field_70172_ad = prevHurtTicks;
         }

         return (R)le;
      }
   }
}
