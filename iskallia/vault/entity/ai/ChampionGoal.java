package iskallia.vault.entity.ai;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.entity.champion.ChampionLogic;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.WorldSettings;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ChampionGoal {
   public static void registerProjectileGoal(Vault vault, final Mob mob) {
      VaultDifficulty vaultDifficulty = WorldSettings.get(mob.level).getPlayerDifficulty(vault.get(Vault.OWNER));
      if (vaultDifficulty.shouldChampionRangeAttack()) {
         mob.goalSelector
            .addGoal(
               3,
               new ChampionGoal.ThrowSpearGoal<Mob>(mob, ModConfigs.FIGHTER.chancerPerTick / 2, 1, ChampionGoal.ThrowableSpear::new) {
                  @Override
                  public boolean canUse() {
                     return super.canUse() && this.targetOutOfReachAbove();
                  }

                  @Override
                  public boolean canContinueToUse() {
                     return super.canContinueToUse() && this.targetOutOfReachAbove();
                  }

                  private boolean targetOutOfReachAbove() {
                     LivingEntity target = mob.getTarget();
                     if (target != null
                        && mob instanceof ChampionLogic.IChampionLogicHolder championLogicHolder
                        && !championLogicHolder.getChampionLogic().isPacified()) {
                        double targetDistance = mob.distanceToSqr(target);
                        double attackReach = this.getAttackReachSqr(target);
                        double yDiff = target.getY() - mob.getY();
                        Path path = mob.getNavigation().getPath();
                        boolean stuck = mob.getNavigation().isStuck();
                        boolean canNotReach = path == null || !path.canReach() || stuck;
                        return targetDistance > attackReach
                           && targetDistance < attackReach * 16.0
                           && (yDiff >= 2.0 && yDiff <= 4.0 || canNotReach && mob.tickCount > 20);
                     } else {
                        return false;
                     }
                  }

                  private double getAttackReachSqr(LivingEntity pAttackTarget) {
                     return mob.getBbWidth() * 2.0F * mob.getBbWidth() * 2.0F + pAttackTarget.getBbWidth();
                  }
               }
            );
      }
   }

   public static class ThrowSpearGoal<T extends Mob> extends GoalTask<T> {
      private final int chance;
      private final int count;
      private final ChampionGoal.ThrowSpearGoal.Projectile projectile;
      private int progress;
      private int waitTicks;

      public ThrowSpearGoal(T entity, int chance, int count, ChampionGoal.ThrowSpearGoal.Projectile projectile) {
         super(entity);
         this.chance = chance;
         this.count = count;
         this.projectile = projectile;
         this.waitTicks = 0;
      }

      public boolean canUse() {
         return this.getEntity().getTarget() != null;
      }

      public boolean canContinueToUse() {
         return this.getEntity().getTarget() != null && this.progress < this.count;
      }

      public void start() {
      }

      public void tick() {
         if (this.waitTicks < 40) {
            this.waitTicks++;
         } else {
            Entity throwEntity = this.projectile.create(this.getWorld(), this.getEntity());
            LivingEntity target = this.getEntity().getTarget();
            if (target != null) {
               double d0 = target.getEyeY() - 1.1F;
               double d1 = target.getX() - this.getEntity().getX();
               double d2 = d0 - throwEntity.getY();
               double d3 = target.getZ() - this.getEntity().getZ();
               float f = Mth.sqrt((float)(d1 * d1 + d3 * d3)) * 0.2F;
               this.shoot(throwEntity, d1, d2 + f, d3, 1.6F, 4.0F, this.getWorld().random);
               this.getWorld()
                  .playSound(
                     null,
                     this.getEntity().blockPosition(),
                     SoundEvents.SNOW_GOLEM_SHOOT,
                     SoundSource.HOSTILE,
                     1.0F,
                     0.4F / (this.getWorld().random.nextFloat() * 0.4F + 0.8F)
                  );
               this.getWorld().addFreshEntity(throwEntity);
            }

            this.waitTicks = 0;
            this.progress++;
         }
      }

      public void shoot(Entity projectile, double x, double y, double z, float velocity, float inaccuracy, Random rand) {
         Vec3 vector3d = new Vec3(x, y, z)
            .normalize()
            .add(rand.nextGaussian() * 0.0075F * inaccuracy, rand.nextGaussian() * 0.0075F * inaccuracy, rand.nextGaussian() * 0.0075F * inaccuracy)
            .scale(velocity);
         projectile.setDeltaMovement(vector3d);
         float f = Mth.sqrt((float)vector3d.horizontalDistanceSqr());
         projectile.setYRot((float)(Mth.atan2(vector3d.x, vector3d.z) * 180.0F / (float)Math.PI));
         projectile.setXRot((float)(Mth.atan2(vector3d.y, f) * 180.0F / (float)Math.PI));
         projectile.yRotO = projectile.getYRot();
         projectile.xRotO = projectile.getXRot();
      }

      public void stop() {
         this.progress = 0;
      }

      public interface Projectile {
         Entity create(Level var1, LivingEntity var2);
      }
   }

   public static class ThrowableSpear extends ThrowableItemProjectile {
      @Nullable
      private LivingEntity thrower;

      public ThrowableSpear(EntityType<ChampionGoal.ThrowableSpear> entityType, Level level) {
         super(entityType, level);
      }

      public ThrowableSpear(Level level, LivingEntity thrower) {
         super(ModEntities.SPEAR, thrower, level);
         this.thrower = thrower;
      }

      protected Item getDefaultItem() {
         return Items.FIRE_CHARGE;
      }

      protected boolean canHitEntity(Entity entity) {
         return entity instanceof Player;
      }

      protected void onHitBlock(BlockHitResult p_37258_) {
      }

      protected void onHitEntity(EntityHitResult result) {
         if (!this.level.isClientSide()) {
            if (result.getEntity() instanceof LivingEntity livingEntity) {
               if (this.thrower == null) {
                  this.discard();
                  return;
               }

               double xRatio = this.thrower.getX() - livingEntity.getX();

               for (double zRatio = this.thrower.getZ() - livingEntity.getZ();
                  xRatio * xRatio + zRatio * zRatio < 1.0E-4;
                  zRatio = (Math.random() - Math.random()) * 0.01
               ) {
                  xRatio = (Math.random() - Math.random()) * 0.01;
               }

               livingEntity.hurtMarked = true;
               EntityHelper.knockbackIgnoreResist(livingEntity, this.thrower, (float)ModConfigs.FIGHTER.knockback * 2.0F);
               livingEntity.hurt(DamageSource.mobAttack(this.thrower), (float)this.thrower.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0F);
            }
         }
      }
   }
}
