package iskallia.vault.item.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class VaultPearlEntity extends ThrownEnderpearl {
   public VaultPearlEntity(Level worldIn, LivingEntity throwerIn) {
      super(worldIn, throwerIn);
   }

   protected void onHit(HitResult result) {
      Type raytraceresult$type = result.getType();
      if (raytraceresult$type == Type.ENTITY) {
         this.onHitEntity((EntityHitResult)result);
      } else if (raytraceresult$type == Type.BLOCK) {
         this.onHitBlock((BlockHitResult)result);
      }

      Entity entity = this.getOwner();

      for (int i = 0; i < 32; i++) {
         this.level
            .addParticle(
               ParticleTypes.PORTAL,
               this.getX(),
               this.getY() + this.random.nextDouble() * 2.0,
               this.getZ(),
               this.random.nextGaussian(),
               0.0,
               this.random.nextGaussian()
            );
      }

      if (!this.level.isClientSide && !this.isRemoved()) {
         if (entity instanceof ServerPlayer serverplayerentity) {
            if (serverplayerentity.connection.getConnection().isConnected() && serverplayerentity.level == this.level && !serverplayerentity.isSleeping()) {
               if (entity.isPassenger()) {
                  entity.stopRiding();
               }

               entity.teleportTo(this.getX(), this.getY(), this.getZ());
               entity.resetFallDistance();
            }
         } else if (entity != null) {
            entity.teleportTo(this.getX(), this.getY(), this.getZ());
            entity.resetFallDistance();
         }

         this.discard();
      }
   }
}
