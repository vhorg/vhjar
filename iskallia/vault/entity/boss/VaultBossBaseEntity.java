package iskallia.vault.entity.boss;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.entity.VaultBoss;
import iskallia.vault.entity.boss.attack.IMeleeAttack;
import iskallia.vault.world.data.ServerVaults;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public abstract class VaultBossBaseEntity extends Monster implements VaultBoss {
   protected static final EntityDataAccessor<String> ACTIVE_ATTACK_MOVE = SynchedEntityData.defineId(VaultBossBaseEntity.class, EntityDataSerializers.STRING);
   protected Vec3 spawnPosition;

   public abstract Map<String, BiFunction<VaultBossBaseEntity, Double, IMeleeAttack>> getMeleeAttackFactories();

   public VaultBossBaseEntity(EntityType<? extends Monster> type, Level level) {
      super(type, level);
   }

   public Vec3 getSpawnPosition() {
      return this.spawnPosition;
   }

   public int getPlayerCount() {
      return this.level instanceof ServerLevel ? ServerVaults.get(this.level).map(vault -> vault.get(Vault.LISTENERS).getAll().size()).orElse(1) : 1;
   }

   public int getPlayerAdjustedRandomCount(int minCount, int maxCount, float additionalPlayersRatio) {
      int playerCount = this.getPlayerCount();
      return this.getLevel()
         .getRandom()
         .nextInt(
            (int)(minCount + (playerCount - 1) * minCount * additionalPlayersRatio),
            (int)(maxCount + (playerCount - 1) * maxCount * additionalPlayersRatio) + 1
         );
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ACTIVE_ATTACK_MOVE, "");
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag
   ) {
      this.spawnPosition = this.position();
      return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
   }

   public void setActiveAttackMove(String attackMove) {
      this.entityData.set(ACTIVE_ATTACK_MOVE, attackMove);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.put("SpawnPosition", this.serializeSpawnPosition());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.spawnPosition = this.deserializeSpawnPosition(compound.getCompound("SpawnPosition"));
   }

   private Vec3 deserializeSpawnPosition(CompoundTag spawnPosition) {
      return new Vec3(spawnPosition.getDouble("X"), spawnPosition.getDouble("Y"), spawnPosition.getDouble("Z"));
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

   public Optional<String> getActiveAttackMove() {
      String attackMove = (String)this.entityData.get(ACTIVE_ATTACK_MOVE);
      return attackMove.isBlank() ? Optional.empty() : Optional.of(attackMove);
   }

   public boolean canAttack(LivingEntity target) {
      return (target instanceof Player || target instanceof AbstractVillager) && super.canAttack(target);
   }

   public abstract WeightedList<VaultBossBaseEntity.AttackData> getMeleeAttacks();

   public abstract WeightedList<VaultBossBaseEntity.AttackData> getRageAttacks();

   public final Vec3 calculateViewVector(float adjustedYaw) {
      return this.calculateViewVector(this.getViewXRot(1.0F), adjustedYaw);
   }

   public void setSpawnPosition(Vec3 position) {
      this.spawnPosition = position;
   }

   public abstract double getAttackReach();

   public void playAttackSound() {
   }

   public record AttackData(String name, double multiplier) {
      public static VaultBossBaseEntity.AttackData from(CompoundTag tag) {
         return new VaultBossBaseEntity.AttackData(tag.getString("Name"), tag.getDouble("Multiplier"));
      }

      public void serializeTo(CompoundTag tag) {
         tag.putString("Name", this.name);
         tag.putDouble("Multiplier", this.multiplier);
      }
   }
}
