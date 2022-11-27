package iskallia.vault.world.vault.logic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import iskallia.vault.entity.LegacyEntityScaler;
import iskallia.vault.entity.entity.AggressiveCowEntity;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.util.gson.IgnoreEmpty;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.task.IVaultTask;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultSpawner implements INBTSerializable<CompoundTag>, IVaultTask {
   private VaultSpawner.Config config = new VaultSpawner.Config();
   private VaultSpawner.Config oldConfig = new VaultSpawner.Config();
   private VMapNBT<Integer, VaultSpawner.Config> configHistory = VMapNBT.ofInt(new LinkedHashMap(), VaultSpawner.Config::new);
   private VListNBT<UUID, StringTag> spawnedMobIds = VListNBT.ofUUID();

   public VaultSpawner.Config getConfig() {
      return this.config;
   }

   public VaultSpawner configure(VaultSpawner.Config config) {
      return this.configure(oldConfig -> config);
   }

   public VaultSpawner configure(UnaryOperator<VaultSpawner.Config> operator) {
      this.oldConfig = this.config.copy();
      this.config = operator.apply(this.config);
      return this;
   }

   public int getMaxMobs() {
      return this.getConfig().getStartMaxMobs() + this.getConfig().getExtraMaxMobs();
   }

   public VaultSpawner addMaxMobs(int amount) {
      return this.configure(config -> config.withExtraMaxMobs(i -> i + amount));
   }

   @Override
   public void execute(VaultRaid vault, VaultPlayer player, ServerLevel world) {
      if (!this.config.equals(this.oldConfig)) {
         this.configHistory.put(player.getTimer().getRunTime(), this.config.copy());
         this.oldConfig = this.config;
      }

      if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
         if (!vault.getAllObjectives().stream().anyMatch(VaultObjective::preventsMobSpawning)) {
            player.runIfPresent(world.getServer(), playerEntity -> {
               this.updateMobIds(world, playerEntity);
               if (this.spawnedMobIds.size() <= this.getMaxMobs() && !(this.getConfig().getMaxDistance() <= 0.0)) {
                  for (int i = 0; i < 50 && this.spawnedMobIds.size() < this.getMaxMobs(); i++) {
                     this.attemptSpawn(vault, world, player, world.getRandom());
                  }
               }
            });
         }
      }
   }

   protected void updateMobIds(ServerLevel world, ServerPlayer player) {
      this.spawnedMobIds = this.spawnedMobIds.stream().<Entity>map(world::getEntity).filter(Objects::nonNull).filter(entity -> {
         double distanceSq = entity.distanceToSqr(player);
         double despawnDistance = this.getConfig().getDespawnDistance();
         if (distanceSq > despawnDistance * despawnDistance) {
            entity.remove(RemovalReason.DISCARDED);
            return false;
         } else {
            return true;
         }
      }).<UUID>map(Entity::getUUID).collect(Collectors.toCollection(VListNBT::ofUUID));
   }

   protected void attemptSpawn(VaultRaid vault, ServerLevel world, VaultPlayer player, Random random) {
      player.runIfPresent(world.getServer(), playerEntity -> {
         double min = this.getConfig().getMinDistance();
         double max = this.getConfig().getMaxDistance();
         double angle = (Math.PI * 2) * random.nextDouble();
         double distance = Math.sqrt(random.nextDouble() * (max * max - min * min) + min * min);
         int x = (int)Math.ceil(distance * Math.cos(angle));
         int z = (int)Math.ceil(distance * Math.sin(angle));
         double xzRadius = Math.sqrt(x * x + z * z);
         double yRange = Math.sqrt(max * max - xzRadius * xzRadius);
         int y = random.nextInt((int)Math.ceil(yRange) * 2 + 1) - (int)Math.ceil(yRange);
         BlockPos pos = playerEntity.blockPosition();
         int level = player.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
         LivingEntity spawned = spawnMob(vault, world, level, pos.getX() + x, pos.getY() + y, pos.getZ() + z, random);
         if (spawned != null) {
            this.spawnedMobIds.add(spawned.getUUID());
         }
      });
   }

   @Nullable
   public static LivingEntity spawnMob(VaultRaid vault, ServerLevel world, int vaultLevel, int x, int y, int z, Random random) {
      LivingEntity entity = createMob(world, vaultLevel, random);
      if (vault.getProperties().getBaseOrDefault(VaultRaid.COW_VAULT, false)) {
         AggressiveCowEntity replaced = VaultCowOverrides.replaceVaultEntity(vault, entity, world);
         if (replaced != null) {
            entity = replaced;
         }
      }

      BlockState state = world.getBlockState(new BlockPos(x, y - 1, z));
      if (!state.isValidSpawn(world, new BlockPos(x, y - 1, z), entity.getType())) {
         return null;
      } else {
         AABB entityBox = entity.getType().getAABB(x + 0.5, y, z + 0.5);
         if (!world.noCollision(entityBox)) {
            return null;
         } else {
            entity.moveTo(x + 0.5F, y + 0.2F, z + 0.5F, (float)(random.nextDouble() * 2.0 * Math.PI), 0.0F);
            if (entity instanceof Mob) {
               ((Mob)entity).spawnAnim();
               ((Mob)entity).finalizeSpawn(world, new DifficultyInstance(Difficulty.PEACEFUL, 13000L, 0L, 0.0F), MobSpawnType.STRUCTURE, null, null);
            }

            LegacyEntityScaler.setScaledEquipmentLegacy(entity, vault, vaultLevel, random, LegacyEntityScaler.Type.MOB);
            LegacyEntityScaler.setScaled(entity);
            world.addWithUUID(entity);
            return entity;
         }
      }
   }

   private static LivingEntity createMob(ServerLevel world, int vaultLevel, Random random) {
      return ModConfigs.VAULT_MOBS.getForLevel(vaultLevel).MOB_POOL.getRandom(random).orElseThrow().create(world);
   }

   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.put("Config", this.getConfig().serializeNBT());
      nbt.put("ConfigHistory", this.configHistory.serializeNBT());
      nbt.put("SpawnedMobsIds", this.spawnedMobIds.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.config.deserializeNBT(nbt.getCompound("Config"));
      this.configHistory.deserializeNBT(nbt.getList("ConfigHistory", 10));
      this.spawnedMobIds.deserializeNBT(nbt.getList("SpawnedMobsIds", 8));
   }

   public static VaultSpawner fromNBT(CompoundTag nbt) {
      VaultSpawner spawner = new VaultSpawner();
      spawner.deserializeNBT(nbt);
      return spawner;
   }

   public static class Config implements INBTSerializable<CompoundTag> {
      @Expose
      @JsonAdapter(IgnoreEmpty.IntegerAdapter.class)
      private int startMaxMobs;
      @Expose
      @JsonAdapter(IgnoreEmpty.IntegerAdapter.class)
      private int extraMaxMobs;
      @Expose
      @JsonAdapter(IgnoreEmpty.DoubleAdapter.class)
      private double minDistance;
      @Expose
      @JsonAdapter(IgnoreEmpty.DoubleAdapter.class)
      private double maxDistance;
      @Expose
      @JsonAdapter(IgnoreEmpty.DoubleAdapter.class)
      private double despawnDistance;

      public Config() {
      }

      public Config(int startMaxMobs, int extraMaxMobs, double minDistance, double maxDistance, double despawnDistance) {
         this.startMaxMobs = startMaxMobs;
         this.extraMaxMobs = extraMaxMobs;
         this.minDistance = minDistance;
         this.maxDistance = maxDistance;
         this.despawnDistance = despawnDistance;
      }

      public int getStartMaxMobs() {
         return this.startMaxMobs;
      }

      public int getExtraMaxMobs() {
         return this.extraMaxMobs;
      }

      public double getMinDistance() {
         return this.minDistance;
      }

      public double getMaxDistance() {
         return this.maxDistance;
      }

      public double getDespawnDistance() {
         return this.despawnDistance;
      }

      public VaultSpawner.Config withStartMaxMobs(int startMaxMobs) {
         return new VaultSpawner.Config(startMaxMobs, this.extraMaxMobs, this.minDistance, this.maxDistance, this.despawnDistance);
      }

      public VaultSpawner.Config withExtraMaxMobs(int extraMaxMobs) {
         return new VaultSpawner.Config(this.startMaxMobs, extraMaxMobs, this.minDistance, this.maxDistance, this.despawnDistance);
      }

      public VaultSpawner.Config withMinDistance(double minDistance) {
         return new VaultSpawner.Config(this.startMaxMobs, this.extraMaxMobs, minDistance, this.maxDistance, this.despawnDistance);
      }

      public VaultSpawner.Config withMaxDistance(double maxDistance) {
         return new VaultSpawner.Config(this.startMaxMobs, this.extraMaxMobs, this.minDistance, maxDistance, this.despawnDistance);
      }

      public VaultSpawner.Config withDespawnDistance(double despawnDistance) {
         return new VaultSpawner.Config(this.startMaxMobs, this.extraMaxMobs, this.minDistance, this.maxDistance, despawnDistance);
      }

      public VaultSpawner.Config withStartMaxMobs(IntUnaryOperator operator) {
         return new VaultSpawner.Config(operator.applyAsInt(this.startMaxMobs), this.extraMaxMobs, this.minDistance, this.maxDistance, this.despawnDistance);
      }

      public VaultSpawner.Config withExtraMaxMobs(IntUnaryOperator operator) {
         return new VaultSpawner.Config(this.startMaxMobs, operator.applyAsInt(this.extraMaxMobs), this.minDistance, this.maxDistance, this.despawnDistance);
      }

      public VaultSpawner.Config withMinDistance(DoubleUnaryOperator operator) {
         return new VaultSpawner.Config(this.startMaxMobs, this.extraMaxMobs, operator.applyAsDouble(this.minDistance), this.maxDistance, this.despawnDistance);
      }

      public VaultSpawner.Config withMaxDistance(DoubleUnaryOperator operator) {
         return new VaultSpawner.Config(this.startMaxMobs, this.extraMaxMobs, this.minDistance, operator.applyAsDouble(this.maxDistance), this.despawnDistance);
      }

      public VaultSpawner.Config withDespawnDistance(DoubleUnaryOperator operator) {
         return new VaultSpawner.Config(this.startMaxMobs, this.extraMaxMobs, this.minDistance, this.maxDistance, operator.applyAsDouble(this.despawnDistance));
      }

      public VaultSpawner.Config copy() {
         return new VaultSpawner.Config(this.startMaxMobs, this.extraMaxMobs, this.minDistance, this.maxDistance, this.despawnDistance);
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putInt("StartMaxMobs", this.startMaxMobs);
         nbt.putInt("ExtraMaxMobs", this.extraMaxMobs);
         nbt.putDouble("MinDistance", this.minDistance);
         nbt.putDouble("MaxDistance", this.maxDistance);
         nbt.putDouble("DespawnDistance", this.despawnDistance);
         return nbt;
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.startMaxMobs = nbt.getInt("StartMaxMobs");
         this.extraMaxMobs = nbt.getInt("ExtraMaxMobs");
         this.minDistance = nbt.getDouble("MinDistance");
         this.maxDistance = nbt.getDouble("MaxDistance");
         this.despawnDistance = nbt.getDouble("DespawnDistance");
      }

      @Override
      public boolean equals(Object other) {
         if (this == other) {
            return true;
         } else {
            return !(other instanceof VaultSpawner.Config config)
               ? false
               : this.getStartMaxMobs() == config.getStartMaxMobs()
                  && this.getExtraMaxMobs() == config.getExtraMaxMobs()
                  && this.getMinDistance() == config.getMinDistance()
                  && this.getMaxDistance() == config.getMaxDistance()
                  && this.getDespawnDistance() == config.getDespawnDistance();
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.getStartMaxMobs(), this.getExtraMaxMobs(), this.getMinDistance(), this.getMaxDistance(), this.getDespawnDistance());
      }

      public static VaultSpawner.Config fromNBT(CompoundTag nbt) {
         VaultSpawner.Config config = new VaultSpawner.Config();
         config.deserializeNBT(nbt);
         return config;
      }
   }
}
