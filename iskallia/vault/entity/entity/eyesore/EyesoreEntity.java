package iskallia.vault.entity.entity.eyesore;

import iskallia.vault.entity.VaultBoss;
import iskallia.vault.entity.ai.eyesore.BasicAttackTask;
import iskallia.vault.entity.ai.eyesore.EyesorePath;
import iskallia.vault.entity.ai.eyesore.LaserAttackTask;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EyesoreEntity extends Ghast implements VaultBoss {
   public static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(EyesoreEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> LASER_TARGET = SynchedEntityData.defineId(EyesoreEntity.class, EntityDataSerializers.INT);
   public final ServerBossEvent bossInfo;
   public EyesorePath path = new EyesorePath();
   public BasicAttackTask<EyesoreEntity> basicAttack = new BasicAttackTask(this);
   public LaserAttackTask laserAttack = new LaserAttackTask(this);

   public EyesoreEntity(EntityType<? extends Ghast> type, Level worldIn) {
      super(type, worldIn);
      this.bossInfo = new ServerBossEvent(this.getDisplayName(), BossBarColor.RED, BossBarOverlay.NOTCHED_6);
      this.noPhysics = true;
      this.setPersistenceRequired();
   }

   public EyesoreEntity.State getState() {
      Integer ordinal = (Integer)this.entityData.get(STATE);
      return EyesoreEntity.State.values()[ordinal];
   }

   public void setState(EyesoreEntity.State state) {
      this.entityData.set(STATE, state.ordinal());
   }

   protected void dropFromLootTable(@Nonnull DamageSource damageSource, boolean attackedRecently) {
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(STATE, EyesoreEntity.State.NORMAL.ordinal());
      this.entityData.define(LASER_TARGET, 0);
   }

   protected void registerGoals() {
   }

   @Override
   public ServerBossEvent getServerBossInfo() {
      return this.bossInfo;
   }

   public void startSeenByPlayer(ServerPlayer player) {
      super.startSeenByPlayer(player);
      this.bossInfo.addPlayer(player);
   }

   public void stopSeenByPlayer(ServerPlayer player) {
      super.stopSeenByPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public void tick() {
      if (!this.level.isClientSide) {
         if (this.laserAttack.isFinished()) {
            if (this.random.nextInt(100) == 0) {
               this.laserAttack.reset();
            }
         } else {
            this.laserAttack.tick();
         }

         this.path.tick(this);
      }

      int id = (Integer)this.getEntityData().get(LASER_TARGET);
      Entity entity = this.getCommandSenderWorld().getEntity(id);
      LivingEntity target = entity instanceof LivingEntity ? (LivingEntity)entity : null;
      if (target != null) {
         this.lookAtTarget(target);
      }

      super.tick();
   }

   protected void lookAtTarget(LivingEntity target) {
      this.setXRot(this.getTargetPitch(target));
      this.yHeadRot = this.getTargetYaw(target);
   }

   private double getEyePosition(Entity entity) {
      return entity instanceof LivingEntity ? entity.getEyeY() : (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
   }

   protected float getTargetPitch(LivingEntity target) {
      double d0 = target.getX() - this.getX();
      double d1 = this.getEyePosition(target) - this.getEyeY();
      double d2 = target.getZ() - this.getZ();
      double d3 = Mth.sqrt((float)(d0 * d0 + d2 * d2));
      return (float)(-(Mth.atan2(d1, d3) * 180.0F / (float)Math.PI));
   }

   protected float getTargetYaw(LivingEntity target) {
      double d0 = target.getX() - this.getX();
      double d1 = target.getZ() - this.getZ();
      return (float)(Mth.atan2(d1, d0) * 180.0F / (float)Math.PI) - 90.0F;
   }

   public void playerTouch(@Nonnull Player playerEntity) {
      if (playerEntity instanceof ServerPlayer player) {
         Vec3 posPlayer = player.position();
         Vec3 posEyesore = this.position();
         byte damage = 10;
         byte knockbackStrength = 5;
         player.hurt(DamageSource.mobAttack(this), damage);
         player.knockback(knockbackStrength, posEyesore.x - posPlayer.x, posEyesore.z - posPlayer.z);
      }
   }

   @Nonnull
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   public SoundEvent getDeathSound() {
      return super.getDeathSound();
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENDER_DRAGON_AMBIENT;
   }

   public SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.ENDER_DRAGON_HURT;
   }

   public float getVoicePitch() {
      return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
   }

   public void setCustomName(Component name) {
      super.setCustomName(name);
      this.bossInfo.setName(this.getDisplayName());
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.bossInfo.setName(this.getDisplayName());
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 100.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 3.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
         .add(Attributes.ARMOR, 2.0);
   }

   public static enum State {
      NORMAL,
      GIVING_BIRTH;
   }
}
