package iskallia.vault.entity.entity.winterwalker;

import iskallia.vault.init.ModSounds;
import javax.annotation.Nonnull;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

public class WinterwalkerEntity extends Skeleton {
   public WinterwalkerEntity(EntityType<? extends Skeleton> entityType, Level world) {
      super(entityType, world);
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.WINTERWALKER_IDLE;
   }

   public int getAmbientSoundInterval() {
      return 240;
   }

   @Nonnull
   protected SoundEvent getStepSound() {
      return ModSounds.WINTERWALKER_STEP;
   }

   protected SoundEvent getHurtSound(@Nonnull DamageSource damageSource) {
      return ModSounds.WINTERWALKER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.WINTERWALKER_DEATH;
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide && this.level.random.nextInt(10) < 3) {
         this.level.addParticle(ParticleTypes.SNOWFLAKE, this.getRandomX(1.0), this.getY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
      }
   }
}
