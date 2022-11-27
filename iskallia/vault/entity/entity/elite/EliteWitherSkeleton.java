package iskallia.vault.entity.entity.elite;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;

public class EliteWitherSkeleton extends WitherSkeleton {
   public EliteWitherSkeleton(EntityType<? extends WitherSkeleton> entityType, Level level) {
      super(entityType, level);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         this.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getRandomX(1.0), this.getY() + 2.9F, this.getRandomZ(1.0), 0.0, 0.01, 0.0);
      }
   }
}
