package iskallia.vault.entity.entity.elite;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.level.Level;

public class EliteStrayEntity extends Stray {
   public EliteStrayEntity(EntityType<? extends Stray> p_33836_, Level p_33837_) {
      super(p_33836_, p_33837_);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         this.level.addParticle(ParticleTypes.SNOWFLAKE, this.getRandomX(1.0), this.getY() + 2.9F, this.getRandomZ(1.0), 0.0, 0.01, 0.0);
      }
   }
}
