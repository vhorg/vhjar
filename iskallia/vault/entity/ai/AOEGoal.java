package iskallia.vault.entity.ai;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AOEGoal<T extends Mob> extends GoalTask<T> {
   protected boolean completed = false;
   protected boolean started = false;
   protected int tick = 0;
   protected int delay = 0;
   protected BlockPos shockwave;
   private final Predicate<LivingEntity> filter;

   public AOEGoal(T entity, Predicate<LivingEntity> filter) {
      super(entity);
      this.filter = filter;
   }

   public boolean canUse() {
      return this.getRandom().nextInt(120) == 0 && this.getEntity().getTarget() != null;
   }

   public boolean canContinueToUse() {
      return !this.completed;
   }

   public void start() {
      this.getEntity().setDeltaMovement(this.getEntity().getDeltaMovement().add(0.0, 1.1, 0.0));
      this.delay = 5;
   }

   public void tick() {
      if (!this.completed) {
         if (!this.started && this.delay < 0 && this.getEntity().isOnGround()) {
            this.getWorld()
               .playSound(
                  null,
                  this.getEntity().getX(),
                  this.getEntity().getY(),
                  this.getEntity().getZ(),
                  SoundEvents.DRAGON_FIREBALL_EXPLODE,
                  this.getEntity().getSoundSource(),
                  1.0F,
                  1.0F
               );
            ((ServerLevel)this.getWorld())
               .sendParticles(
                  ParticleTypes.EXPLOSION,
                  this.getEntity().getX() + 0.5,
                  this.getEntity().getY() + 0.1,
                  this.getEntity().getZ() + 0.5,
                  10,
                  this.getRandom().nextGaussian() * 0.02,
                  this.getRandom().nextGaussian() * 0.02,
                  this.getRandom().nextGaussian() * 0.02,
                  1.0
               );
            this.shockwave = this.getEntity().blockPosition();
            this.started = true;
         }

         if (this.started) {
            double max = 50.0;
            double distance = this.tick * 2;
            double nextDistance = this.tick * 2 + 2;
            if (distance >= max) {
               this.completed = true;
               return;
            }

            this.getWorld().getEntitiesOfClass(LivingEntity.class, new AABB(this.shockwave).inflate(max, max, max), e -> {
               if (e != this.getEntity() && !e.isSpectator() && this.filter.test(e)) {
                  double d = Math.sqrt(e.blockPosition().distSqr(this.shockwave));
                  return d >= distance && d < nextDistance;
               } else {
                  return false;
               }
            }).forEach(e -> {
               Vec3 direction = new Vec3(e.getX() - this.shockwave.getX(), e.getY() - this.shockwave.getY(), e.getZ() - this.shockwave.getZ()).scale(0.5);
               direction = direction.normalize().add(0.0, 1.0 - 0.02 * (this.tick + 1), 0.0);
               e.setDeltaMovement(e.getDeltaMovement().add(direction));
               e.hurt(DamageSource.GENERIC, 8.0F / (this.tick + 1));
            });
            this.tick++;
         } else {
            this.delay--;
         }
      }
   }

   public void stop() {
      this.completed = false;
      this.started = false;
      this.tick = 0;
      this.delay = 0;
      this.shockwave = null;
   }
}
