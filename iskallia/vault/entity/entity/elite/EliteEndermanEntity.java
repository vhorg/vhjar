package iskallia.vault.entity.entity.elite;

import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModEntities;
import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity;
import org.jetbrains.annotations.NotNull;

public class EliteEndermanEntity extends EnderMan {
   public EliteEndermanEntity(EntityType<EliteEndermanEntity> type, Level world) {
      super(type, world);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new EliteEndermanEntity.EndermanFreezeWhenLookedAt(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(10, new EliteEndermanEntity.EndermanLeaveBlockGoal(this));
      this.targetSelector.addGoal(1, new EliteEndermanEntity.EndermanLookForPlayerGoal(this, this::isAngryAt));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Endermite.class, true, false));
      this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal(this, false));
   }

   public boolean canBeAffected(MobEffectInstance potionEffect) {
      return potionEffect.getEffect() == ModEffects.GLACIAL_SHATTER ? false : super.canBeAffected(potionEffect);
   }

   public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
      if (this.level.isClientSide) {
         return super.hurt(pSource, pAmount);
      } else {
         int prevOrnamentCount = this.getVisibleOrnamentCount();
         float prevHealth = this.getHealth();
         boolean hurt = super.hurt(pSource, pAmount);
         int ornamentCount = this.getVisibleOrnamentCount();
         float health = this.getHealth();
         if (prevOrnamentCount != ornamentCount && health < prevHealth) {
            EndervexEntity endervex = (EndervexEntity)ModEntities.ENDERVEX.create(this.level);
            if (endervex != null) {
               if (this.isPersistenceRequired()) {
                  endervex.setPersistenceRequired();
               }

               endervex.setDeltaMovement(new Vec3(0.0, 0.35, 0.0));
               endervex.moveTo(this.position());
               this.level.addFreshEntity(endervex);
               endervex.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 10.0F, 0.95F + endervex.getRandom().nextFloat() * 0.1F);
               endervex.setOwner(this);
            }
         }

         return hurt;
      }
   }

   public static int getVisibleOrnamentCount(double healthRate) {
      if (healthRate > 0.75) {
         return 3;
      } else if (healthRate > 0.5) {
         return 2;
      } else {
         return healthRate > 0.25 ? 1 : 0;
      }
   }

   public int getVisibleOrnamentCount() {
      return getVisibleOrnamentCount(this.getHealth() / this.getMaxHealth());
   }

   boolean isLookingAtMe(Player pPlayer) {
      ItemStack itemstack = (ItemStack)pPlayer.getInventory().armor.get(3);
      if (ForgeHooks.shouldSuppressEnderManAnger(this, pPlayer, itemstack)) {
         return false;
      } else {
         Vec3 vec3 = pPlayer.getViewVector(1.0F).normalize();
         Vec3 vec31 = new Vec3(this.getX() - pPlayer.getX(), this.getEyeY() - pPlayer.getEyeY(), this.getZ() - pPlayer.getZ());
         double d0 = vec31.length();
         vec31 = vec31.normalize();
         double d1 = vec3.dot(vec31);
         return d1 > 1.0 - 0.025 / d0 ? pPlayer.hasLineOfSight(this) : false;
      }
   }

   boolean teleportTowards(Entity p_32501_) {
      Vec3 vec3 = new Vec3(this.getX() - p_32501_.getX(), this.getY(0.5) - p_32501_.getEyeY(), this.getZ() - p_32501_.getZ());
      vec3 = vec3.normalize();
      double d0 = 16.0;
      double d1 = this.getX() + (this.random.nextDouble() - 0.5) * 8.0 - vec3.x * 16.0;
      double d2 = this.getY() + (this.random.nextInt(16) - 8) - vec3.y * 16.0;
      double d3 = this.getZ() + (this.random.nextDouble() - 0.5) * 8.0 - vec3.z * 16.0;
      return this.teleport(d1, d2, d3);
   }

   private boolean teleport(double pX, double pY, double pZ) {
      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos(pX, pY, pZ);

      while (
         blockpos$mutableblockpos.getY() > this.level.getMinBuildHeight() && !this.level.getBlockState(blockpos$mutableblockpos).getMaterial().blocksMotion()
      ) {
         blockpos$mutableblockpos.move(Direction.DOWN);
      }

      BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos);
      boolean flag = blockstate.getMaterial().blocksMotion();
      boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
      if (flag && !flag1) {
         EnderEntity event = ForgeEventFactory.onEnderTeleport(this, pX, pY, pZ);
         if (event.isCanceled()) {
            return false;
         } else {
            boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (flag2 && !this.isSilent()) {
               this.level.playSound((Player)null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
               this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag2;
         }
      } else {
         return false;
      }
   }

   static class EndermanFreezeWhenLookedAt extends Goal {
      private final EliteEndermanEntity enderman;
      @Nullable
      private LivingEntity target;

      public EndermanFreezeWhenLookedAt(EliteEndermanEntity p_32550_) {
         this.enderman = p_32550_;
         this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
      }

      public boolean canUse() {
         this.target = this.enderman.getTarget();
         if (!(this.target instanceof Player)) {
            return false;
         } else {
            double d0 = this.target.distanceToSqr(this.enderman);
            return d0 > 256.0 ? false : this.enderman.isLookingAtMe((Player)this.target);
         }
      }

      public void start() {
         this.enderman.getNavigation().stop();
      }

      public void tick() {
         this.enderman.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
      }
   }

   static class EndermanLeaveBlockGoal extends Goal {
      private final EnderMan enderman;

      public EndermanLeaveBlockGoal(EnderMan p_32556_) {
         this.enderman = p_32556_;
      }

      public boolean canUse() {
         if (this.enderman.getCarriedBlock() == null) {
            return false;
         } else {
            return !ForgeEventFactory.getMobGriefingEvent(this.enderman.level, this.enderman)
               ? false
               : this.enderman.getRandom().nextInt(reducedTickDelay(2000)) == 0;
         }
      }

      public void tick() {
         Random random = this.enderman.getRandom();
         Level level = this.enderman.level;
         int i = Mth.floor(this.enderman.getX() - 1.0 + random.nextDouble() * 2.0);
         int j = Mth.floor(this.enderman.getY() + random.nextDouble() * 2.0);
         int k = Mth.floor(this.enderman.getZ() - 1.0 + random.nextDouble() * 2.0);
         BlockPos blockpos = new BlockPos(i, j, k);
         BlockState blockstate = level.getBlockState(blockpos);
         BlockPos blockpos1 = blockpos.below();
         BlockState blockstate1 = level.getBlockState(blockpos1);
         BlockState blockstate2 = this.enderman.getCarriedBlock();
         if (blockstate2 != null) {
            blockstate2 = Block.updateFromNeighbourShapes(blockstate2, this.enderman.level, blockpos);
            if (this.canPlaceBlock(level, blockpos, blockstate2, blockstate, blockstate1, blockpos1)
               && !ForgeEventFactory.onBlockPlace(this.enderman, BlockSnapshot.create(level.dimension(), level, blockpos1), Direction.UP)) {
               level.setBlock(blockpos, blockstate2, 3);
               level.gameEvent(this.enderman, GameEvent.BLOCK_PLACE, blockpos);
               this.enderman.setCarriedBlock((BlockState)null);
            }
         }
      }

      private boolean canPlaceBlock(Level p_32559_, BlockPos p_32560_, BlockState p_32561_, BlockState p_32562_, BlockState p_32563_, BlockPos p_32564_) {
         return p_32562_.isAir()
            && !p_32563_.isAir()
            && !p_32563_.is(Blocks.BEDROCK)
            && !p_32563_.is(net.minecraftforge.common.Tags.Blocks.ENDERMAN_PLACE_ON_BLACKLIST)
            && p_32563_.isCollisionShapeFullBlock(p_32559_, p_32564_)
            && p_32561_.canSurvive(p_32559_, p_32560_)
            && p_32559_.getEntities(this.enderman, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(p_32560_))).isEmpty();
      }
   }

   static class EndermanLookForPlayerGoal extends NearestAttackableTargetGoal<Player> {
      private final EliteEndermanEntity enderman;
      @Nullable
      private Player pendingTarget;
      private int aggroTime;
      private int teleportTime;
      private final TargetingConditions startAggroTargetConditions;
      private final TargetingConditions continueAggroTargetConditions = TargetingConditions.forCombat().ignoreLineOfSight();

      public EndermanLookForPlayerGoal(EliteEndermanEntity p_32573_, @Nullable Predicate<LivingEntity> p_32574_) {
         super(p_32573_, Player.class, 10, false, false, p_32574_);
         this.enderman = p_32573_;
         this.startAggroTargetConditions = TargetingConditions.forCombat()
            .range(this.getFollowDistance())
            .selector(p_32578_ -> p_32573_.isLookingAtMe((Player)p_32578_));
      }

      public boolean canUse() {
         this.pendingTarget = this.enderman.level.getNearestPlayer(this.startAggroTargetConditions, this.enderman);
         return this.pendingTarget != null;
      }

      public void start() {
         this.aggroTime = this.adjustedTickDelay(5);
         this.teleportTime = 0;
         this.enderman.setBeingStaredAt();
      }

      public void stop() {
         this.pendingTarget = null;
         super.stop();
      }

      public boolean canContinueToUse() {
         if (this.pendingTarget != null) {
            if (!this.enderman.isLookingAtMe(this.pendingTarget)) {
               return false;
            } else {
               this.enderman.lookAt(this.pendingTarget, 10.0F, 10.0F);
               return true;
            }
         } else {
            return this.target != null && this.continueAggroTargetConditions.test(this.enderman, this.target) ? true : super.canContinueToUse();
         }
      }

      public void tick() {
         if (this.enderman.getTarget() == null) {
            super.setTarget((LivingEntity)null);
         }

         if (this.pendingTarget != null) {
            if (--this.aggroTime <= 0) {
               this.target = this.pendingTarget;
               this.pendingTarget = null;
               super.start();
            }
         } else {
            if (this.target != null && !this.enderman.isPassenger()) {
               if (this.enderman.isLookingAtMe((Player)this.target)) {
                  if (this.target.distanceToSqr(this.enderman) < 16.0) {
                     this.enderman.teleport();
                  }

                  this.teleportTime = 0;
               } else if (this.target.distanceToSqr(this.enderman) > 256.0
                  && this.teleportTime++ >= this.adjustedTickDelay(30)
                  && this.enderman.teleportTowards(this.target)) {
                  this.teleportTime = 0;
               }
            }

            super.tick();
         }
      }
   }
}
