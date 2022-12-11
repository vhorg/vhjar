package iskallia.vault.entity.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfig;

public class VaultGummySoldier extends Monster {
   private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(
      SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5, Operation.MULTIPLY_BASE
   );
   private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(VaultGummySoldier.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_SPECIAL_TYPE_ID = SynchedEntityData.defineId(VaultGummySoldier.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_DROWNED_CONVERSION_ID = SynchedEntityData.defineId(
      VaultGummySoldier.class, EntityDataSerializers.BOOLEAN
   );
   private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE = p_34284_ -> p_34284_ == Difficulty.HARD;
   private final BreakDoorGoal breakDoorGoal = new BreakDoorGoal(this, DOOR_BREAKING_PREDICATE);
   private boolean canBreakDoors;

   public VaultGummySoldier(EntityType<? extends VaultGummySoldier> p_34271_, Level p_34272_) {
      super(p_34271_, p_34272_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.addBehaviourGoals();
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new VaultGummySoldier.GummyAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[]{ZombifiedPiglin.class}));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 35.0)
         .add(Attributes.MOVEMENT_SPEED, 0.23F)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ARMOR, 2.0);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_BABY_ID, false);
      this.getEntityData().define(DATA_SPECIAL_TYPE_ID, 0);
      this.getEntityData().define(DATA_DROWNED_CONVERSION_ID, false);
   }

   public boolean canBreakDoors() {
      return this.canBreakDoors;
   }

   public void setCanBreakDoors(boolean pEnabled) {
      if (this.supportsBreakDoorGoal() && GoalUtils.hasGroundPathNavigation(this)) {
         if (this.canBreakDoors != pEnabled) {
            this.canBreakDoors = pEnabled;
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(pEnabled);
            if (pEnabled) {
               this.goalSelector.addGoal(1, this.breakDoorGoal);
            } else {
               this.goalSelector.removeGoal(this.breakDoorGoal);
            }
         }
      } else if (this.canBreakDoors) {
         this.goalSelector.removeGoal(this.breakDoorGoal);
         this.canBreakDoors = false;
      }
   }

   protected boolean supportsBreakDoorGoal() {
      return true;
   }

   public boolean isBaby() {
      return (Boolean)this.getEntityData().get(DATA_BABY_ID);
   }

   protected int getExperienceReward(Player pPlayer) {
      if (this.isBaby()) {
         this.xpReward = (int)(this.xpReward * 2.5);
      }

      return super.getExperienceReward(pPlayer);
   }

   public void setBaby(boolean pChildZombie) {
      this.getEntityData().set(DATA_BABY_ID, pChildZombie);
      if (!this.level.isClientSide) {
         AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
         attributeinstance.removeModifier(SPEED_MODIFIER_BABY);
         if (pChildZombie) {
            attributeinstance.addTransientModifier(SPEED_MODIFIER_BABY);
         }
      }
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (DATA_BABY_ID.equals(pKey)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   public void tick() {
      super.tick();
   }

   public void aiStep() {
      if (this.isAlive()) {
         boolean flag = this.isSunSensitive() && this.isSunBurnTick();
         if (flag) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty()) {
               if (itemstack.isDamageableItem()) {
                  itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
                  if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                     this.broadcastBreakEvent(EquipmentSlot.HEAD);
                     this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                  }
               }

               flag = false;
            }

            if (flag) {
               this.setSecondsOnFire(8);
            }
         }
      }

      super.aiStep();
   }

   protected boolean isSunSensitive() {
      return false;
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return !super.hurt(pSource, pAmount) ? false : this.level instanceof ServerLevel;
   }

   public boolean doHurtTarget(Entity pEntity) {
      boolean flag = super.doHurtTarget(pEntity);
      if (flag) {
         float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
         if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
            pEntity.setSecondsOnFire(2 * (int)f);
         }
      }

      return flag;
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   public SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.SLIME_ATTACK;
   }

   public SoundEvent getDeathSound() {
      return SoundEvents.SLIME_DEATH;
   }

   protected SoundEvent getStepSound() {
      return this.isBaby() ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
   }

   protected SoundEvent getAttackSound() {
      return SoundEvents.SLIME_ATTACK;
   }

   protected void playStepSound(BlockPos pPos, BlockState pBlock) {
      this.playSound(this.getStepSound(), 0.4F, 1.0F);
   }

   public MobType getMobType() {
      return MobType.UNDEFINED;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance pDifficulty) {
      super.populateDefaultEquipmentSlots(pDifficulty);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("IsBaby", this.isBaby());
      pCompound.putBoolean("CanBreakDoors", this.canBreakDoors());
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setBaby(pCompound.getBoolean("IsBaby"));
      this.setCanBreakDoors(pCompound.getBoolean("CanBreakDoors"));
   }

   public void killed(ServerLevel pLevel, LivingEntity pKilledEntity) {
      super.killed(pLevel, pKilledEntity);
   }

   protected float getStandingEyeHeight(Pose pPose, EntityDimensions pSize) {
      return this.isBaby() ? 0.93F : 1.74F;
   }

   public boolean canHoldItem(ItemStack pStack) {
      return (!pStack.is(Items.EGG) || !this.isBaby() || !this.isPassenger()) && super.canHoldItem(pStack);
   }

   public boolean wantsToPickUp(ItemStack p_182400_) {
      return !p_182400_.is(Items.GLOW_INK_SAC) && super.wantsToPickUp(p_182400_);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag
   ) {
      pSpawnData = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
      float f = pDifficulty.getSpecialMultiplier();
      this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * f);
      if (pSpawnData == null) {
         pSpawnData = new VaultGummySoldier.GummyGroupData(getSpawnAsBabyOdds(pLevel.getRandom()), true);
      }

      if (pSpawnData instanceof VaultGummySoldier.GummyGroupData groupData) {
         if (groupData.isBaby) {
            this.setBaby(true);
            if (groupData.canSpawnJockey) {
               if (pLevel.getRandom().nextFloat() < 0.05) {
                  List<Chicken> list = pLevel.getEntitiesOfClass(
                     Chicken.class, this.getBoundingBox().inflate(5.0, 3.0, 5.0), EntitySelector.ENTITY_NOT_BEING_RIDDEN
                  );
                  if (!list.isEmpty()) {
                     Chicken chicken = list.get(0);
                     chicken.setChickenJockey(true);
                     this.startRiding(chicken);
                  }
               } else if (pLevel.getRandom().nextFloat() < 0.05) {
                  Chicken chicken1 = (Chicken)EntityType.CHICKEN.create(this.level);
                  chicken1.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                  chicken1.finalizeSpawn(pLevel, pDifficulty, MobSpawnType.JOCKEY, (SpawnGroupData)null, (CompoundTag)null);
                  chicken1.setChickenJockey(true);
                  this.startRiding(chicken1);
                  pLevel.addFreshEntity(chicken1);
               }
            }
         }

         this.setCanBreakDoors(this.supportsBreakDoorGoal() && this.random.nextFloat() < f * 0.1F);
         this.populateDefaultEquipmentSlots(pDifficulty);
         this.populateDefaultEquipmentEnchantments(pDifficulty);
      }

      if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
         LocalDate localdate = LocalDate.now();
         int i = localdate.get(ChronoField.DAY_OF_MONTH);
         int j = localdate.get(ChronoField.MONTH_OF_YEAR);
         if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
         }
      }

      return pSpawnData;
   }

   public static boolean getSpawnAsBabyOdds(Random p_34303_) {
      return p_34303_.nextFloat() < (Double)ForgeConfig.SERVER.zombieBabyChance.get();
   }

   public double getMyRidingOffset() {
      return this.isBaby() ? 0.0 : -0.45;
   }

   public class GummyAttackGoal extends MeleeAttackGoal {
      private final VaultGummySoldier vaultGummySoldier;
      private int raiseArmTicks;

      public GummyAttackGoal(VaultGummySoldier vaultGummySoldier, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
         super(vaultGummySoldier, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
         this.vaultGummySoldier = vaultGummySoldier;
      }

      protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
         double d0 = this.getAttackReachSqr(pEnemy);
         if (pDistToEnemySqr <= d0 && this.getTicksUntilNextAttack() <= 0) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(pEnemy);
            VaultGummySoldier.this.playSound(VaultGummySoldier.this.getAttackSound(), 1.0F, 1.0F);
         }
      }

      public void start() {
         super.start();
         this.raiseArmTicks = 0;
      }

      public void stop() {
         super.stop();
         this.vaultGummySoldier.setAggressive(false);
      }

      public void tick() {
         super.tick();
         this.raiseArmTicks++;
         if (this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2) {
            this.vaultGummySoldier.setAggressive(true);
         } else {
            this.vaultGummySoldier.setAggressive(false);
         }
      }
   }

   public static class GummyGroupData implements SpawnGroupData {
      public final boolean isBaby;
      public final boolean canSpawnJockey;

      public GummyGroupData(boolean p_34357_, boolean p_34358_) {
         this.isBaby = p_34357_;
         this.canSpawnJockey = p_34358_;
      }
   }
}
