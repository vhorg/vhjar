package iskallia.vault.entity.entity;

import iskallia.vault.core.Version;
import iskallia.vault.core.data.key.LootTableKey;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.loot.generator.LootTableGenerator;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.SkinProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class AncientCopperGolemEntity extends PathfinderMob {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(AncientCopperGolemEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<CompoundTag> DATA_POSING = SynchedEntityData.defineId(
      AncientCopperGolemEntity.class, EntityDataSerializers.COMPOUND_TAG
   );
   private static final EntityDataAccessor<Boolean> DATA_WAXED = SynchedEntityData.defineId(AncientCopperGolemEntity.class, EntityDataSerializers.BOOLEAN);
   private static final int MAX_AGE = 800;
   private int age = 800;
   public String lastName = "";
   public SkinProfile skin;

   public AncientCopperGolemEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
      super(entityType, world);
      if (world.isClientSide) {
         this.skin = new SkinProfile();
      }

      this.setAge(ModConfigs.ANCIENT_COPPER_GOLEM.degradeTime);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, Ingredient.of(new ItemLike[]{ModItems.ANCIENT_COPPER_INGOT}), false));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.addBehaviourGoals();
   }

   protected void addBehaviourGoals() {
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 35.0)
         .add(Attributes.MOVEMENT_SPEED, 0.18F)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.ARMOR, 2.0);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
      this.entityData.define(DATA_POSING, new CompoundTag());
      this.entityData.define(DATA_WAXED, false);
   }

   public void setPosing(Optional<CompoundTag> pose) {
      CompoundTag tag = new CompoundTag();
      pose.ifPresent(oTag -> tag.put("pose", oTag));
      this.entityData.set(DATA_POSING, tag);
   }

   public Optional<CompoundTag> getPosing() {
      CompoundTag tag = (CompoundTag)this.entityData.get(DATA_POSING);
      return tag.contains("pose") ? Optional.of(tag.getCompound("pose")) : Optional.empty();
   }

   public void loadAngles(CompoundTag tag) {
      this.setXRot(tag.getFloat("xRot"));
      this.setYHeadRot(tag.getFloat("yHeadRot"));
      this.setYBodyRot(tag.getFloat("yBodyRot"));
      this.yHeadRotO = this.getYHeadRot();
      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
      this.animationPosition = tag.getFloat("animationPosition");
      this.animationSpeed = tag.getFloat("animationSpeed");
      this.animationSpeedOld = this.animationSpeed;
   }

   public CompoundTag saveAngles() {
      CompoundTag tag = new CompoundTag();
      tag.putFloat("xRot", this.getXRot());
      tag.putFloat("yHeadRot", this.getYHeadRot());
      tag.putFloat("yBodyRot", this.yBodyRot);
      tag.putFloat("animationPosition", this.animationPosition);
      tag.putFloat("animationSpeed", this.animationSpeed);
      return tag;
   }

   public CompoundTag saveAnglesWithBodyRot(float bodyRot) {
      CompoundTag tag = new CompoundTag();
      float diff = this.yBodyRot - bodyRot;
      float xRot = this.getXRot();
      tag.putFloat("xRot", xRot);
      tag.putFloat("yHeadRot", this.getYHeadRot() - diff);
      tag.putFloat("yBodyRot", bodyRot);
      tag.putFloat("animationPosition", this.animationPosition);
      tag.putFloat("animationSpeed", this.animationSpeed);
      return tag;
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("Variant", this.getTypeVariant());
      pCompound.putBoolean("Waxed", this.getWaxed());
      if (this.getPosing().isPresent()) {
         pCompound.put("Pose", (Tag)this.getPosing().get());
      }

      pCompound.putInt("Age", this.getAge());
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setTypeVariant(pCompound.getInt("Variant"));
      this.setWaxed(pCompound.getBoolean("Waxed"));
      if (pCompound.contains("Pose")) {
         this.setPosing(Optional.of(pCompound.getCompound("Pose")));
      }

      this.setAge(pCompound.getInt("Age"));
   }

   private void setTypeVariant(int p_30737_) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, p_30737_);
   }

   public int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   private void setAge(int age) {
      this.age = age;
   }

   public int getAge() {
      return this.age;
   }

   public boolean getWaxed() {
      return (Boolean)this.entityData.get(DATA_WAXED);
   }

   public void setWaxed(boolean waxed) {
      this.entityData.set(DATA_WAXED, waxed);
   }

   public float getSpeed() {
      return this.getWaxed() ? 0.0F : super.getSpeed();
   }

   protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      if (itemstack.isEmpty()) {
         if (pHand == InteractionHand.MAIN_HAND && this.getWaxed()) {
            if (!this.level.isClientSide) {
               Vec3 vec3 = pPlayer.position().subtract(this.position());
               this.setPosing(Optional.of(this.saveAnglesWithBodyRot((float)(-(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI)))));
            }

            return InteractionResult.sidedSuccess(this.level.isClientSide);
         } else {
            return InteractionResult.PASS;
         }
      } else if (itemstack.is(Items.HONEYCOMB)) {
         if (this.getWaxed()) {
            return InteractionResult.PASS;
         } else {
            if (!this.level.isClientSide) {
               this.setWaxed(true);
               if (this.level instanceof ServerLevel serverLevel) {
                  serverLevel.sendParticles(
                     ParticleTypes.WAX_ON,
                     this.position().x + 0.3F,
                     this.position().y + this.getBbHeight() / 2.0F,
                     this.position().z,
                     5,
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.05, 0.05),
                     1.0
                  );
                  serverLevel.sendParticles(
                     ParticleTypes.WAX_ON,
                     this.position().x - 0.3F,
                     this.position().y + this.getBbHeight() / 2.0F,
                     this.position().z,
                     5,
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.05, 0.05),
                     1.0
                  );
                  serverLevel.sendParticles(
                     ParticleTypes.WAX_ON,
                     this.position().x,
                     this.position().y + this.getBbHeight() / 2.0F,
                     this.position().z - 0.3F,
                     5,
                     Mth.nextDouble(this.level.random, -0.05, 0.05),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     1.0
                  );
                  serverLevel.sendParticles(
                     ParticleTypes.WAX_ON,
                     this.position().x,
                     this.position().y + this.getBbHeight() / 2.0F,
                     this.position().z + 0.3F,
                     5,
                     Mth.nextDouble(this.level.random, -0.05, 0.05),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     1.0
                  );
               }

               this.level.playSound(null, this, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return InteractionResult.sidedSuccess(this.level.isClientSide);
         }
      } else if (itemstack.getItem() instanceof AxeItem) {
         if (!this.getWaxed()) {
            return InteractionResult.PASS;
         } else {
            if (!this.level.isClientSide) {
               this.setWaxed(false);
               this.setPosing(Optional.empty());
               if (this.level instanceof ServerLevel serverLevel) {
                  serverLevel.sendParticles(
                     ParticleTypes.WAX_OFF,
                     this.position().x + 0.3F,
                     this.position().y + this.getBbHeight() / 2.0F,
                     this.position().z,
                     5,
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.05, 0.05),
                     1.0
                  );
                  serverLevel.sendParticles(
                     ParticleTypes.WAX_OFF,
                     this.position().x - 0.3F,
                     this.position().y + this.getBbHeight() / 2.0F,
                     this.position().z,
                     5,
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.05, 0.05),
                     1.0
                  );
                  serverLevel.sendParticles(
                     ParticleTypes.WAX_OFF,
                     this.position().x,
                     this.position().y + this.getBbHeight() / 2.0F,
                     this.position().z - 0.3F,
                     5,
                     Mth.nextDouble(this.level.random, -0.05, 0.05),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     1.0
                  );
                  serverLevel.sendParticles(
                     ParticleTypes.WAX_OFF,
                     this.position().x,
                     this.position().y + this.getBbHeight() / 2.0F,
                     this.position().z + 0.3F,
                     5,
                     Mth.nextDouble(this.level.random, -0.05, 0.05),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     Mth.nextDouble(this.level.random, -0.25, 0.25),
                     1.0
                  );
               }

               this.level.playSound(null, this, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return InteractionResult.sidedSuccess(this.level.isClientSide);
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide()) {
         if (this.getWaxed()) {
            if (this.getPosing().isEmpty()) {
               this.setPosing(Optional.of(this.saveAngles()));
            }

            this.getPosing().ifPresent(this::loadAngles);
         } else if (this.getAge() > 0) {
            this.age--;
            if (this.getAge() == 0) {
               if (this.getTypeVariant() < 3) {
                  this.setTypeVariant(this.getTypeVariant() + 1);
                  if (this.getTypeVariant() == 2) {
                     this.setAge(ModConfigs.ANCIENT_COPPER_GOLEM.degradeTime);
                  } else {
                     this.setAge(ModConfigs.ANCIENT_COPPER_GOLEM.finalDegradeTime);
                  }
               } else {
                  this.kill();
               }
            }
         }
      } else {
         if (this.getWaxed()) {
            this.getPosing().ifPresent(this::loadAngles);
         }

         if (this.hasCustomName() && this.getCustomName() != null) {
            String name = this.getCustomName().getString();
            if (!this.lastName.equals(name)) {
               this.skin.updateSkin(name);
               this.lastName = name;
            }
         }
      }
   }

   protected void dropFromLootTable(DamageSource pDamageSource, boolean pAttackedRecently) {
      super.dropFromLootTable(pDamageSource, pAttackedRecently);
      if (!this.level.isClientSide()) {
         if (this.level instanceof ServerLevel sWorld && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ResourceLocation loc = new ResourceLocation(ModConfigs.ANCIENT_COPPER_GOLEM.LOOT_TABLES.get(this.getOxidizationLevel()));
            List<ItemStack> loot = new ArrayList<>();
            LootTableKey key = VaultRegistry.LOOT_TABLE.getKey(loc);
            if (key != null) {
               LootTableGenerator generator = new LootTableGenerator(Version.latest(), key, 0.0F);
               generator.generate(JavaRandom.ofNanoTime());
               generator.getItems().forEachRemaining(loot::add);
            }

            if (this.level instanceof ServerLevel) {
               loot.forEach(stack -> popResource(this.level, this.position(), stack));
            }

            if (this.hasCustomName() && this.getCustomName() != null) {
               ItemStack headDrop = new ItemStack(Items.PLAYER_HEAD, 1);
               CompoundTag nbt = new CompoundTag();
               nbt.putString("SkullOwner", this.getCustomName().getString());
               headDrop.setTag(nbt);
               ItemEntity entity = new ItemEntity(this.level, this.position().x(), this.position().y(), this.position().z(), headDrop);
               this.level.addFreshEntity(entity);
            }
         }
      }
   }

   public static void popResource(Level pLevel, Vec3 pPos, ItemStack pStack) {
      float f = EntityType.ITEM.getHeight() / 2.0F;
      double d0 = (float)pPos.x() + Mth.nextDouble(pLevel.random, -0.25, 0.25);
      double d1 = (float)pPos.y() + Mth.nextDouble(pLevel.random, -0.25, 0.25) - f;
      double d2 = (float)pPos.z() + Mth.nextDouble(pLevel.random, -0.25, 0.25);
      popResource(pLevel, (Supplier<ItemEntity>)(() -> new ItemEntity(pLevel, d0, d1, d2, pStack)), pStack);
   }

   private static void popResource(Level pLevel, Supplier<ItemEntity> pItemEntitySupplier, ItemStack pStack) {
      if (!pLevel.isClientSide && !pStack.isEmpty() && pLevel.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !pLevel.restoringBlockSnapshots) {
         ItemEntity itementity = pItemEntitySupplier.get();
         itementity.setDefaultPickUpDelay();
         pLevel.addFreshEntity(itementity);
      }
   }

   public String getOxidizationLevel() {
      return switch (this.getTypeVariant()) {
         case 1 -> "Exposed";
         case 2 -> "Weathered";
         case 3 -> "Oxidized";
         default -> "Copper";
      };
   }

   public boolean isBaby() {
      return false;
   }

   public void setBaby(boolean pChildZombie) {
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
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
      this.setAge(ModConfigs.ANCIENT_COPPER_GOLEM.degradeTime);
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }
}
