package iskallia.vault.entity.boss;

import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.ArtifactBossImmunityParticleOptions;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ClientboundArtifactBossImmunityParticleMessage;
import iskallia.vault.network.message.ClientboundBossStagesMessage;
import iskallia.vault.world.data.ServerVaults;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

@EventBusSubscriber
public class ArtifactBossEntity extends Monster implements IAnimatable {
   public static final EntityDataSerializer<Optional<ArtifactBossEntity.AttackMove>> OPTIONAL_ATTACK_MOVE = new EntityDataSerializer<Optional<ArtifactBossEntity.AttackMove>>() {
      public void write(FriendlyByteBuf buf, Optional<ArtifactBossEntity.AttackMove> value) {
         if (value.isPresent()) {
            buf.writeBoolean(true);
            buf.writeEnum(value.get());
         } else {
            buf.writeBoolean(false);
         }
      }

      public Optional<ArtifactBossEntity.AttackMove> read(FriendlyByteBuf buf) {
         return buf.readBoolean() ? Optional.of((ArtifactBossEntity.AttackMove)buf.readEnum(ArtifactBossEntity.AttackMove.class)) : Optional.empty();
      }

      public Optional<ArtifactBossEntity.AttackMove> copy(Optional<ArtifactBossEntity.AttackMove> value) {
         return value;
      }
   };
   private static final EntityDataAccessor<Optional<ArtifactBossEntity.AttackMove>> ACTIVE_ATTACK_MOVE = SynchedEntityData.defineId(
      ArtifactBossEntity.class, OPTIONAL_ATTACK_MOVE
   );
   private static final EntityDataAccessor<Integer> CURRENT_STAGE_INDEX = SynchedEntityData.defineId(ArtifactBossEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> IS_STUNNED = SynchedEntityData.defineId(ArtifactBossEntity.class, EntityDataSerializers.BOOLEAN);
   private final List<IBossStage> stages = new ArrayList<>();
   private boolean stagesInitialized = false;
   public Vec3 spawnPosition;
   private BossMeleeAttackGoal meleeAttackGoal;
   protected static final AnimationBuilder HAMMERSMASH_ANIM = new AnimationBuilder()
      .addAnimation("animation.vaultbattlemage.hammersmash", EDefaultLoopTypes.PLAY_ONCE);
   protected static final AnimationBuilder UPPERCUT_ANIM = new AnimationBuilder()
      .addAnimation("animation.vaultbattlemage.uppercut", EDefaultLoopTypes.PLAY_ONCE);
   protected static final AnimationBuilder SUMMON_ANIM = new AnimationBuilder().addAnimation("animation.vaultbattlemage.summon", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder SUMMON_CONTINUOUS_ANIM = new AnimationBuilder()
      .addAnimation("animation.vaultbattlemage.summonstart", EDefaultLoopTypes.PLAY_ONCE)
      .addAnimation("animation.vaultbattlemage.summonloop", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder GROUNDSLAM_ANIM = new AnimationBuilder()
      .addAnimation("animation.vaultbattlemage.groundslam", EDefaultLoopTypes.PLAY_ONCE);
   protected static final AnimationBuilder STUNNED_ANIM = new AnimationBuilder()
      .addAnimation("animation.vaultbattlemage.stunned", EDefaultLoopTypes.PLAY_ONCE)
      .addAnimation("animation.vaultbattlemage.stunnedloop", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("animation.vaultbattlemage.idle", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("animation.vaultbattlemage.walk", EDefaultLoopTypes.LOOP);
   private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

   public ArtifactBossEntity(EntityType<ArtifactBossEntity> type, Level world) {
      super(type, world);
   }

   public int getPlayerAdjustedRandomCount(int minCount, int maxCount, float additionalPlayersRatio) {
      int playerCount = this.getPlayerCount();
      return playerCount == 0
         ? 0
         : this.getLevel()
            .getRandom()
            .nextInt(
               (int)(minCount + (playerCount - 1) * minCount * additionalPlayersRatio),
               (int)(maxCount + (playerCount - 1) * maxCount * additionalPlayersRatio) + 1
            );
   }

   public Vec3 getSpawnPosition() {
      return this.spawnPosition;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new ArtifactBossEntity.BossStageGoal(this));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.meleeAttackGoal = new BossMeleeAttackGoal(this);
      this.goalSelector.addGoal(2, this.meleeAttackGoal);
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 35.0)
         .add(Attributes.MOVEMENT_SPEED, 0.1)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ARMOR, 2.0)
         .add(Attributes.ATTACK_KNOCKBACK, 2.5)
         .add(ModAttributes.CRIT_CHANCE, 0.0)
         .add(ModAttributes.CRIT_MULTIPLIER, 0.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag
   ) {
      this.spawnPosition = this.position();
      return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
   }

   public int getPlayerCount() {
      return this.level instanceof ServerLevel ? ServerVaults.get(this.level).map(vault -> vault.get(Vault.LISTENERS).getAll().size()).orElse(0) : 1;
   }

   public void tick() {
      if (!this.level.isClientSide() && !this.stagesInitialized) {
         this.stagesInitialized = true;
         ModConfigs.BOSS
            .getBossStageConfigs("artifact_boss")
            .forEach(s -> this.stages.add(BossStageManager.createStageFromAttributes(this, s.stageType(), s.attributes())));
         syncStagesToClient(this, PacketDistributor.DIMENSION.with(() -> this.level.dimension()));
         this.setCurrentStageIndex(-1);
         this.setNextStage();
      }

      if (this.level.isClientSide) {
         if (!(Boolean)this.entityData.get(IS_STUNNED)) {
            if (this.getCurrentStage().isPresent() && this.getCurrentStage().get() instanceof SparkStage) {
               int col = 16769280;
               int col2 = 16774829;
               float r = (col >> 16 & 0xFF) / 255.0F;
               float g = (col >> 8 & 0xFF) / 255.0F;
               float b = (col & 0xFF) / 255.0F;
               float r2 = (col2 >> 16 & 0xFF) / 255.0F;
               float g2 = (col2 >> 8 & 0xFF) / 255.0F;
               float b2 = (col2 & 0xFF) / 255.0F;
               Vec3 vec3 = new Vec3(3.0, 8.0, 0.0).yRot((float)Math.toRadians(this.tickCount * 15));
               this.level
                  .addParticle(
                     new ArtifactBossImmunityParticleOptions(
                        (ParticleType<ArtifactBossImmunityParticleOptions>)ModParticles.ARTIFACT_BOSS_IMMUNITY.get(),
                        20,
                        new Vector3f(r, g, b),
                        new Vector3f(r2, g2, b2)
                     ),
                     true,
                     this.getX(),
                     this.getY(),
                     this.getZ(),
                     vec3.x,
                     vec3.y,
                     vec3.z
                  );
               Vec3 vec32 = new Vec3(3.0, 8.0, 0.0).yRot((float)Math.toRadians(this.tickCount * 15 + 180));
               this.level
                  .addParticle(
                     new ArtifactBossImmunityParticleOptions(
                        (ParticleType<ArtifactBossImmunityParticleOptions>)ModParticles.ARTIFACT_BOSS_IMMUNITY.get(),
                        20,
                        new Vector3f(r, g, b),
                        new Vector3f(r2, g2, b2)
                     ),
                     true,
                     this.getX(),
                     this.getY(),
                     this.getZ(),
                     vec32.x,
                     vec32.y,
                     vec32.z
                  );
            }
         } else {
            this.level
               .addParticle(
                  (ParticleOptions)ModParticles.STUNNED.get(),
                  true,
                  this.position().x(),
                  this.position().y() + this.getBbHeight() + 0.25,
                  this.position().z(),
                  this.getBbWidth(),
                  0.0,
                  0.0
               );
         }
      }

      super.tick();
   }

   public boolean canBeAffected(MobEffectInstance potionEffect) {
      return potionEffect.getEffect() != ModEffects.CHILLED
            && potionEffect.getEffect() != ModEffects.GLACIAL_SHATTER
            && potionEffect.getEffect() != ModEffects.NO_AI
         ? super.canBeAffected(potionEffect)
         : false;
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.ARTIFACT_BOSS_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return ModSounds.ARTIFACT_BOSS_HURT;
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.ARTIFACT_BOSS_DEATH;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return !source.isCreativePlayer()
         && source != DamageSource.OUT_OF_WORLD
         && this.getCurrentStage().map(IBossStage::makesBossInvulnerable).orElse(super.isInvulnerableTo(source));
   }

   protected void updateControlFlags() {
      if (this.getCurrentStage().isEmpty()) {
         super.updateControlFlags();
      }
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(CURRENT_STAGE_INDEX, -1);
      this.entityData.define(ACTIVE_ATTACK_MOVE, Optional.empty());
      this.entityData.define(IS_STUNNED, false);
   }

   public void setStunned(boolean stunned) {
      this.entityData.set(IS_STUNNED, stunned);
   }

   public void setCurrentStageIndex(int stageIndex) {
      this.entityData.set(CURRENT_STAGE_INDEX, stageIndex);
   }

   public int getCurrentStageIndex() {
      return (Integer)this.entityData.get(CURRENT_STAGE_INDEX);
   }

   public Optional<IBossStage> getCurrentStage() {
      int currentStageIndex = this.getCurrentStageIndex();
      return currentStageIndex >= 0 && currentStageIndex < this.stages.size() ? Optional.of(this.stages.get(this.getCurrentStageIndex())) : Optional.empty();
   }

   private void setNextStage() {
      this.setCurrentStageIndex(this.getCurrentStageIndex() + 1);
   }

   public boolean hasMoreStages() {
      return this.stages.size() > this.getCurrentStageIndex() + 1;
   }

   public void setActiveAttackMove(@Nullable ArtifactBossEntity.AttackMove attackMove) {
      this.entityData.set(ACTIVE_ATTACK_MOVE, Optional.ofNullable(attackMove));
   }

   private Optional<ArtifactBossEntity.AttackMove> getActiveAttackMove() {
      return (Optional<ArtifactBossEntity.AttackMove>)this.entityData.get(ACTIVE_ATTACK_MOVE);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      ListTag stagesNbt1 = new ListTag();

      for (IBossStage stage : this.stages) {
         stagesNbt1.add(stage.serialize());
      }

      compound.put("Stages", stagesNbt1);
      compound.putBoolean("StagesInitialized", this.stagesInitialized);
      compound.putInt("CurrentStage", this.getCurrentStageIndex());
      compound.put("SpawnPosition", this.serializeSpawnPosition());
   }

   private CompoundTag serializeSpawnPosition() {
      if (this.spawnPosition == null) {
         return new CompoundTag();
      } else {
         CompoundTag tag = new CompoundTag();
         tag.putDouble("X", this.spawnPosition.x);
         tag.putDouble("Y", this.spawnPosition.y);
         tag.putDouble("Z", this.spawnPosition.z);
         return tag;
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      ListTag stagesNbt = compound.getList("Stages", 10);
      this.stages.clear();

      for (int i = 0; i < stagesNbt.size(); i++) {
         CompoundTag stageNbt = stagesNbt.getCompound(i);
         this.stages.add(BossStageManager.createStageFrom(this, stageNbt));
      }

      this.stagesInitialized = compound.getBoolean("StagesInitialized");
      this.setCurrentStageIndex(compound.getInt("CurrentStage"));
      this.spawnPosition = this.deserializeSpawnPosition(compound.getCompound("SpawnPosition"));
   }

   private Vec3 deserializeSpawnPosition(CompoundTag spawnPosition) {
      return new Vec3(spawnPosition.getDouble("X"), spawnPosition.getDouble("Y"), spawnPosition.getDouble("Z"));
   }

   public AABB getBoundingBoxForCulling() {
      return super.getBoundingBoxForCulling().inflate(2.0, 0.0, 2.0);
   }

   public int getMaxHeadYRot() {
      return 30;
   }

   public final Vec3 calculateViewVector(float adjustedYaw) {
      return super.calculateViewVector(this.getViewXRot(1.0F), adjustedYaw);
   }

   public void die(DamageSource cause) {
      if (cause != DamageSource.OUT_OF_WORLD && this.hasMoreStages()) {
         this.setHealth(1.0F);
      } else {
         super.die(cause);
         this.meleeAttackGoal.stop();
         this.getCurrentStage().ifPresent(IBossStage::stop);
      }
   }

   public void playSound(SoundEvent sound, float volume, float pitch) {
      if (sound != this.getDeathSound() || !this.hasMoreStages()) {
         super.playSound(sound, volume, pitch);
      }
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      boolean wasHurt = super.hurt(pSource, pAmount);
      if (wasHurt) {
         this.getCurrentStage().ifPresent(IBossStage::onHurt);
      }

      if (this.getCurrentStage().isPresent()
         && this.getCurrentStage().get() instanceof SparkStage
         && !this.level.isClientSide()
         && this.isInvulnerableTo(pSource)
         && pSource instanceof EntityDamageSource entityDamageSource
         && entityDamageSource.getEntity() instanceof Player) {
         int col = 16777138;
         int col2 = 16777181;
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.blockPosition())),
               new ClientboundArtifactBossImmunityParticleMessage(this.position(), col, col2)
            );
      }

      return wasHurt;
   }

   public boolean isCloseToDeath() {
      return this.getHealth() <= 1.0F;
   }

   public void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "StageAnimation", 5.0F, this::stageAnimController));
      data.addAnimationController(new AnimationController(this, "Walking", 5.0F, this::walkAnimController));
      data.addAnimationController(new AnimationController(this, "Idle", 5.0F, this::idleAnimController));
      data.addAnimationController(new AnimationController(this, "AttackMove", 5.0F, this::attackMoveAnimController));
   }

   private PlayState attackMoveAnimController(AnimationEvent<ArtifactBossEntity> event) {
      AnimationController<ArtifactBossEntity> controller = event.getController();
      return this.getActiveAttackMove().map(attackMove -> {
         return switch (attackMove) {
            case HAMMERSMASH -> {
               controller.setAnimation(HAMMERSMASH_ANIM);
               yield PlayState.CONTINUE;
            }
            case UPPERCUT -> {
               controller.setAnimation(UPPERCUT_ANIM);
               yield PlayState.CONTINUE;
            }
            case GROUNDSLAM -> {
               controller.setAnimation(GROUNDSLAM_ANIM);
               yield PlayState.CONTINUE;
            }
            case SUMMON -> {
               controller.setAnimation(SUMMON_ANIM);
               yield PlayState.CONTINUE;
            }
         };
      }).orElseGet(() -> {
         controller.markNeedsReload();
         return PlayState.STOP;
      });
   }

   private PlayState stageAnimController(AnimationEvent<ArtifactBossEntity> event) {
      return this.getCurrentStage().flatMap(IBossStage::getAnimation).map(a -> {
         event.getController().transitionLengthTicks = 5.0;
         if ((Boolean)this.entityData.get(IS_STUNNED)) {
            a = STUNNED_ANIM;
            event.getController().transitionLengthTicks = 0.0;
         }

         event.getController().setAnimation(a);
         return PlayState.CONTINUE;
      }).orElse(PlayState.STOP);
   }

   private PlayState idleAnimController(AnimationEvent<ArtifactBossEntity> event) {
      if (!event.isMoving()) {
         event.getController().setAnimation(IDLE_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState walkAnimController(AnimationEvent<ArtifactBossEntity> event) {
      if (event.isMoving()) {
         event.getController().setAnimation(WALK_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   public AnimationFactory getFactory() {
      return this.factory;
   }

   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      if (event.getTarget() instanceof ArtifactBossEntity artifactBossEntity && event.getPlayer() instanceof ServerPlayer player) {
         syncStagesToClient(artifactBossEntity, PacketDistributor.PLAYER.with(() -> player));
      }
   }

   private static void syncStagesToClient(ArtifactBossEntity artifactBossEntity, PacketTarget target) {
      List<CompoundTag> stagesNbt = new ArrayList<>();

      for (IBossStage stage : artifactBossEntity.stages) {
         stagesNbt.add(stage.serialize());
      }

      ModNetwork.CHANNEL.send(target, new ClientboundBossStagesMessage(artifactBossEntity.getId(), stagesNbt));
   }

   public void setStagesFromNbt(List<CompoundTag> stagesNbt) {
      this.stages.clear();

      for (CompoundTag stageNbt : stagesNbt) {
         this.stages.add(BossStageManager.createStageFrom(this, stageNbt));
      }
   }

   public void checkDespawn() {
      if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
         this.discard();
      } else {
         this.noActionTime = 0;
      }
   }

   private boolean isAtSpawnPoint() {
      return this.spawnPosition != null && this.spawnPosition.distanceToSqr(this.position()) < 1.0;
   }

   public boolean isStunned() {
      return (Boolean)this.entityData.get(IS_STUNNED);
   }

   public boolean ignoreExplosion() {
      return true;
   }

   static {
      EntityDataSerializers.registerSerializer(OPTIONAL_ATTACK_MOVE);
   }

   public static enum AttackMove {
      SUMMON,
      HAMMERSMASH,
      UPPERCUT,
      GROUNDSLAM;
   }

   private static class BossStageGoal extends Goal {
      private final ArtifactBossEntity boss;

      public BossStageGoal(ArtifactBossEntity boss) {
         this.boss = boss;
      }

      public boolean canUse() {
         return this.boss.getCurrentStage().isPresent();
      }

      public void start() {
         super.start();
         this.boss
            .getCurrentStage()
            .ifPresent(
               currentStage -> {
                  if (!this.boss.isAtSpawnPoint()) {
                     if (this.boss.level instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(
                           null,
                           this.boss.position().x(),
                           this.boss.position().y(),
                           this.boss.position().z(),
                           SoundEvents.ENDERMAN_TELEPORT,
                           SoundSource.PLAYERS,
                           0.5F,
                           2.0F
                        );
                     }

                     this.boss.teleportTo(this.boss.spawnPosition.x, this.boss.spawnPosition.y, this.boss.spawnPosition.z);
                  }

                  currentStage.getControlFlags().forEach(f -> this.boss.goalSelector.setControlFlag(f, false));
                  currentStage.start();
               }
            );
      }

      public void stop() {
         super.stop();
         this.boss.getCurrentStage().ifPresent(currentStage -> {
            currentStage.getControlFlags().forEach(f -> this.boss.goalSelector.setControlFlag(f, true));
            currentStage.stop();
            if (currentStage.isFinished()) {
               this.boss.setNextStage();
            }
         });
      }

      public void tick() {
         super.tick();
         this.boss.getCurrentStage().ifPresent(IBossStage::tick);
      }

      public boolean canContinueToUse() {
         return !this.boss.getCurrentStage().map(IBossStage::isFinished).orElse(true);
      }
   }
}
