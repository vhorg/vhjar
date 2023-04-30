package iskallia.vault.entity.entity.mushroom;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class MushroomEntity extends Monster {
   private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(MushroomEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(MushroomEntity.class, EntityDataSerializers.INT);
   private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("b3d3f1c0-c290-48e2-a4d9-58de72f73b8a");
   private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(
      SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5, Operation.MULTIPLY_BASE
   );

   public MushroomEntity(EntityType<? extends Monster> entityType, Level world) {
      super(entityType, world);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.addBehaviourGoals();
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[]{ZombifiedPiglin.class}));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public abstract int getTier();

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 35.0)
         .add(Attributes.MOVEMENT_SPEED, 0.23F)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ARMOR, 2.0)
         .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_BABY_ID, false);
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("Variant", this.getTypeVariant());
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setTypeVariant(pCompound.getInt("Variant"));
   }

   private void setTypeVariant(int p_30737_) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, p_30737_);
   }

   public int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public boolean isBaby() {
      return (Boolean)this.getEntityData().get(DATA_BABY_ID);
   }

   public void setBaby(boolean pChildZombie) {
      this.getEntityData().set(DATA_BABY_ID, pChildZombie);
      if (!this.level.isClientSide) {
         AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);

         assert attributeinstance != null;

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
