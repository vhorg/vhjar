package iskallia.vault.world.vault.logic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import iskallia.vault.entity.EntityScaler;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.nbt.VListNBT;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.util.gson.IgnoreEmpty;
import iskallia.vault.world.data.GlobalDifficultyData;
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
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

public class VaultSpawner implements INBTSerializable<CompoundNBT>, IVaultTask {
   private VaultSpawner.Config config = new VaultSpawner.Config();
   private VaultSpawner.Config oldConfig = new VaultSpawner.Config();
   private VMapNBT<Integer, VaultSpawner.Config> configHistory = VMapNBT.ofInt(new LinkedHashMap(), VaultSpawner.Config::new);
   private VListNBT<UUID, StringNBT> spawnedMobIds = VListNBT.ofUUID();

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
   public void execute(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      if (!this.config.equals(this.oldConfig)) {
         this.configHistory.put(player.getTimer().getRunTime(), this.config.copy());
         this.oldConfig = this.config;
      }

      if (world.func_82736_K().func_223586_b(GameRules.field_223601_d)) {
         if (!vault.getAllObjectives().stream().anyMatch(VaultObjective::preventsMobSpawning)) {
            player.runIfPresent(world.func_73046_m(), playerEntity -> {
               this.updateMobIds(world, playerEntity);
               if (this.spawnedMobIds.size() <= this.getMaxMobs() && !(this.getConfig().getMaxDistance() <= 0.0)) {
                  for (int i = 0; i < 50 && this.spawnedMobIds.size() < this.getMaxMobs(); i++) {
                     this.attemptSpawn(vault, world, player, world.func_201674_k());
                  }
               }
            });
         }
      }
   }

   protected void updateMobIds(ServerWorld world, ServerPlayerEntity player) {
      this.spawnedMobIds = this.spawnedMobIds.stream().<Entity>map(world::func_217461_a).filter(Objects::nonNull).filter(entity -> {
         double distanceSq = entity.func_70068_e(player);
         double despawnDistance = this.getConfig().getDespawnDistance();
         if (distanceSq > despawnDistance * despawnDistance) {
            entity.func_70106_y();
            return false;
         } else {
            return true;
         }
      }).<UUID>map(Entity::func_110124_au).collect(Collectors.toCollection(VListNBT::ofUUID));
   }

   protected void attemptSpawn(VaultRaid vault, ServerWorld world, VaultPlayer player, Random random) {
      player.runIfPresent(world.func_73046_m(), playerEntity -> {
         double min = this.getConfig().getMinDistance();
         double max = this.getConfig().getMaxDistance();
         double angle = (Math.PI * 2) * random.nextDouble();
         double distance = Math.sqrt(random.nextDouble() * (max * max - min * min) + min * min);
         int x = (int)Math.ceil(distance * Math.cos(angle));
         int z = (int)Math.ceil(distance * Math.sin(angle));
         double xzRadius = Math.sqrt(x * x + z * z);
         double yRange = Math.sqrt(max * max - xzRadius * xzRadius);
         int y = random.nextInt((int)Math.ceil(yRange) * 2 + 1) - (int)Math.ceil(yRange);
         BlockPos pos = playerEntity.func_233580_cy_();
         int level = player.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
         LivingEntity spawned = spawnMob(vault, world, level, pos.func_177958_n() + x, pos.func_177956_o() + y, pos.func_177952_p() + z, random);
         if (spawned != null) {
            this.spawnedMobIds.add(spawned.func_110124_au());
         }
      });
   }

   @Nullable
   public static LivingEntity spawnMob(VaultRaid vault, ServerWorld world, int vaultLevel, int x, int y, int z, Random random) {
      LivingEntity entity = createMob(world, vaultLevel, random);
      if (vault.getProperties().getBaseOrDefault(VaultRaid.COW_VAULT, false)) {
         entity = VaultCowOverrides.replaceVaultEntity(entity, world);
      }

      BlockState state = world.func_180495_p(new BlockPos(x, y - 1, z));
      if (!state.func_215688_a(world, new BlockPos(x, y - 1, z), entity.func_200600_R())) {
         return null;
      } else {
         AxisAlignedBB entityBox = entity.func_200600_R().func_220328_a(x + 0.5, y, z + 0.5);
         if (!world.func_226664_a_(entityBox)) {
            return null;
         } else {
            entity.func_70012_b(x + 0.5F, y + 0.2F, z + 0.5F, (float)(random.nextDouble() * 2.0 * Math.PI), 0.0F);
            if (entity instanceof MobEntity) {
               ((MobEntity)entity).func_70656_aK();
               ((MobEntity)entity).func_213386_a(world, new DifficultyInstance(Difficulty.PEACEFUL, 13000L, 0L, 0.0F), SpawnReason.STRUCTURE, null, null);
            }

            GlobalDifficultyData.Difficulty difficulty = GlobalDifficultyData.get(world).getVaultDifficulty();
            EntityScaler.setScaledEquipment(entity, vault, difficulty, vaultLevel, random, EntityScaler.Type.MOB);
            EntityScaler.setScaled(entity);
            world.func_217470_d(entity);
            return entity;
         }
      }
   }

   private static LivingEntity createMob(ServerWorld world, int vaultLevel, Random random) {
      return ModConfigs.VAULT_MOBS.getForLevel(vaultLevel).MOB_POOL.getRandom(random).create(world);
   }

   public CompoundNBT serializeNBT() {
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_218657_a("Config", this.getConfig().serializeNBT());
      nbt.func_218657_a("ConfigHistory", this.configHistory.serializeNBT());
      nbt.func_218657_a("SpawnedMobsIds", this.spawnedMobIds.serializeNBT());
      return nbt;
   }

   public void deserializeNBT(CompoundNBT nbt) {
      this.config.deserializeNBT(nbt.func_74775_l("Config"));
      this.configHistory.deserializeNBT(nbt.func_150295_c("ConfigHistory", 10));
      this.spawnedMobIds.deserializeNBT(nbt.func_150295_c("SpawnedMobsIds", 8));
   }

   public static VaultSpawner fromNBT(CompoundNBT nbt) {
      VaultSpawner spawner = new VaultSpawner();
      spawner.deserializeNBT(nbt);
      return spawner;
   }

   public static class Config implements INBTSerializable<CompoundNBT> {
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

      public CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74768_a("StartMaxMobs", this.startMaxMobs);
         nbt.func_74768_a("ExtraMaxMobs", this.extraMaxMobs);
         nbt.func_74780_a("MinDistance", this.minDistance);
         nbt.func_74780_a("MaxDistance", this.maxDistance);
         nbt.func_74780_a("DespawnDistance", this.despawnDistance);
         return nbt;
      }

      public void deserializeNBT(CompoundNBT nbt) {
         this.startMaxMobs = nbt.func_74762_e("StartMaxMobs");
         this.extraMaxMobs = nbt.func_74762_e("ExtraMaxMobs");
         this.minDistance = nbt.func_74769_h("MinDistance");
         this.maxDistance = nbt.func_74769_h("MaxDistance");
         this.despawnDistance = nbt.func_74769_h("DespawnDistance");
      }

      @Override
      public boolean equals(Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof VaultSpawner.Config)) {
            return false;
         } else {
            VaultSpawner.Config config = (VaultSpawner.Config)other;
            return this.getStartMaxMobs() == config.getStartMaxMobs()
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

      public static VaultSpawner.Config fromNBT(CompoundNBT nbt) {
         VaultSpawner.Config config = new VaultSpawner.Config();
         config.deserializeNBT(nbt);
         return config;
      }
   }
}
