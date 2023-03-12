package iskallia.vault.entity.entity.tier3;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.level.Level;

public class Tier3StrayEntity extends Stray {
   public Tier3StrayEntity(EntityType<? extends Stray> entityType, Level world) {
      super(entityType, world);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide && this.level.random.nextFloat() <= 0.4F) {
         this.level.addParticle(ParticleTypes.SNOWFLAKE, this.getRandomX(1.0), this.getY() + 1.9F, this.getRandomZ(1.0), 0.0, 0.01, 0.0);
      }
   }
}
