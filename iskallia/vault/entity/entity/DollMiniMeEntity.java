package iskallia.vault.entity.entity;

import com.mojang.authlib.GameProfile;
import iskallia.vault.entity.IPlayerSkinHolder;
import iskallia.vault.item.VaultDollItem;
import iskallia.vault.world.data.DollLootData;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

public class DollMiniMeEntity extends Monster implements IPlayerSkinHolder {
   private static final int ATTACK_COOLDOWN_SECONDS_MIN = 1;
   private static final int ATTACK_COOLDOWN_SECONDS_MAX = 5;
   private static final String OWNER_PROFILE_TAG = "ownerProfile";
   private static final String DOLL_ID_TAG = "dollId";
   private static final String XP_POINTS_TAG = "xpPoints";
   private long attackCooldownTime = 0L;
   private GameProfile gameProfile = null;
   private UUID dollId = null;
   private int experiencePoints = 0;
   private ResourceLocation skinLocation = null;
   private boolean updatingSkin = false;
   private boolean slimSkin = false;
   private static final int PANIC_DISTANCE = 20;

   public DollMiniMeEntity(EntityType<DollMiniMeEntity> entityType, Level level) {
      super(entityType, level);
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 50.0)
         .add(Attributes.ATTACK_KNOCKBACK, 10.0)
         .add(Attributes.MOVEMENT_SPEED, 0.34F)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.ARMOR, 2.0);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag
   ) {
      if (dataTag != null) {
         VaultDollItem.getPlayerGameProfile(dataTag).ifPresent(this::setGameProfile);
         VaultDollItem.getDollUUID(dataTag).ifPresent(dollUuid -> this.dollId = dollUuid);
         this.experiencePoints = VaultDollItem.getExperience(dataTag);
      }

      return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new DollMiniMeEntity.MoveCloserToTargetGoal(this));
      this.goalSelector.addGoal(3, new DollMiniMeEntity.DollMiniMePanicGoal(this, 1.2));
      this.goalSelector.addGoal(4, new DollMiniMeEntity.DollMiniMeMeleeAttackGoal(this));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.targetSelector.addGoal(0, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
      super.dropCustomDeathLoot(source, looting, recentlyHit);
      Entity killedByEntity = source.getEntity();
      if (killedByEntity instanceof ServerPlayer player && !(killedByEntity instanceof FakePlayer) && this.level instanceof ServerLevel serverLevel) {
         DollLootData dollLootData = DollLootData.get(serverLevel, this.dollId);
         List<ItemStack> loot = dollLootData.getLoot();
         loot.forEach(this::spawnAtLocation);
         dollLootData.clearLoot();
         PlayerVaultStatsData statsData = PlayerVaultStatsData.get(player.getLevel());
         statsData.addVaultExp(player, this.experiencePoints);
      }
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(IPlayerSkinHolder.OPTIONAL_GAME_PROFILE, Optional.empty());
   }

   public boolean doHurtTarget(Entity entity) {
      boolean result = super.doHurtTarget(entity);
      this.attackCooldownTime = this.level.getGameTime() + this.random.nextInt(1, 5) * 20L;
      return result;
   }

   public boolean isOutOfCoolDownTime() {
      return this.level.getGameTime() > this.attackCooldownTime;
   }

   public boolean isPersistenceRequired() {
      return true;
   }

   public boolean isBaby() {
      return true;
   }

   public boolean isTargetLookingAtMe() {
      LivingEntity target = this.getTarget();
      if (target == null) {
         return false;
      } else {
         Vec3 targetViewVector = target.getViewVector(1.0F).normalize();
         Vec3 positionsVector = new Vec3(this.getX() - target.getX(), this.getEyeY() - target.getEyeY(), this.getZ() - target.getZ());
         positionsVector = positionsVector.normalize();
         double closenessRatio = targetViewVector.dot(positionsVector);
         return closenessRatio > 0.5 && target.hasLineOfSight(this);
      }
   }

   @Override
   public Optional<GameProfile> getGameProfile() {
      return (Optional<GameProfile>)this.entityData.get(IPlayerSkinHolder.OPTIONAL_GAME_PROFILE);
   }

   @Override
   public void setGameProfile(GameProfile gameProfile) {
      this.gameProfile = gameProfile;
      this.entityData.set(IPlayerSkinHolder.OPTIONAL_GAME_PROFILE, Optional.ofNullable(gameProfile));
   }

   @Override
   public Optional<ResourceLocation> getSkinLocation() {
      return Optional.ofNullable(this.skinLocation);
   }

   @Override
   public boolean isUpdatingSkin() {
      return this.updatingSkin;
   }

   @Override
   public void setSkinLocation(ResourceLocation skinLocation) {
      this.skinLocation = skinLocation;
   }

   @Override
   public void startUpdatingSkin() {
      this.updatingSkin = true;
   }

   @Override
   public void stopUpdatingSkin() {
      this.updatingSkin = false;
   }

   @Override
   public boolean hasSlimSkin() {
      return this.slimSkin;
   }

   @Override
   public void setSlimSkin(boolean slimSkin) {
      this.slimSkin = slimSkin;
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.gameProfile != null) {
         compound.put("ownerProfile", NbtUtils.writeGameProfile(new CompoundTag(), this.gameProfile));
      }

      if (this.dollId != null) {
         compound.putUUID("dollId", this.dollId);
      }

      compound.putInt("xpPoints", this.experiencePoints);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setGameProfile(compound.contains("ownerProfile") ? NbtUtils.readGameProfile(compound.getCompound("ownerProfile")) : null);
      if (compound.contains("dollId")) {
         this.dollId = compound.getUUID("dollId");
      }

      this.experiencePoints = compound.getInt("xpPoints");
   }

   private static class DollMiniMeMeleeAttackGoal extends MeleeAttackGoal {
      private final DollMiniMeEntity dollMiniMe;

      public DollMiniMeMeleeAttackGoal(DollMiniMeEntity mob) {
         super(mob, 1.0, true);
         this.dollMiniMe = mob;
      }

      public boolean canUse() {
         LivingEntity target = this.dollMiniMe.getTarget();
         return target != null
            && this.dollMiniMe.isOutOfCoolDownTime()
            && (!this.dollMiniMe.isTargetLookingAtMe() || this.getAttackReachSqr(target) > this.dollMiniMe.distanceToSqr(target))
            && super.canUse();
      }

      public boolean canContinueToUse() {
         LivingEntity target = this.dollMiniMe.getTarget();
         return target != null
            && this.dollMiniMe.isOutOfCoolDownTime()
            && (!this.dollMiniMe.isTargetLookingAtMe() || this.getAttackReachSqr(target) > this.dollMiniMe.distanceToSqr(target))
            && super.canContinueToUse();
      }

      protected double getAttackReachSqr(LivingEntity attackTarget) {
         return 2.0 * super.getAttackReachSqr(attackTarget);
      }
   }

   private static class DollMiniMePanicGoal extends PanicGoal {
      private final DollMiniMeEntity miniMe;

      public DollMiniMePanicGoal(DollMiniMeEntity miniMe, double speedModifier) {
         super(miniMe, speedModifier);
         this.miniMe = miniMe;
      }

      protected boolean shouldPanic() {
         LivingEntity target = this.mob.getTarget();
         return target != null && this.mob.distanceToSqr(this.mob.getTarget()) < 400.0 && this.miniMe.isTargetLookingAtMe();
      }

      protected boolean findRandomPosition() {
         Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 5, 4, this.mob.getTarget().getPosition(0.0F));
         if (vec3 == null) {
            return false;
         } else {
            this.posX = vec3.x;
            this.posY = vec3.y;
            this.posZ = vec3.z;
            return true;
         }
      }
   }

   private static class MoveCloserToTargetGoal extends Goal {
      private static final int HALF_PANIC_DISTANCE = 10;
      private final DollMiniMeEntity mob;
      private int ticksUntilNextPathRecalculation;
      private double pathedTargetX;
      private double pathedTargetY;
      private double pathedTargetZ;

      public MoveCloserToTargetGoal(DollMiniMeEntity mob) {
         this.mob = mob;
      }

      public boolean canUse() {
         LivingEntity target = this.mob.getTarget();
         return target != null && !this.mob.isTargetLookingAtMe() && this.mob.distanceToSqr(this.mob.getTarget()) > 100.0;
      }

      public void tick() {
         LivingEntity target = this.mob.getTarget();
         if (target != null) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double d0 = this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if (this.mob.getSensing().hasLineOfSight(target)
               && this.ticksUntilNextPathRecalculation <= 0
               && (
                  this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0
                     || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0
                     || this.mob.getRandom().nextFloat() < 0.05F
               )) {
               this.pathedTargetX = target.getX();
               this.pathedTargetY = target.getY();
               this.pathedTargetZ = target.getZ();
               this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
               if (d0 > 1024.0) {
                  this.ticksUntilNextPathRecalculation += 10;
               } else if (d0 > 256.0) {
                  this.ticksUntilNextPathRecalculation += 5;
               }

               if (!this.mob.getNavigation().moveTo(target, 1.0)) {
                  this.ticksUntilNextPathRecalculation += 15;
               }

               this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }
         }
      }

      public void start() {
         this.ticksUntilNextPathRecalculation = 0;
         Path path = this.mob.getNavigation().createPath(this.mob.getTarget(), 0);
         this.mob.getNavigation().moveTo(path, 1.0);
      }

      public void stop() {
         this.mob.getNavigation().stop();
      }
   }
}
