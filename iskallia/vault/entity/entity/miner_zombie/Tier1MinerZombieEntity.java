package iskallia.vault.entity.entity.miner_zombie;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Tier1MinerZombieEntity extends MinerZombieEntity {
   public Tier1MinerZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide && !this.isInWater() && this.level.random.nextInt(30) < 5) {
         float pitch = this.getRotationVector().x;
         float yaw = this.getRotationVector().y;
         float rand = this.level.random.nextFloat(-1.0F, 1.0F);
         float candleDist = 0.33F;
         Vec3 deltaPos = new Vec3(0.0, 0.0, 1.0)
            .xRot((float) (Math.PI / 180.0) * pitch)
            .yRot((float) (Math.PI / 180.0) * -yaw)
            .multiply(candleDist, candleDist, candleDist);
         Vec3 candlePos = this.getEyePosition().add(deltaPos).add(rand * 0.05, 0.5 + this.level.random.nextFloat() * 0.05, rand * 0.05);
         this.level.addParticle(ParticleTypes.SMALL_FLAME, candlePos.x, candlePos.y, candlePos.z, 0.0, 0.0, 0.0);
         this.level.addParticle(ParticleTypes.SMOKE, candlePos.x, candlePos.y, candlePos.z, 0.0, 0.0, 0.0);
      }
   }
}
