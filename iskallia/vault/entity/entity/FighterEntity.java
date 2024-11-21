package iskallia.vault.entity.entity;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.entity.ai.ThrowProjectilesGoal;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.FighterSizeMessage;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.SkinProfile;
import iskallia.vault.world.VaultDifficulty;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.data.WorldSettings;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class FighterEntity extends Zombie {
   public static final ThrowProjectilesGoal.Projectile SNOWBALLS = (world1, shooter) -> new Snowball(world1, shooter) {
      protected void onHitEntity(EntityHitResult raycast) {
         Entity entity = raycast.getEntity();
         if (entity != shooter) {
            int i = entity instanceof Blaze ? 3 : 1;
            entity.hurt(DamageSource.indirectMobAttack(this, shooter), i);
         }
      }
   };
   public SkinProfile skin;
   public String lastName = "Fighter";
   public float sizeMultiplier = 1.0F;
   public ServerBossEvent bossInfo;

   public FighterEntity(EntityType<? extends Zombie> type, Level world) {
      super(type, world);
      if (!this.level.isClientSide) {
         this.changeSize(this.sizeMultiplier);
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.random.nextFloat() * 0.15 + 0.2);
      } else {
         this.skin = new SkinProfile();
      }

      this.bossInfo = new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.PROGRESS);
      this.bossInfo.setDarkenScreen(true);
      this.bossInfo.setVisible(false);
      this.setCustomName(new TextComponent(this.lastName));
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      ServerVaults.get(this.level).ifPresent(this::registerProjectileGoal);
      super.registerGoals();
   }

   private void registerProjectileGoal(Vault vault) {
      VaultDifficulty vaultDifficulty = WorldSettings.get(this.level).getPlayerDifficulty(vault.get(Vault.OWNER));
      if (vaultDifficulty.shouldAddAntiNerdPoleAi()) {
         this.goalSelector.addGoal(3, new ThrowProjectilesGoal<FighterEntity>(this, ModConfigs.FIGHTER.chancerPerTick, 1, FighterEntity.ThrowableBrick::new) {
            @Override
            public void start() {
               super.start();
               this.getEntity().setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.BRICK));
            }

            @Override
            public boolean canUse() {
               return super.canUse() && this.targetOutOfReachAbove();
            }

            @Override
            public boolean canContinueToUse() {
               return super.canContinueToUse() && this.targetOutOfReachAbove();
            }

            private boolean targetOutOfReachAbove() {
               LivingEntity target = FighterEntity.this.getTarget();
               if (target == null) {
                  return false;
               } else {
                  double targetDistance = FighterEntity.this.distanceToSqr(target);
                  double attackReach = this.getAttackReachSqr(target);
                  double yDiff = target.getY() - FighterEntity.this.getY();
                  return targetDistance > attackReach && targetDistance < attackReach * 16.0 && yDiff >= 2.0 && yDiff <= 4.0;
               }
            }

            private double getAttackReachSqr(LivingEntity pAttackTarget) {
               return FighterEntity.this.getBbWidth() * 2.0F * FighterEntity.this.getBbWidth() * 2.0F + pAttackTarget.getBbWidth();
            }
         });
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasSkin() {
      return !this.skin.isEmpty();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSlimSkin() {
      return this.skin.isSlim();
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getLocationSkin() {
      return this.skin.getLocationSkin();
   }

   public boolean isBaby() {
      return false;
   }

   protected boolean isSunSensitive() {
      return false;
   }

   public void tick() {
      super.tick();
      if (!this.dead) {
         if (this.level.isClientSide) {
            String name = this.getCustomName().getString();
            String star = String.valueOf('✦');

            while (name.startsWith(star)) {
               name = name.substring(1);
            }

            name = name.trim();
            if (name.startsWith("[")) {
               String[] data = name.split(Pattern.quote("]"));
               name = data[1].trim();
            }

            if (!this.lastName.equals(name)) {
               this.skin.updateSkin(name);
               this.lastName = name;
            }
         } else {
            double amplitude = this.getDeltaMovement().distanceToSqr(0.0, this.getDeltaMovement().y(), 0.0);
            if (amplitude > 0.004) {
               this.setSprinting(true);
            } else {
               this.setSprinting(false);
            }

            this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
         }
      }
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

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putFloat("SizeMultiplier", this.sizeMultiplier);
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.contains("SizeMultiplier", 5)) {
         this.changeSize(compound.getFloat("SizeMultiplier"));
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

   public FighterEntity changeSize(float m) {
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

   protected void doUnderWaterConversion() {
   }

   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnData, CompoundTag dataTag
   ) {
      this.setCustomName(this.getCustomName());
      this.setCanBreakDoors(true);
      this.setCanPickUpLoot(true);
      if (this.random.nextInt(100) == 0) {
         Chicken chicken = (Chicken)EntityType.CHICKEN.create(this.level);
         chicken.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
         chicken.finalizeSpawn(world, difficulty, reason, spawnData, dataTag);
         chicken.setChickenJockey(true);
         ((ServerLevel)this.level).addWithUUID(chicken);
         this.startRiding(chicken);
      }

      return spawnData;
   }

   protected void dropFromLootTable(DamageSource damageSource, boolean attackedRecently) {
      super.dropFromLootTable(damageSource, attackedRecently);
      if (!this.level.isClientSide()) {
         ;
      }
   }

   public boolean doHurtTarget(Entity entity) {
      if (!this.level.isClientSide) {
         ((ServerLevel)this.level).sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
         this.level
            .playSound(
               null, this.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, this.random.nextFloat() - this.random.nextFloat()
            );
      }

      return super.doHurtTarget(entity);
   }

   public static class ThrowableBrick extends ThrowableItemProjectile {
      @Nullable
      private LivingEntity thrower;

      public ThrowableBrick(EntityType<FighterEntity.ThrowableBrick> entityType, Level level) {
         super(entityType, level);
      }

      public ThrowableBrick(Level level, LivingEntity thrower) {
         super(ModEntities.BRICK, thrower, level);
         this.thrower = thrower;
      }

      protected Item getDefaultItem() {
         return Items.BRICK;
      }

      protected void onHitEntity(EntityHitResult result) {
         if (!this.level.isClientSide()) {
            if (result.getEntity() instanceof LivingEntity livingEntity) {
               if (this.thrower == null) {
                  this.discard();
                  return;
               }

               double xRatio = this.thrower.getX() - livingEntity.getX();

               double zRatio;
               for (zRatio = this.thrower.getZ() - livingEntity.getZ();
                  xRatio * xRatio + zRatio * zRatio < 1.0E-4;
                  zRatio = (Math.random() - Math.random()) * 0.01
               ) {
                  xRatio = (Math.random() - Math.random()) * 0.01;
               }

               livingEntity.hurtMarked = true;
               livingEntity.knockback(ModConfigs.FIGHTER.knockback, xRatio, zRatio);
            }
         }
      }
   }
}
