package iskallia.vault.entity.entity.miner_zombie;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Tier3MinerZombieEntity extends MinerZombieEntity {
   public Tier3MinerZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide && !this.isInWater() && this.level.random.nextInt(30) < 5) {
         Vec3 candlePos = this.getEyePosition().add(0.0, 0.68F, 0.0);
         this.level.addParticle(ParticleTypes.FLAME, candlePos.x, candlePos.y, candlePos.z, 0.0, 0.0, 0.0);
         this.level.addParticle(ParticleTypes.SMOKE, candlePos.x, candlePos.y, candlePos.z, 0.0, 0.0, 0.0);
      }
   }
}
