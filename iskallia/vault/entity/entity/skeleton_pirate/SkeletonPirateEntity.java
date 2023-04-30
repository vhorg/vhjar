package iskallia.vault.entity.entity.skeleton_pirate;

import iskallia.vault.init.ModSounds;
import javax.annotation.Nonnull;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

public class SkeletonPirateEntity extends Skeleton {
   public SkeletonPirateEntity(EntityType<? extends Skeleton> entityType, Level world) {
      super(entityType, world);
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.SKELETON_PIRATE_IDLE;
   }

   @Nonnull
   protected SoundEvent getStepSound() {
      return SoundEvents.STRAY_STEP;
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide && !this.isInWater()) {
         if (this.level.random.nextInt(10) < 5) {
            this.level.addParticle(ParticleTypes.FALLING_WATER, this.getRandomX(1.0), this.getY() + 1.25, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
         }

         if (this.level.random.nextInt(10) < 1) {
            this.level.addParticle(ParticleTypes.FALLING_DRIPSTONE_WATER, this.getRandomX(1.0), this.getY() + 1.25, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
         }
      }
   }
}
