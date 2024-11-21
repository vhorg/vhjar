package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.trait.ITrait;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EvokerFangsGoal extends Goal implements ITrait {
   public static final String TYPE = "evoker_fangs";
   private final VaultBossBaseEntity boss;
   private int attackCooldown;
   private int minAttackInterval = 100;
   private int maxAttackInterval = 200;
   private float inaccuracy = 1.3F;
   private int radius = 3;
   private int fangCount = 5;
   private float damageMultiplier = 0.7F;
   private int stackSize = 1;

   public EvokerFangsGoal(VaultBossBaseEntity boss) {
      this.boss = boss;
   }

   public EvokerFangsGoal setAttributes(int minAttackInterval, int maxAttackInterval, float inaccuracy, int radius, int fangCount, float meleeDamageMultiplier) {
      this.minAttackInterval = minAttackInterval;
      this.maxAttackInterval = maxAttackInterval;
      this.inaccuracy = inaccuracy;
      this.radius = radius;
      this.fangCount = fangCount;
      this.damageMultiplier = meleeDamageMultiplier;
      return this;
   }

   @Override
   public String getType() {
      return "evoker_fangs";
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addTraitGoal(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof EvokerFangsGoal evokerFangsGoal) {
         this.stackSize++;
         this.minAttackInterval = evokerFangsGoal.minAttackInterval * this.stackSize;
         this.maxAttackInterval = evokerFangsGoal.maxAttackInterval * this.stackSize;
      }
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("MinAttackInterval", this.minAttackInterval);
      nbt.putInt("MaxAttackInterval", this.maxAttackInterval);
      nbt.putFloat("Inaccuracy", this.inaccuracy);
      nbt.putInt("Radius", this.radius);
      nbt.putInt("FangCount", this.fangCount);
      nbt.putFloat("DamageMultiplier", this.damageMultiplier);
      nbt.putInt("StackSize", this.stackSize);
      return nbt;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      this.minAttackInterval = nbt.getInt("MinAttackInterval");
      this.maxAttackInterval = nbt.getInt("MaxAttackInterval");
      this.inaccuracy = nbt.getFloat("Inaccuracy");
      this.radius = nbt.getInt("Radius");
      this.fangCount = nbt.getInt("FangCount");
      this.damageMultiplier = nbt.getFloat("DamageMultiplier");
      this.stackSize = nbt.getInt("StackSize");
   }

   public void start() {
      super.start();
      this.attackCooldown = this.minAttackInterval + this.boss.getRandom().nextInt(this.maxAttackInterval - this.minAttackInterval);
   }

   public void tick() {
      if (this.attackCooldown > 0) {
         this.attackCooldown--;
      } else {
         Vec3 position = this.boss.getTarget().position();
         position = position.add(this.boss.getRandom().nextGaussian() * this.inaccuracy, 0.0, this.boss.getRandom().nextGaussian() * this.inaccuracy);
         this.spawnFangs(position, this.fangCount, this.radius);
         this.attackCooldown = this.minAttackInterval + this.boss.getRandom().nextInt(this.maxAttackInterval - this.minAttackInterval);
      }
   }

   private void spawnFangs(Vec3 position, int numberOfFangs, int fangRadius) {
      for (int i = 0; i < numberOfFangs; i++) {
         this.getGroundPos(new BlockPos(position))
            .ifPresent(
               y -> {
                  double x = position.x + this.boss.getRandom().nextInt(fangRadius * 2) - fangRadius;
                  double z = position.z + this.boss.getRandom().nextInt(fangRadius * 2) - fangRadius;
                  this.boss
                     .level
                     .addFreshEntity(
                        new EvokerFangsGoal.BossFangs(this.boss.level, x, y, z, this.boss.getAttributeValue(Attributes.ATTACK_DAMAGE) * this.damageMultiplier)
                     );
               }
            );
      }
   }

   private Optional<Double> getGroundPos(BlockPos pos) {
      while (pos.getY() >= Mth.floor(this.boss.position().y()) - 1) {
         BlockPos posBelow = pos.below();
         BlockState stateBelow = this.boss.level.getBlockState(posBelow);
         if (stateBelow.isFaceSturdy(this.boss.level, posBelow, Direction.UP)) {
            if (!this.boss.level.isEmptyBlock(pos)) {
               BlockState state = this.boss.level.getBlockState(pos);
               VoxelShape collisionShape = state.getCollisionShape(this.boss.level, pos);
               if (!collisionShape.isEmpty()) {
                  return Optional.of(pos.getY() + collisionShape.max(Axis.Y));
               }
            }

            return Optional.of((double)pos.getY());
         }

         pos = pos.below();
      }

      return Optional.empty();
   }

   public boolean canUse() {
      return this.boss.getTarget() != null;
   }

   private static class BossFangs extends EvokerFangs {
      private final double attackDamage;

      public BossFangs(Level level, double x, double y, double z, double attackDamage) {
         super(level, x, y, z, level.random.nextFloat((float) Math.PI), level.random.nextInt(10), null);
         this.attackDamage = attackDamage;
      }

      public void tick() {
         super.baseTick();
         if (this.level.isClientSide) {
            if (this.clientSideAttackStarted) {
               this.lifeTicks--;
               if (this.lifeTicks == 14) {
                  for (int i = 0; i < 12; i++) {
                     double d0 = this.getX() + (this.random.nextDouble() * 2.0 - 1.0) * this.getBbWidth() * 0.5;
                     double d1 = this.getY() + 0.05 + this.random.nextDouble();
                     double d2 = this.getZ() + (this.random.nextDouble() * 2.0 - 1.0) * this.getBbWidth() * 0.5;
                     double d3 = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                     double d4 = 0.3 + this.random.nextDouble() * 0.3;
                     double d5 = (this.random.nextDouble() * 2.0 - 1.0) * 0.3;
                     this.level.addParticle(ParticleTypes.CRIT, d0, d1 + 1.0, d2, d3, d4, d5);
                  }
               }
            }
         } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
               for (LivingEntity livingentity : this.level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2))) {
                  this.dealDamageTo(livingentity);
               }
            }

            if (!this.sentSpikeEvent) {
               this.level.broadcastEntityEvent(this, (byte)4);
               this.sentSpikeEvent = true;
            }

            if (--this.lifeTicks < 0) {
               this.discard();
            }
         }
      }

      public void dealDamageTo(LivingEntity livingEntity) {
         LivingEntity owner = this.getOwner();
         float damage = (float)this.attackDamage;
         if (livingEntity.isAlive() && !livingEntity.isInvulnerable() && livingEntity != owner) {
            if (owner == null) {
               livingEntity.hurt(DamageSource.MAGIC, damage);
            } else {
               if (owner.isAlliedTo(livingEntity)) {
                  return;
               }

               livingEntity.hurt(DamageSource.indirectMagic(this, owner), damage);
            }
         }
      }
   }
}
