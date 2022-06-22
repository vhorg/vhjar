package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.entity.EyesoreFireballEntity;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class BasicAttackTask<T extends MobEntity> extends EyesoreTask<T> {
   public int tick = 0;

   public BasicAttackTask(T entity) {
      super(entity);
   }

   @Override
   public void tick() {
      if (!this.isFinished()) {
         List<Optional<ServerPlayerEntity>> players = this.getVault()
            .getPlayers()
            .stream()
            .filter(playerx -> playerx instanceof VaultRunner)
            .map(p -> p.getServerPlayer(this.getWorld().func_73046_m()))
            .collect(Collectors.toList());
         Optional<ServerPlayerEntity> player = this.tick / 27 < players.size() ? players.get(this.tick / 27) : Optional.empty();
         if (player.isPresent()) {
            ServerPlayerEntity target = player.get();
            this.getEntity().func_70671_ap().func_75651_a(target, 30.0F, 30.0F);
            if (this.tick % 9 == 0) {
               EyesoreFireballEntity throwEntity = new EyesoreFireballEntity(this.getWorld(), this.getEntity());
               throwEntity.func_70107_b(throwEntity.func_226277_ct_(), throwEntity.func_226278_cu_() - 5.0, throwEntity.func_226281_cx_());
               double d0 = target.func_226280_cw_() - 1.1F;
               double d1 = target.func_226277_ct_() - this.getEntity().func_226277_ct_();
               double d2 = d0 - throwEntity.func_226278_cu_();
               double d3 = target.func_226281_cx_() - this.getEntity().func_226281_cx_();
               float f = MathHelper.func_76133_a(d1 * d1 + d3 * d3) * 0.1F;
               this.shoot(throwEntity, d1, d2 + f, d3, 3.2F, 0.0F, this.getWorld().field_73012_v);
               this.getWorld()
                  .func_184133_a(
                     null,
                     this.getEntity().func_233580_cy_(),
                     SoundEvents.field_187606_E,
                     SoundCategory.HOSTILE,
                     1.0F,
                     0.4F / (this.getWorld().field_73012_v.nextFloat() * 0.4F + 0.8F)
                  );
               this.getWorld().func_217376_c(throwEntity);
            }
         }

         this.tick++;
      }
   }

   public void shoot(Entity projectile, double x, double y, double z, float velocity, float inaccuracy, Random rand) {
      Vector3d vector3d = new Vector3d(x, y, z)
         .func_72432_b()
         .func_72441_c(rand.nextGaussian() * 0.0075F * inaccuracy, rand.nextGaussian() * 0.0075F * inaccuracy, rand.nextGaussian() * 0.0075F * inaccuracy)
         .func_186678_a(velocity);
      projectile.func_213317_d(vector3d);
      float f = MathHelper.func_76133_a(Entity.func_213296_b(vector3d));
      projectile.field_70177_z = (float)(MathHelper.func_181159_b(vector3d.field_72450_a, vector3d.field_72449_c) * 180.0F / (float)Math.PI);
      projectile.field_70125_A = (float)(MathHelper.func_181159_b(vector3d.field_72448_b, f) * 180.0F / (float)Math.PI);
      projectile.field_70126_B = projectile.field_70177_z;
      projectile.field_70127_C = projectile.field_70125_A;
   }

   @Override
   public boolean isFinished() {
      if (this.getVault() == null) {
         return true;
      } else {
         List<Optional<ServerPlayerEntity>> players = this.getVault()
            .getPlayers()
            .stream()
            .map(p -> p.getServerPlayer(this.getWorld().func_73046_m()))
            .collect(Collectors.toList());
         return this.tick / 27 >= Math.min(players.size(), 2);
      }
   }

   @Override
   public void reset() {
      this.tick = 0;
   }
}
