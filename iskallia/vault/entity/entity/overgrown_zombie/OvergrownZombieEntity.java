package iskallia.vault.entity.entity.overgrown_zombie;

import iskallia.vault.init.ModSounds;
import javax.annotation.Nonnull;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class OvergrownZombieEntity extends Zombie {
   public OvergrownZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.OVERGROWN_ZOMBIE_IDLE;
   }

   public int getAmbientSoundInterval() {
      return 240;
   }

   @Nonnull
   protected SoundEvent getStepSound() {
      return ModSounds.OVERGROWN_ZOMBIE_STEP;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return ModSounds.OVERGROWN_ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.OVERGROWN_ZOMBIE_DEATH;
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide && !this.isInWater() && this.level.random.nextInt(10) < 5) {
         this.level.addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, this.getRandomX(1.0), this.getY() + 1.25, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
      }
   }
}
