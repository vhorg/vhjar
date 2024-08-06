package iskallia.vault.entity.boss;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.entity.VaultBoss;
import iskallia.vault.entity.boss.attack.BossAttackMove;
import iskallia.vault.entity.boss.attack.MeleeAttacks;
import iskallia.vault.world.data.ServerVaults;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public abstract class VaultBossBaseEntity extends Monster implements VaultBoss {
   public static final EntityDataSerializer<Optional<BossAttackMove>> OPTIONAL_ATTACK_MOVE = new EntityDataSerializer<Optional<BossAttackMove>>() {
      public void write(FriendlyByteBuf buf, Optional<BossAttackMove> value) {
         if (value.isPresent()) {
            buf.writeBoolean(true);
            buf.writeEnum(value.get());
         } else {
            buf.writeBoolean(false);
         }
      }

      public Optional<BossAttackMove> read(FriendlyByteBuf buf) {
         return buf.readBoolean() ? Optional.of((BossAttackMove)buf.readEnum(BossAttackMove.class)) : Optional.empty();
      }

      public Optional<BossAttackMove> copy(Optional<BossAttackMove> value) {
         return value;
      }
   };
   protected static final EntityDataAccessor<Optional<BossAttackMove>> ACTIVE_ATTACK_MOVE = SynchedEntityData.defineId(
      VaultBossBaseEntity.class, OPTIONAL_ATTACK_MOVE
   );
   protected Vec3 spawnPosition;

   public VaultBossBaseEntity(EntityType<? extends Monster> type, Level level) {
      super(type, level);
   }

   public Vec3 getSpawnPosition() {
      return this.spawnPosition;
   }

   public int getPlayerCount() {
      return this.level instanceof ServerLevel ? ServerVaults.get(this.level).map(vault -> vault.get(Vault.LISTENERS).getAll().size()).orElse(0) : 1;
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

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ACTIVE_ATTACK_MOVE, Optional.empty());
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag
   ) {
      this.spawnPosition = this.position();
      return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
   }

   public void setActiveAttackMove(@Nullable BossAttackMove attackMove) {
      this.entityData.set(ACTIVE_ATTACK_MOVE, Optional.ofNullable(attackMove));
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

   protected Optional<BossAttackMove> getActiveAttackMove() {
      return (Optional<BossAttackMove>)this.entityData.get(ACTIVE_ATTACK_MOVE);
   }

   public abstract WeightedList<MeleeAttacks.AttackData> getMeleeAttacks();

   public abstract WeightedList<MeleeAttacks.AttackData> getRageAttacks();

   public final Vec3 calculateViewVector(float adjustedYaw) {
      return this.calculateViewVector(this.getViewXRot(1.0F), adjustedYaw);
   }

   public void setSpawnPosition(Vec3 position) {
      this.spawnPosition = position;
   }

   public abstract double getAttackReach();
}
