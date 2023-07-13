package iskallia.vault.entity.entity.miner_zombie;

import javax.annotation.Nonnull;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class Tier5MinerZombieEntity extends MinerZombieEntity {
   public Tier5MinerZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }

   public boolean doHurtTarget(@Nonnull Entity target) {
      if (!super.doHurtTarget(target)) {
         return false;
      } else {
         if (target instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 1, true, true));
         }

         return true;
      }
   }
}
