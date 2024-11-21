package iskallia.vault.entity.boss;

import com.mojang.math.Vector3f;
import iskallia.vault.client.particles.ArtifactBossImmunityParticleOptions;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.entity.boss.attack.AoeCloseAttack;
import iskallia.vault.entity.boss.attack.BasicMeleeAttack;
import iskallia.vault.entity.boss.attack.BossMeleeAttackGoal;
import iskallia.vault.entity.boss.attack.IMeleeAttack;
import iskallia.vault.entity.boss.attack.ThrowAttack;
import iskallia.vault.entity.boss.stage.BossStageManager;
import iskallia.vault.entity.boss.stage.IBossStage;
import iskallia.vault.entity.boss.stage.SparkStage;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModParticles;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ClientboundArtifactBossImmunityParticleMessage;
import iskallia.vault.network.message.ClientboundBossStagesMessage;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.WorldSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
public class ArtifactBossEntity extends VaultBossBaseEntity implements IAnimatable {
   public static final String SUMMON_ATTACK_NAME = "summon";
   public static final String PUNCH_ATTACK_NAME = "punch";
   public static final String HAMMER_SMASH_ATTACK_NAME = "hammersmash";
   public static final String UPPERCUT_ATTACK_NAME = "uppercut";
   public static final String GROUND_SLAM_ATTACK_NAME = "groundslam";
   public static final String THROW_ATTACK_NAME = "throw";
   public static final String AOE_CLOSE_ATTACK_NAME = "aoeclose";
   public static final BasicMeleeAttack.BasicMeleeAttackAttributes HAMMERSMASH_ATTACK = new BasicMeleeAttack.BasicMeleeAttackAttributes(
      new BasicMeleeAttack.BasicMeleeAttackAttributes.Slice(-0.1F, 0.6F), 30, 17, "hammersmash", 1.0F, 0.0F
   );
   public static final BasicMeleeAttack.BasicMeleeAttackAttributes UPPERCUT_ATTACK = new BasicMeleeAttack.BasicMeleeAttackAttributes(
      new BasicMeleeAttack.BasicMeleeAttackAttributes.Slice(-0.1F, 0.6F), 25, 15, "uppercut", 1.5F, 0.3F
   );
   public static final BasicMeleeAttack.BasicMeleeAttackAttributes GROUNDSLAM_ATTACK = new BasicMeleeAttack.BasicMeleeAttackAttributes(
      new BasicMeleeAttack.BasicMeleeAttackAttributes.Slice(-0.1F, 0.6F), 45, 24, "groundslam", 2.5F, 0.1F
   );
   public static final BasicMeleeAttack.BasicMeleeAttackAttributes PUNCH_ATTACK = new BasicMeleeAttack.BasicMeleeAttackAttributes(
      new BasicMeleeAttack.BasicMeleeAttackAttributes.Slice(0.0F, 0.6F), 20, 8, "punch", 0.5F, 0.0F
   );
   public static final Map<String, BiFunction<VaultBossBaseEntity, Double, IMeleeAttack>> MELEE_ATTACK_FACTORIES = Map.of(
      "hammersmash",
      (boss, multiplier) -> new BasicMeleeAttack(boss, multiplier, HAMMERSMASH_ATTACK),
      "uppercut",
      (boss, multiplier) -> new BasicMeleeAttack(boss, multiplier, UPPERCUT_ATTACK),
      "groundslam",
      (boss, multiplier) -> new BasicMeleeAttack(boss, multiplier, GROUNDSLAM_ATTACK),
      "throw",
      ThrowAttack::new,
      "aoeclose",
      AoeCloseAttack::new,
      "punch",
      (boss, multiplier) -> new BasicMeleeAttack(boss, multiplier, PUNCH_ATTACK)
   );
   private static final EntityDataAccessor<Integer> CURRENT_STAGE_INDEX = SynchedEntityData.defineId(ArtifactBossEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> IS_STUNNED = SynchedEntityData.defineId(ArtifactBossEntity.class, EntityDataSerializers.BOOLEAN);
   private final List<IBossStage> stages = new ArrayList<>();
   private boolean stagesInitialized = false;
   private BossMeleeAttackGoal meleeAttackGoal;
   private static final AnimationBuilder HAMMERSMASH_ANIM = new AnimationBuilder()
      .addAnimation("animation.vaultbattlemage.hammersmash", EDefaultLoopTypes.PLAY_ONCE);
   private static final AnimationBuilder UPPERCUT_ANIM = new AnimationBuilder().addAnimation("animation.vaultbattlemage.uppercut", EDefaultLoopTypes.PLAY_ONCE);
   public static final AnimationBuilder SUMMON_ANIM = new AnimationBuilder().addAnimation("animation.vaultbattlemage.summon", EDefaultLoopTypes.LOOP);
   public static final AnimationBuilder SUMMON_CONTINUOUS_ANIM = new AnimationBuilder()
      .addAnimation("animation.vaultbattlemage.summonstart", EDefaultLoopTypes.PLAY_ONCE)
      .addAnimation("animation.vaultbattlemage.summonloop", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder GROUNDSLAM_ANIM = new AnimationBuilder()
      .addAnimation("animation.vaultbattlemage.groundslam", EDefaultLoopTypes.PLAY_ONCE);
   protected static final AnimationBuilder STUNNED_ANIM = new AnimationBuilder()
      .addAnimation("animation.vaultbattlemage.stunned", EDefaultLoopTypes.PLAY_ONCE)
      .addAnimation("animation.vaultbattlemage.stunnedloop", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("animation.vaultbattlemage.idle", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("animation.vaultbattlemage.walk", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder AOE_CLOSE = new AnimationBuilder().addAnimation("animation.vaultbattlemage.aoeclose2", EDefaultLoopTypes.PLAY_ONCE);
   private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

   public ArtifactBossEntity(EntityType<ArtifactBossEntity> type, Level world) {
      super(type, world);
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
         .add(Attributes.MAX_HEALTH, 1000000.0)
         .add(Attributes.FOLLOW_RANGE, 35.0)
         .add(Attributes.MOVEMENT_SPEED, 0.1)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ARMOR, 2.0)
         .add(Attributes.ATTACK_KNOCKBACK, 2.5)
         .add(ModAttributes.CRIT_CHANCE, 0.0)
         .add(ModAttributes.CRIT_MULTIPLIER, 0.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   public void tick() {
      if (!this.level.isClientSide() && !this.stagesInitialized) {
         this.stagesInitialized = true;
         ModConfigs.HERALD
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

   public void push(Entity pEntity) {
   }

   public void push(double pX, double pY, double pZ) {
   }

   public boolean startRiding(Entity pEntity, boolean pForce) {
      return true;
   }

   public void setScaledHealth(int baseHealth) {
      double healthMultiplier = ServerVaults.get(this.level)
         .map(vault -> WorldSettings.get(this.level).getPlayerDifficulty(vault.get(Vault.OWNER)).getBossHealthMultiplier())
         .orElse(1.0);
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(baseHealth * healthMultiplier);
      this.setHealth((float)(baseHealth * healthMultiplier));
   }

   public void setScaledDamage(double baseDamage) {
      double damageMultiplier = ServerVaults.get(this.level)
         .map(vault -> WorldSettings.get(this.level).getPlayerDifficulty(vault.get(Vault.OWNER)).getBossDamageMultiplier())
         .orElse(1.0);
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(baseDamage * damageMultiplier);
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

   @Override
   public Map<String, BiFunction<VaultBossBaseEntity, Double, IMeleeAttack>> getMeleeAttackFactories() {
      return MELEE_ATTACK_FACTORIES;
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(CURRENT_STAGE_INDEX, -1);
      this.entityData.define(IS_STUNNED, false);
   }

   @Override
   public WeightedList<VaultBossBaseEntity.AttackData> getMeleeAttacks() {
      return this.getCurrentStage().map(IBossStage::getMeleeAttacks).orElse(WeightedList.empty());
   }

   @Override
   public WeightedList<VaultBossBaseEntity.AttackData> getRageAttacks() {
      return this.getCurrentStage().map(IBossStage::getRageAttacks).orElse(WeightedList.empty());
   }

   @Override
   public double getAttackReach() {
      return 5.0;
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
      this.getCurrentStage().ifPresent(IBossStage::init);
   }

   public boolean hasMoreStages() {
      return this.stages.size() > this.getCurrentStageIndex() + 1;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      ListTag stagesNbt1 = new ListTag();

      for (IBossStage stage : this.stages) {
         stagesNbt1.add(stage.serialize());
      }

      compound.put("Stages", stagesNbt1);
      compound.putBoolean("StagesInitialized", this.stagesInitialized);
      compound.putInt("CurrentStage", this.getCurrentStageIndex());
   }

   @Override
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
   }

   public AABB getBoundingBoxForCulling() {
      return super.getBoundingBoxForCulling().inflate(2.0, 0.0, 2.0);
   }

   public int getMaxHeadYRot() {
      return 30;
   }

   public void die(DamageSource cause) {
      if (cause != DamageSource.OUT_OF_WORLD && this.hasMoreStages()) {
         this.setHealth(1.0F);
      } else {
         super.die(cause);
         this.meleeAttackGoal.stop();
         this.getCurrentStage().ifPresent(stage -> {
            stage.stop();
            stage.finish();
         });
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
      data.addAnimationController(new AnimationController(this, "AttackMove", 0.0F, this::attackMoveAnimController));
   }

   private PlayState attackMoveAnimController(AnimationEvent<ArtifactBossEntity> event) {
      AnimationController<ArtifactBossEntity> controller = event.getController();
      return this.getActiveAttackMove().map(attackMove -> {
         return switch (attackMove) {
            case "hammersmash" -> {
               controller.setAnimation(HAMMERSMASH_ANIM);
               yield PlayState.CONTINUE;
            }
            case "uppercut" -> {
               controller.setAnimation(UPPERCUT_ANIM);
               yield PlayState.CONTINUE;
            }
            case "groundslam" -> {
               controller.setAnimation(GROUNDSLAM_ANIM);
               yield PlayState.CONTINUE;
            }
            case "summon" -> {
               controller.setAnimation(SUMMON_ANIM);
               yield PlayState.CONTINUE;
            }
            case "aoeclose" -> {
               controller.setAnimation(AOE_CLOSE);
               yield PlayState.CONTINUE;
            }
            default -> throw new IllegalStateException("Unexpected value: " + attackMove);
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

   @Override
   public ServerBossEvent getServerBossInfo() {
      return new ServerBossEvent(this.getDisplayName(), BossBarColor.RED, BossBarOverlay.PROGRESS);
   }

   @Override
   public void playAttackSound() {
      this.level.playSound(null, this.blockPosition(), ModSounds.ARTIFACT_BOSS_ATTACK, SoundSource.HOSTILE, 1.0F, 1.0F);
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
               currentStage.finish();
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
