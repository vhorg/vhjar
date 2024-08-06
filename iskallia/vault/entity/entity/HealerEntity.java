package iskallia.vault.entity.entity;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.ClientboundHealSpellParticleMessage;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.monster.AbstractIllager.IllagerArmPose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import org.jetbrains.annotations.Nullable;

public class HealerEntity extends AbstractIllager {
   private static final EntityDataAccessor<Boolean> IS_SPELLCASTING = SynchedEntityData.defineId(SpellcasterIllager.class, EntityDataSerializers.BOOLEAN);
   private int spellCastingTickCount;

   public HealerEntity(EntityType<? extends AbstractIllager> entityType, Level level) {
      super(entityType, level);
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.5)
         .add(Attributes.FOLLOW_RANGE, 18.0)
         .add(Attributes.MAX_HEALTH, 32.0)
         .add(ModAttributes.HEAL_RANGE, 15.0)
         .add(ModAttributes.HEAL_AMOUNT, 6.0);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new HealerEntity.HealerSpellGoal(this));
      this.goalSelector.addGoal(2, new HealerEntity.FollowHurtMonsterGoal(this, 1.0, 5.0F, 25.0F));
      this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[]{Raider.class}).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true).setUnseenMemoryTicks(300));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(IS_SPELLCASTING, false);
   }

   public void readAdditionalSaveData(CompoundTag nbt) {
      super.readAdditionalSaveData(nbt);
      this.spellCastingTickCount = nbt.getInt("SpellTicks");
   }

   public IllagerArmPose getArmPose() {
      if (this.isCastingSpell()) {
         return IllagerArmPose.SPELLCASTING;
      } else {
         return this.isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.CROSSED;
      }
   }

   public boolean isCastingSpell() {
      return this.level.isClientSide ? (Boolean)this.entityData.get(IS_SPELLCASTING) : this.spellCastingTickCount > 0;
   }

   public void startCastingSpell() {
      this.entityData.set(IS_SPELLCASTING, true);
   }

   public void stopCastingSpell() {
      this.entityData.set(IS_SPELLCASTING, false);
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide && this.isCastingSpell()) {
         double red = 0.625;
         double green = 0.0;
         double blue = 0.0;
         float f = this.yBodyRot * (float) (Math.PI / 180.0) + Mth.cos(this.tickCount * 0.6662F) * 0.25F;
         float f1 = Mth.cos(f);
         float f2 = Mth.sin(f);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + f1 * 0.6, this.getY() + 1.8, this.getZ() + f2 * 0.6, red, green, blue);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - f1 * 0.6, this.getY() + 1.8, this.getZ() - f2 * 0.6, red, green, blue);
      }
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.spellCastingTickCount > 0) {
         this.spellCastingTickCount--;
      }
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("SpellTicks", this.spellCastingTickCount);
   }

   private SoundEvent getCastingSoundEvent() {
      return ModSounds.HEAL;
   }

   public void applyRaidBuffs(int raidWave, boolean iDontSeemToBeUsedForAnything) {
   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.ILLUSIONER_AMBIENT;
   }

   private class FollowHurtMonsterGoal extends FollowMobGoal {
      public FollowHurtMonsterGoal(Mob pMob, double pSpeedModifier, float pStopDistance, float pAreaSize) {
         super(pMob, pSpeedModifier, pStopDistance, pAreaSize);
         this.followPredicate = mob -> mob instanceof Monster && mob.getHealth() < mob.getMaxHealth();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.followingMob.getHealth() < this.followingMob.getMaxHealth();
      }
   }

   private class HealerSpellGoal extends Goal {
      public static final int CASTING_TIME = 100;
      public static final int CASTING_INTERVAL = 120;
      private final HealerEntity healerEntity;
      protected int healWarmupDelay;
      protected int nextAttackTickCount;

      public HealerSpellGoal(HealerEntity healerEntity) {
         this.healerEntity = healerEntity;
      }

      public boolean canUse() {
         return !HealerEntity.this.isCastingSpell() && !this.getEntitiesToHeal().isEmpty() ? HealerEntity.this.tickCount >= this.nextAttackTickCount : false;
      }

      public boolean canContinueToUse() {
         return this.healWarmupDelay > 0;
      }

      public void start() {
         this.healWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
         HealerEntity.this.spellCastingTickCount = this.getCastingTime();
         this.nextAttackTickCount = HealerEntity.this.tickCount + this.getCastingInterval();
         SoundEvent soundevent = this.getSpellPrepareSound();
         if (soundevent != null) {
            HealerEntity.this.playSound(soundevent, 1.0F, 1.0F);
         }

         HealerEntity.this.startCastingSpell();
      }

      public void stop() {
         HealerEntity.this.stopCastingSpell();
      }

      public void tick() {
         this.healWarmupDelay--;
         if (this.healWarmupDelay == 0) {
            this.performSpellCasting();
            HealerEntity.this.playSound(HealerEntity.this.getCastingSoundEvent(), 0.5F, 0.6F + HealerEntity.this.level.random.nextFloat(0.2F));
         }
      }

      protected void performSpellCasting() {
         List<Monster> entitiesToHeal = this.getEntitiesToHeal();
         entitiesToHeal.forEach(entity -> entity.heal((float)this.healerEntity.getAttributeValue(ModAttributes.HEAL_AMOUNT)));
         ClientboundHealSpellParticleMessage message = new ClientboundHealSpellParticleMessage(
            this.healerEntity.getX(), this.healerEntity.getY() + 1.0, this.healerEntity.getZ(), entitiesToHeal.stream().<Integer>map(Entity::getId).toList()
         );
         ModNetwork.CHANNEL
            .send(
               PacketDistributor.NEAR
                  .with(
                     () -> new TargetPoint(
                        this.healerEntity.getX(), this.healerEntity.getY(), this.healerEntity.getZ(), 64.0, HealerEntity.this.level.dimension()
                     )
                  ),
               message
            );
      }

      private List<Monster> getEntitiesToHeal() {
         return this.healerEntity
            .getLevel()
            .getEntities(
               this.healerEntity,
               this.healerEntity.getBoundingBox().inflate(this.healerEntity.getAttributeValue(ModAttributes.HEAL_RANGE)),
               Monster.class::isInstance
            )
            .stream()
            .map(Monster.class::cast)
            .filter(e -> e.getHealth() < e.getMaxHealth())
            .toList();
      }

      protected int getCastWarmupTime() {
         return 20;
      }

      protected int getCastingTime() {
         return 100;
      }

      protected int getCastingInterval() {
         return 120;
      }

      @Nullable
      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
      }
   }
}
