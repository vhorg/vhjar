package iskallia.vault.entity.entity.elite;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class ScarabEntity extends FlyingMob implements IAnimatable {
   public static final float FLAP_DEGREES_PER_TICK = 7.448451F;
   public static final int TICKS_PER_FLAP = Mth.ceil(24.166098F);
   private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(ScarabEntity.class, EntityDataSerializers.INT);
   Vec3 moveTargetPoint = Vec3.ZERO;
   BlockPos anchorPoint = BlockPos.ZERO;
   ScarabEntity.AttackPhase attackPhase = ScarabEntity.AttackPhase.CIRCLE;
   @Nullable
   UUID ownerId;
   protected static final AnimationBuilder WALKING_ANIM = new AnimationBuilder().addAnimation("animation.scarab.walking", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder FLYING_ANIM = new AnimationBuilder().addAnimation("animation.scarab.flying", EDefaultLoopTypes.LOOP);
   private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

   public ScarabEntity(EntityType<ScarabEntity> type, Level world) {
      super(type, world);
      this.xpReward = 5;
      this.moveControl = new ScarabEntity.PhantomMoveControl(this);
      this.lookControl = new ScarabEntity.PhantomLookControl(this);
   }

   public boolean isFlapping() {
      return (this.getUniqueFlapTickOffset() + this.tickCount) % TICKS_PER_FLAP == 0;
   }

   protected BodyRotationControl createBodyControl() {
      return new ScarabEntity.PhantomBodyRotationControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new ScarabEntity.PhantomAttackStrategyGoal());
      this.goalSelector.addGoal(2, new ScarabEntity.PhantomSweepAttackGoal());
      this.goalSelector.addGoal(3, new ScarabEntity.PhantomCircleAroundAnchorGoal());
      this.targetSelector.addGoal(1, new ScarabEntity.PhantomAttackPlayerTargetGoal());
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_SIZE, 0);
   }

   public void setPhantomSize(int pSize) {
      this.entityData.set(ID_SIZE, Mth.clamp(pSize, 0, 64));
   }

   private void updatePhantomSizeInfo() {
      this.refreshDimensions();
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6 + this.getPhantomSize());
   }

   public int getPhantomSize() {
      return (Integer)this.entityData.get(ID_SIZE);
   }

   protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
      return pSize.height * 0.35F;
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (ID_SIZE.equals(pKey)) {
         this.updatePhantomSizeInfo();
      }

      super.onSyncedDataUpdated(pKey);
   }

   public int getUniqueFlapTickOffset() {
      return this.getId() * 3;
   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   public void tick() {
      super.tick();
   }

   public void aiStep() {
      super.aiStep();
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
   }

   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag
   ) {
      this.anchorPoint = this.blockPosition().above(2);
      this.setPhantomSize(0);
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.contains("AX")) {
         this.anchorPoint = new BlockPos(pCompound.getInt("AX"), pCompound.getInt("AY"), pCompound.getInt("AZ"));
      }

      this.setPhantomSize(pCompound.getInt("Size"));
      if (pCompound.hasUUID("Owner")) {
         this.ownerId = pCompound.getUUID("Owner");
      }
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("AX", this.anchorPoint.getX());
      pCompound.putInt("AY", this.anchorPoint.getY());
      pCompound.putInt("AZ", this.anchorPoint.getZ());
      pCompound.putInt("Size", this.getPhantomSize());
      if (this.ownerId != null) {
         pCompound.putUUID("Owner", this.ownerId);
      }
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return true;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PARROT_FLY;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.PHANTOM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PHANTOM_DEATH;
   }

   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   public boolean canAttackType(EntityType<?> pType) {
      return true;
   }

   public EntityDimensions getDimensions(Pose pPose) {
      int i = this.getPhantomSize();
      EntityDimensions entitydimensions = super.getDimensions(pPose);
      float f = (entitydimensions.width + 0.2F * i) / entitydimensions.width;
      return entitydimensions.scale(f);
   }

   public void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "Walking", 10.0F, this::walkingAnimController));
      data.addAnimationController(new AnimationController(this, "Flying", 10.0F, this::flyingAnimController));
   }

   private PlayState walkingAnimController(AnimationEvent<ScarabEntity> event) {
      BlockState blockState = this.level.getBlockState(this.getOnPos());
      if (blockState.getBlock() != Blocks.AIR && event.isMoving()) {
         event.getController().setAnimation(WALKING_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState flyingAnimController(AnimationEvent<ScarabEntity> event) {
      if (this.isFlapping()) {
         event.getController().setAnimation(FLYING_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.CONTINUE;
      }
   }

   public AnimationFactory getFactory() {
      return this.factory;
   }

   @Nullable
   public UUID getOwner() {
      return this.ownerId;
   }

   public void setOwner(UUID ownerId) {
      this.ownerId = ownerId;
   }

   static enum AttackPhase {
      CIRCLE,
      SWOOP;
   }

   class PhantomAttackPlayerTargetGoal extends Goal {
      private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0);
      private int nextScanTick = reducedTickDelay(20);

      public boolean canUse() {
         if (this.nextScanTick > 0) {
            this.nextScanTick--;
            return false;
         } else {
            this.nextScanTick = reducedTickDelay(60);
            List<Player> list = ScarabEntity.this.level
               .getNearbyPlayers(this.attackTargeting, ScarabEntity.this, ScarabEntity.this.getBoundingBox().inflate(16.0, 64.0, 16.0));
            if (!list.isEmpty()) {
               list.sort(Comparator.comparing(Entity::getY).reversed());

               for (Player player : list) {
                  if (ScarabEntity.this.canAttack(player, TargetingConditions.DEFAULT)) {
                     ScarabEntity.this.setTarget(player);
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public boolean canContinueToUse() {
         LivingEntity livingentity = ScarabEntity.this.getTarget();
         return livingentity != null ? ScarabEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT) : false;
      }
   }

   class PhantomAttackStrategyGoal extends Goal {
      private int nextSweepTick;

      public boolean canUse() {
         LivingEntity livingentity = ScarabEntity.this.getTarget();
         return livingentity != null ? ScarabEntity.this.canAttack(livingentity, TargetingConditions.DEFAULT) : false;
      }

      public void start() {
         this.nextSweepTick = this.adjustedTickDelay(10);
         ScarabEntity.this.attackPhase = ScarabEntity.AttackPhase.CIRCLE;
         this.setAnchorAboveTarget();
      }

      public void stop() {
         ScarabEntity.this.anchorPoint = ScarabEntity.this.level
            .getHeightmapPos(Types.MOTION_BLOCKING, ScarabEntity.this.anchorPoint)
            .above(10 + ScarabEntity.this.random.nextInt(20));
      }

      public void tick() {
         if (ScarabEntity.this.attackPhase == ScarabEntity.AttackPhase.CIRCLE) {
            this.nextSweepTick--;
            if (this.nextSweepTick <= 0) {
               ScarabEntity.this.attackPhase = ScarabEntity.AttackPhase.SWOOP;
               this.setAnchorAboveTarget();
               this.nextSweepTick = this.adjustedTickDelay((8 + ScarabEntity.this.random.nextInt(4)) * 20);
               ScarabEntity.this.playSound(SoundEvents.PARROT_FLY, 10.0F, 0.95F + ScarabEntity.this.random.nextFloat() * 0.1F);
            }
         }
      }

      private void setAnchorAboveTarget() {
         ScarabEntity.this.anchorPoint = ScarabEntity.this.getTarget().blockPosition().above(4 + ScarabEntity.this.random.nextInt(10));
         if (ScarabEntity.this.anchorPoint.getY() < ScarabEntity.this.level.getSeaLevel()) {
            ScarabEntity.this.anchorPoint = new BlockPos(
               ScarabEntity.this.anchorPoint.getX(), ScarabEntity.this.level.getSeaLevel() + 1, ScarabEntity.this.anchorPoint.getZ()
            );
         }
      }
   }

   class PhantomBodyRotationControl extends BodyRotationControl {
      public PhantomBodyRotationControl(Mob p_33216_) {
         super(p_33216_);
      }

      public void clientTick() {
         ScarabEntity.this.yHeadRot = ScarabEntity.this.yBodyRot;
         ScarabEntity.this.yBodyRot = ScarabEntity.this.getYRot();
      }
   }

   class PhantomCircleAroundAnchorGoal extends ScarabEntity.PhantomMoveTargetGoal {
      private float angle;
      private float distance;
      private float height;
      private float clockwise;

      public boolean canUse() {
         return ScarabEntity.this.getTarget() == null || ScarabEntity.this.attackPhase == ScarabEntity.AttackPhase.CIRCLE;
      }

      public void start() {
         this.distance = 5.0F;
         this.height = -4.0F;
         this.clockwise = ScarabEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
         this.selectNext();
      }

      public void tick() {
         if (ScarabEntity.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
            this.height = -4.0F + ScarabEntity.this.random.nextFloat() * 9.0F;
         }

         if (ScarabEntity.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
            this.distance++;
            if (this.distance > 15.0F) {
               this.distance = 5.0F;
               this.clockwise = -this.clockwise;
            }
         }

         if (ScarabEntity.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
            this.angle = ScarabEntity.this.random.nextFloat() * 2.0F * (float) Math.PI;
            this.selectNext();
         }

         if (this.touchingTarget()) {
            this.selectNext();
         }

         if (ScarabEntity.this.moveTargetPoint.y < ScarabEntity.this.getY()
            && !ScarabEntity.this.level.isEmptyBlock(ScarabEntity.this.blockPosition().below(1))) {
            this.height = Math.max(1.0F, this.height);
            this.selectNext();
         }

         if (ScarabEntity.this.moveTargetPoint.y > ScarabEntity.this.getY()
            && !ScarabEntity.this.level.isEmptyBlock(ScarabEntity.this.blockPosition().above(1))) {
            this.height = Math.min(-1.0F, this.height);
            this.selectNext();
         }
      }

      private void selectNext() {
         if (BlockPos.ZERO.equals(ScarabEntity.this.anchorPoint)) {
            ScarabEntity.this.anchorPoint = ScarabEntity.this.blockPosition();
         }

         this.angle = this.angle + this.clockwise * 15.0F * (float) (Math.PI / 180.0);
         ScarabEntity.this.moveTargetPoint = Vec3.atLowerCornerOf(ScarabEntity.this.anchorPoint)
            .add(this.distance * Mth.cos(this.angle), -4.0F + this.height, this.distance * Mth.sin(this.angle));
      }
   }

   class PhantomLookControl extends LookControl {
      public PhantomLookControl(Mob p_33235_) {
         super(p_33235_);
      }

      public void tick() {
      }
   }

   class PhantomMoveControl extends MoveControl {
      private float speed = 0.1F;

      public PhantomMoveControl(Mob p_33241_) {
         super(p_33241_);
      }

      public void tick() {
         if (ScarabEntity.this.horizontalCollision) {
            ScarabEntity.this.setYRot(ScarabEntity.this.getYRot() + 180.0F);
            this.speed = 0.1F;
         }

         double d0 = ScarabEntity.this.moveTargetPoint.x - ScarabEntity.this.getX();
         double d1 = ScarabEntity.this.moveTargetPoint.y - ScarabEntity.this.getY();
         double d2 = ScarabEntity.this.moveTargetPoint.z - ScarabEntity.this.getZ();
         double d3 = Math.sqrt(d0 * d0 + d2 * d2);
         if (Math.abs(d3) > 1.0E-5F) {
            double d4 = 1.0 - Math.abs(d1 * 0.7F) / d3;
            d0 *= d4;
            d2 *= d4;
            d3 = Math.sqrt(d0 * d0 + d2 * d2);
            double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
            float f = ScarabEntity.this.getYRot();
            float f1 = (float)Mth.atan2(d2, d0);
            float f2 = Mth.wrapDegrees(ScarabEntity.this.getYRot() + 90.0F);
            float f3 = Mth.wrapDegrees(f1 * (180.0F / (float)Math.PI));
            ScarabEntity.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
            ScarabEntity.this.yBodyRot = ScarabEntity.this.getYRot();
            if (Mth.degreesDifferenceAbs(f, ScarabEntity.this.getYRot()) < 3.0F) {
               this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
            } else {
               this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
            }

            float f4 = (float)(-(Mth.atan2(-d1, d3) * 180.0F / (float)Math.PI));
            ScarabEntity.this.setXRot(f4);
            float f5 = ScarabEntity.this.getYRot() + 90.0F;
            double d6 = this.speed * Mth.cos(f5 * (float) (Math.PI / 180.0)) * Math.abs(d0 / d5);
            double d7 = this.speed * Mth.sin(f5 * (float) (Math.PI / 180.0)) * Math.abs(d2 / d5);
            double d8 = this.speed * Mth.sin(f4 * (float) (Math.PI / 180.0)) * Math.abs(d1 / d5);
            Vec3 vec3 = ScarabEntity.this.getDeltaMovement();
            ScarabEntity.this.setDeltaMovement(vec3.add(new Vec3(d6, d8, d7).subtract(vec3).scale(0.2)));
         }
      }
   }

   abstract class PhantomMoveTargetGoal extends Goal {
      public PhantomMoveTargetGoal() {
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      protected boolean touchingTarget() {
         return ScarabEntity.this.moveTargetPoint.distanceToSqr(ScarabEntity.this.getX(), ScarabEntity.this.getY(), ScarabEntity.this.getZ()) < 4.0;
      }
   }

   class PhantomSweepAttackGoal extends ScarabEntity.PhantomMoveTargetGoal {
      private static final int CAT_SEARCH_TICK_DELAY = 20;
      private boolean isScaredOfCat;
      private int catSearchTick;

      public boolean canUse() {
         return ScarabEntity.this.getTarget() != null && ScarabEntity.this.attackPhase == ScarabEntity.AttackPhase.SWOOP;
      }

      public boolean canContinueToUse() {
         LivingEntity livingentity = ScarabEntity.this.getTarget();
         if (livingentity == null) {
            return false;
         } else if (!livingentity.isAlive()) {
            return false;
         } else if (livingentity instanceof Player player && (livingentity.isSpectator() || player.isCreative())) {
            return false;
         } else if (!this.canUse()) {
            return false;
         } else {
            if (ScarabEntity.this.tickCount > this.catSearchTick) {
               this.catSearchTick = ScarabEntity.this.tickCount + 20;
               List<Cat> list = ScarabEntity.this.level
                  .getEntitiesOfClass(Cat.class, ScarabEntity.this.getBoundingBox().inflate(16.0), EntitySelector.ENTITY_STILL_ALIVE);

               for (Cat cat : list) {
                  cat.hiss();
               }

               this.isScaredOfCat = !list.isEmpty();
            }

            return !this.isScaredOfCat;
         }
      }

      public void start() {
      }

      public void stop() {
         ScarabEntity.this.setTarget((LivingEntity)null);
         ScarabEntity.this.attackPhase = ScarabEntity.AttackPhase.CIRCLE;
      }

      public void tick() {
         LivingEntity livingentity = ScarabEntity.this.getTarget();
         if (livingentity != null) {
            ScarabEntity.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5), livingentity.getZ());
            if (ScarabEntity.this.getBoundingBox().inflate(0.2F).intersects(livingentity.getBoundingBox())) {
               ScarabEntity.this.doHurtTarget(livingentity);
               ScarabEntity.this.attackPhase = ScarabEntity.AttackPhase.CIRCLE;
               if (!ScarabEntity.this.isSilent()) {
                  ScarabEntity.this.level.levelEvent(1039, ScarabEntity.this.blockPosition(), 0);
               }
            } else if (ScarabEntity.this.horizontalCollision || ScarabEntity.this.hurtTime > 0) {
               ScarabEntity.this.attackPhase = ScarabEntity.AttackPhase.CIRCLE;
            }
         }
      }
   }
}
