package iskallia.vault.entity.entity;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class VaultHorseEntity extends AbstractHorse {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(VaultHorseEntity.class, EntityDataSerializers.INT);

   public VaultHorseEntity(EntityType<? extends AbstractHorse> entityType, Level world) {
      super(entityType, world);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0, AbstractHorse.class));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(3, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.addBehaviourGoals();
   }

   public static Builder createAttributes() {
      return createBaseHorseAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.ATTACK_DAMAGE, 2.0);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("Variant", this.getTypeVariant());
      if (!this.inventory.getItem(1).isEmpty()) {
         pCompound.put("ArmorItem", this.inventory.getItem(1).save(new CompoundTag()));
      }
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setTypeVariant(pCompound.getInt("Variant"));
      if (pCompound.contains("ArmorItem", 10)) {
         ItemStack itemstack = ItemStack.of(pCompound.getCompound("ArmorItem"));
         if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
            this.inventory.setItem(1, itemstack);
         }
      }

      this.updateContainerEquipment();
   }

   protected void randomizeAttributes() {
      this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
   }

   private void setTypeVariant(int p_30737_) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, p_30737_);
   }

   public int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      if (!this.isTamed()) {
         return InteractionResult.PASS;
      } else if (this.isBaby()) {
         return super.mobInteract(pPlayer, pHand);
      } else if (pPlayer.isSecondaryUseActive()) {
         this.openInventory(pPlayer);
         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else if (this.isVehicle()) {
         return super.mobInteract(pPlayer, pHand);
      } else {
         if (!itemstack.isEmpty()) {
            if (itemstack.is(Items.SADDLE) && !this.isSaddled()) {
               this.openInventory(pPlayer);
               return InteractionResult.sidedSuccess(this.level.isClientSide);
            }

            InteractionResult interactionresult = itemstack.interactLivingEntity(pPlayer, this, pHand);
            if (interactionresult.consumesAction()) {
               return interactionresult;
            }
         }

         this.doPlayerRide(pPlayer);
         return InteractionResult.sidedSuccess(this.level.isClientSide);
      }
   }

   protected void addBehaviourGoals() {
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.HORSE_LAND;
   }

   @Nullable
   protected SoundEvent getEatingSound() {
      return SoundEvents.HORSE_EAT;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      super.getHurtSound(pDamageSource);
      return SoundEvents.HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.HORSE_ANGRY;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor pLevel,
      DifficultyInstance pDifficulty,
      MobSpawnType pReason,
      @org.jetbrains.annotations.Nullable SpawnGroupData pSpawnData,
      @org.jetbrains.annotations.Nullable CompoundTag pDataTag
   ) {
      this.setTypeVariant(pLevel.getRandom().nextInt(2));
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }
}
