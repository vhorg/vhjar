package iskallia.vault.entity.entity;

import com.mojang.datafixers.util.Either;
import iskallia.vault.aura.AuraManager;
import iskallia.vault.aura.EntityAuraProvider;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.entity.ai.FollowEntityGoal;
import iskallia.vault.entity.eternal.ActiveEternalData;
import iskallia.vault.entity.eternal.EternalData;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.util.calc.GrantedEffectHelper;
import iskallia.vault.util.damage.DamageUtil;
import iskallia.vault.world.data.EternalsData;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class EternalEntity extends Zombie {
   private static final EntityDataAccessor<String> ETERNAL_NAME = SynchedEntityData.defineId(EternalEntity.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor<Integer> ETERNAL_VARIANT = SynchedEntityData.defineId(EternalEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> ETERNAL_SHOW_SKIN = SynchedEntityData.defineId(EternalEntity.class, EntityDataSerializers.BOOLEAN);
   public SkinProfile skin;
   public float sizeMultiplier = 1.0F;
   private boolean ancient = false;
   private long despawnTime = Long.MAX_VALUE;
   private final ServerBossEvent bossInfo;
   private UUID owner;
   private UUID eternalId;
   private String providedAura;

   public EternalEntity(EntityType<? extends EternalEntity> type, Level world) {
      super(type, world);
      if (this.level.isClientSide) {
         this.skin = new SkinProfile();
      }

      this.bossInfo = new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.PROGRESS);
      this.bossInfo.setDarkenScreen(true);
      this.bossInfo.setVisible(false);
      this.setCanPickUpLoot(false);
      this.setCustomNameVisible(true);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ETERNAL_NAME, "Eternal");
      this.entityData.define(ETERNAL_VARIANT, EternalsData.EternalVariant.CAVE.getId());
      this.entityData.define(ETERNAL_SHOW_SKIN, false);
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.1, false));
      this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.1, true, 4, this::canBreakDoors));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.1));
      this.targetSelector.addGoal(2, new FollowEntityGoal(this, 1.1, 32.0F, 3.0F, false, () -> this.getOwner().right()));
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getLocationSkin() {
      return this.skin.getLocationSkin();
   }

   public void setOwner(UUID owner) {
      this.owner = owner;
   }

   public void setSkinName(String skinName) {
      this.entityData.set(ETERNAL_NAME, skinName);
   }

   public String getSkinName() {
      return (String)this.entityData.get(ETERNAL_NAME);
   }

   public void setAncient(boolean ancient) {
      this.ancient = ancient;
   }

   public boolean isAncient() {
      return this.ancient;
   }

   public void setEternalId(UUID eternalId) {
      this.eternalId = eternalId;
   }

   public UUID getEternalId() {
      return this.eternalId;
   }

   public void setProvidedAura(String providedAura) {
      this.providedAura = providedAura;
   }

   public String getProvidedAura() {
      return this.providedAura;
   }

   public void setDespawnTime(long despawnTime) {
      this.despawnTime = despawnTime;
   }

   public void setVariant(EternalsData.EternalVariant variant) {
      this.entityData.set(ETERNAL_VARIANT, variant.getId());
   }

   public EternalsData.EternalVariant getVariant() {
      return EternalsData.EternalVariant.byId((Integer)this.entityData.get(ETERNAL_VARIANT));
   }

   public void setUsingPlayerSkin(boolean usingPlayerSkin) {
      this.entityData.set(ETERNAL_SHOW_SKIN, usingPlayerSkin);
   }

   public boolean isUsingPlayerSkin() {
      return (Boolean)this.entityData.get(ETERNAL_SHOW_SKIN);
   }

   public boolean isBaby() {
      return false;
   }

   protected boolean isSunSensitive() {
      return false;
   }

   protected void doUnderWaterConversion() {
   }

   protected boolean convertsInWater() {
      return false;
   }

   public boolean isInvertedHealAndHarm() {
      return false;
   }

   public MobCategory getClassification(boolean forSpawnCount) {
      return MobCategory.MONSTER;
   }

   public Either<UUID, ServerPlayer> getOwner() {
      if (this.level.isClientSide()) {
         return Either.left(this.owner);
      } else {
         ServerPlayer player = this.getServer().getPlayerList().getPlayer(this.owner);
         return player == null ? Either.left(this.owner) : Either.right(player);
      }
   }

   public void tick() {
      super.tick();
      if (this.level instanceof ServerLevel sWorld) {
         int tickCounter = sWorld.getServer().getTickCount();
         if (tickCounter < this.despawnTime) {
            ActiveEternalData.getInstance().updateEternal(this);
         }

         if (this.dead) {
            return;
         }

         if (tickCounter >= this.despawnTime) {
            this.kill();
         }

         double amplitude = this.getDeltaMovement().distanceToSqr(0.0, this.getDeltaMovement().y(), 0.0);
         if (amplitude > 0.004) {
            this.setSprinting(true);
         } else {
            this.setSprinting(false);
         }

         this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
         if (this.tickCount % 10 == 0) {
            this.updateAttackTarget();
         }

         if (this.providedAura != null && this.tickCount % 4 == 0) {
            this.getOwner().ifRight(sPlayer -> {
               EternalAuraConfig.AuraConfig auraCfg = ModConfigs.ETERNAL_AURAS.getByName(this.providedAura);
               if (auraCfg != null) {
                  AuraManager.getInstance().provideAura(EntityAuraProvider.ofEntity(this, auraCfg));
               }
            });
         }

         GrantedEffectHelper.applyEffects(this, GrantedEffectHelper.getSnapshotEffectData(this));
      } else {
         if (this.dead) {
            return;
         }

         if (!Objects.equals(this.getSkinName(), this.skin.getLatestNickname())) {
            this.skin.updateSkin(this.getSkinName());
         }
      }
   }

   protected void tickDeath() {
      super.tickDeath();
   }

   public void setTarget(LivingEntity entity) {
      if (entity != this.getOwner().right().orElse(null)
         && !(entity instanceof EternalEntity)
         && !(entity instanceof Player)
         && !(entity instanceof EtchingVendorEntity)) {
         super.setTarget(entity);
      }
   }

   public void setLastHurtByMob(LivingEntity entity) {
      if (entity != this.getOwner().right().orElse(null)
         && !(entity instanceof EternalEntity)
         && !(entity instanceof Player)
         && !(entity instanceof EtchingVendorEntity)) {
         super.setLastHurtByMob(entity);
      }
   }

   private void updateAttackTarget() {
      AABB box = this.getBoundingBox().inflate(32.0);
      this.level
         .getEntitiesOfClass(
            LivingEntity.class,
            box,
            e -> {
               Either<UUID, ServerPlayer> owner = this.getOwner();
               return owner.right().isPresent() && owner.right().get() == e
                  ? false
                  : !(e instanceof EternalEntity) && !(e instanceof Player) && !(e instanceof EtchingVendorEntity);
            }
         )
         .stream()
         .sorted(Comparator.comparingDouble(e -> e.position().distanceTo(this.position())))
         .findFirst()
         .ifPresent(this::setTarget);
   }

   private Predicate<LivingEntity> ignoreEntities() {
      return e -> !(e instanceof EternalEntity) && !(e instanceof Player);
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   public SoundEvent getDeathSound() {
      return SoundEvents.PLAYER_DEATH;
   }

   public SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.PLAYER_HURT;
   }

   public void setCustomName(Component name) {
      super.setCustomName(name);
      this.bossInfo.setName(this.getDisplayName());
   }

   public void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("ancient", this.ancient);
      tag.putFloat("SizeMultiplier", this.sizeMultiplier);
      tag.putLong("DespawnTime", this.despawnTime);
      if (this.providedAura != null) {
         tag.putString("providedAura", this.providedAura);
      }

      if (this.owner != null) {
         tag.putString("Owner", this.owner.toString());
      }

      if (this.eternalId != null) {
         tag.putString("eternalId", this.eternalId.toString());
      }
   }

   public void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.ancient = tag.getBoolean("ancient");
      this.sizeMultiplier = tag.getFloat("SizeMultiplier");
      this.changeSize(this.sizeMultiplier);
      this.despawnTime = tag.getLong("DespawnTime");
      if (tag.contains("providedAura", 8)) {
         this.providedAura = tag.getString("providedAura");
      }

      if (tag.contains("Owner", 8)) {
         this.owner = UUID.fromString(tag.getString("Owner"));
      }

      if (tag.contains("eternalId", 8)) {
         this.eternalId = UUID.fromString(tag.getString("eternalId"));
      }

      this.bossInfo.setName(this.getDisplayName());
   }

   public void startSeenByPlayer(ServerPlayer player) {
      super.startSeenByPlayer(player);
      this.bossInfo.addPlayer(player);
   }

   public void stopSeenByPlayer(ServerPlayer player) {
      super.stopSeenByPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public EntityDimensions getDimensions(Pose pose) {
      return this.dimensions;
   }

   public float getSizeMultiplier() {
      return this.sizeMultiplier;
   }

   public EternalEntity changeSize(float m) {
      this.sizeMultiplier = m;
      EntityHelper.changeSize(this, this.sizeMultiplier);
      if (!this.level.isClientSide()) {
         ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new FighterSizeMessage(this, this.sizeMultiplier));
      }

      return this;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
      return super.getStandingEyeHeight(pose, size) * this.sizeMultiplier;
   }

   public void die(DamageSource cause) {
      super.die(cause);
      if (this.level instanceof ServerLevel sWorld && this.dead && this.owner != null && this.eternalId != null && !cause.isBypassInvul()) {
         EternalData eternal = EternalsData.get(sWorld).getEternal(this.eternalId);
         if (eternal != null) {
            eternal.setAlive(false);
         }
      }
   }

   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnData, CompoundTag dataTag
   ) {
      this.setCustomName(this.getCustomName());
      this.setCanBreakDoors(true);
      this.setCanPickUpLoot(false);
      this.setPersistenceRequired();
      if (this.random.nextInt(100) == 0) {
         Chicken chicken = (Chicken)EntityType.CHICKEN.create(this.level);
         if (chicken != null) {
            chicken.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            chicken.finalizeSpawn(world, difficulty, reason, spawnData, dataTag);
            chicken.setChickenJockey(true);
            ((ServerLevel)this.level).addWithUUID(chicken);
            this.startRiding(chicken);
         }
      }

      return spawnData;
   }

   public boolean hurt(DamageSource source, float amount) {
      return source.getEntity() instanceof ServerPlayer player && player.getUUID().equals(this.owner) ? false : super.hurt(source, amount);
   }

   public boolean doHurtTarget(Entity entity) {
      if (!this.level.isClientSide() && this.level instanceof ServerLevel sWorld) {
         sWorld.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
         this.level
            .playSound(
               null, this.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, this.random.nextFloat() - this.random.nextFloat()
            );
      }

      return DamageUtil.shotgunAttackApply(entity, x$0 -> super.doHurtTarget(x$0));
   }
}
