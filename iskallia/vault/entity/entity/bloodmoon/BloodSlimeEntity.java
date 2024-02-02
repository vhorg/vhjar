package iskallia.vault.entity.entity.bloodmoon;

import javax.annotation.Nonnull;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;

public class BloodSlimeEntity extends Slime {
   public BloodSlimeEntity(EntityType<? extends Slime> entityType, Level world) {
      super(entityType, world);
   }

   @Nonnull
   protected ParticleOptions getParticleType() {
      return ParticleTypes.LANDING_LAVA;
   }
}
