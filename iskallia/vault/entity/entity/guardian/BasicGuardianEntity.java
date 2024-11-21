package iskallia.vault.entity.entity.guardian;

import iskallia.vault.entity.entity.guardian.helper.GuardianType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BasicGuardianEntity extends AbstractGuardianEntity {
   public BasicGuardianEntity(EntityType<BasicGuardianEntity> entityType, GuardianType type, Level world) {
      super(entityType, type, world);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide && this.level.random.nextInt(10) < 5) {
         this.level
            .addParticle(
               ParticleTypes.FLAME,
               this.getX() + (this.random.nextDouble() - 0.5) / 1.3,
               this.getY() + this.random.nextDouble() * 1.75,
               this.getZ() + (this.random.nextDouble() - 0.5) / 1.3,
               0.0,
               0.0,
               0.0
            );
      }
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.level.isClientSide ? null : (SoundEvent)PiglinAi.getSoundForCurrentActivity(this).orElse(null);
   }

   @Override
   protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
      return SoundEvents.PIGLIN_BRUTE_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.PIGLIN_BRUTE_DEATH;
   }
}
