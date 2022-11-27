package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.entity.entity.eyesore.EyesoreFireballEntity;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class BasicAttackTask<T extends Mob> extends EyesoreTask<T> {
   public int tick = 0;

   public BasicAttackTask(T entity) {
      super(entity);
   }

   public void tick() {
      if (!this.isFinished()) {
         List<Optional<ServerPlayer>> players = this.getVault()
            .getPlayers()
            .stream()
            .map(p -> p.getServerPlayer(this.getWorld().getServer()))
            .collect(Collectors.toList());
         Optional<ServerPlayer> player = this.tick / 27 < players.size() ? players.get(this.tick / 27) : Optional.empty();
         if (player.isPresent()) {
            ServerPlayer target = player.get();
            this.getEntity().getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (this.tick % 9 == 0) {
               EyesoreFireballEntity throwEntity = new EyesoreFireballEntity(this.getWorld(), this.getEntity());
               double d0 = target.getEyeY() - 1.1F;
               double d1 = target.getX() - this.getEntity().getX();
               double d2 = d0 - throwEntity.getY();
               double d3 = target.getZ() - this.getEntity().getZ();
               float f = Mth.sqrt((float)(d1 * d1 + d3 * d3)) * 0.1F;
               this.shoot(throwEntity, d1, d2 + f, d3, 3.2F, 0.0F, this.getWorld().random);
               this.getWorld()
                  .playSound(
                     null,
                     this.getEntity().blockPosition(),
                     SoundEvents.BLAZE_SHOOT,
                     SoundSource.HOSTILE,
                     1.0F,
                     0.4F / (this.getWorld().random.nextFloat() * 0.4F + 0.8F)
                  );
               this.getWorld().addFreshEntity(throwEntity);
            }
         }

         this.tick++;
      }
   }

   public void shoot(Entity projectile, double x, double y, double z, float velocity, float inaccuracy, Random rand) {
      Vec3 vector3d = new Vec3(x, y, z)
         .normalize()
         .add(rand.nextGaussian() * 0.0075F * inaccuracy, rand.nextGaussian() * 0.0075F * inaccuracy, rand.nextGaussian() * 0.0075F * inaccuracy)
         .scale(velocity);
      projectile.setDeltaMovement(vector3d);
      float f = Mth.sqrt((float)vector3d.horizontalDistanceSqr());
      projectile.setXRot((float)(Mth.atan2(vector3d.x, vector3d.z) * 180.0F / (float)Math.PI));
      projectile.setYRot((float)(Mth.atan2(vector3d.y, f) * 180.0F / (float)Math.PI));
      projectile.yRotO = projectile.getYRot();
      projectile.xRotO = projectile.getXRot();
   }

   public boolean isFinished() {
      if (this.getVault() == null) {
         return true;
      } else {
         List<Optional<ServerPlayer>> players = this.getVault()
            .getPlayers()
            .stream()
            .map(p -> p.getServerPlayer(this.getWorld().getServer()))
            .collect(Collectors.toList());
         return this.tick / 27 >= players.size();
      }
   }

   public void reset() {
      this.tick = 0;
   }
}
