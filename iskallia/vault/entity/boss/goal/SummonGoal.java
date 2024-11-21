package iskallia.vault.entity.boss.goal;

import iskallia.vault.VaultMod;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.boss.MobSpawningUtils;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.trait.ITrait;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class SummonGoal extends Goal implements ITrait {
   public static final String TYPE = "summon";
   protected final VaultBossBaseEntity boss;
   private int maxMobsAliveBeforeNextSpawn = 5;
   private int minMobSpawnCount = 1;
   private int maxMobSpawnCount = 5;
   private WeightedList<MobSpawningUtils.EntitySpawnData> entityTypes = WeightedList.empty();
   private int minDistanceFromCenter = 2;
   private int radius = 10;
   private int spawnInterval = 20;
   private long lastSpawnCheckTime = 0L;
   private final Set<UUID> spawnedMobs = new HashSet<>();
   private long lastMobDeathCheckTime = 0L;
   private Consumer<LivingDeathEvent> onLivingDeath;
   private int stackSize = 1;

   public SummonGoal(VaultBossBaseEntity boss) {
      this.boss = boss;
   }

   @Override
   public String getType() {
      return "summon";
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addTraitGoal(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof SummonGoal summoningGoal) {
         this.stackSize++;
         this.spawnInterval = summoningGoal.spawnInterval * this.stackSize;
      }
   }

   public void tick() {
      if (this.boss.getLevel() instanceof ServerLevel serverLevel && this.lastMobDeathCheckTime + 20L < this.boss.getLevel().getGameTime()) {
         this.lastMobDeathCheckTime = this.boss.getLevel().getGameTime();
         this.spawnedMobs.removeIf(uuid -> serverLevel.getEntity(uuid) == null);
      }

      if (this.boss.getLevel() instanceof ServerLevel serverLevel) {
         if (this.lastSpawnCheckTime + this.spawnInterval <= this.boss.getLevel().getGameTime()) {
            this.lastSpawnCheckTime = this.boss.getLevel().getGameTime();
            if (this.spawnedMobs.size() <= this.maxMobsAliveBeforeNextSpawn * this.boss.getPlayerCount()) {
               int mobsToSpawn;
               if (this.minMobSpawnCount > this.maxMobSpawnCount) {
                  mobsToSpawn = this.boss.getPlayerAdjustedRandomCount(this.minMobSpawnCount, this.minMobSpawnCount, 0.4F);
                  VaultMod.LOGGER.error("minMobCount is greater than maxMobCount in summoning stage attributes, defaulting to minMobCount");
               } else {
                  mobsToSpawn = this.boss.getPlayerAdjustedRandomCount(this.minMobSpawnCount, this.maxMobSpawnCount, 0.4F);
               }

               for (int i = 0; i < mobsToSpawn; i++) {
                  this.entityTypes
                     .getRandom(serverLevel.getRandom())
                     .ifPresent(
                        entityType -> {
                           Entity mob = MobSpawningUtils.spawnMob(
                              serverLevel,
                              this.minDistanceFromCenter,
                              this.radius,
                              2,
                              entityType.entityType(),
                              entityType.entityNbt(),
                              this.getSummonCenter(),
                              true
                           );
                           if (mob != null) {
                              this.spawnedMobs.add(mob.getUUID());
                           }
                        }
                     );
               }
            }
         }
      }
   }

   protected Vec3 getSummonCenter() {
      return this.boss.position();
   }

   public void start() {
      this.onLivingDeath = this::onSummonedMobDeath;
      MinecraftForge.EVENT_BUS.addListener(this.onLivingDeath);
   }

   private void onSummonedMobDeath(LivingDeathEvent event) {
      Entity entity = event.getEntity();
      if (entity.getTags().contains("boss_summoned")) {
         this.spawnedMobs.remove(entity.getUUID());
      }
   }

   public void stop() {
      MinecraftForge.EVENT_BUS.unregister(this.onLivingDeath);
   }

   public boolean canUse() {
      return true;
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("MaxMobsAliveBeforeNextSpawn", this.maxMobsAliveBeforeNextSpawn);
      nbt.putInt("MinMobSpawnCount", this.minMobSpawnCount);
      nbt.putInt("MaxMobSpawnCount", this.maxMobSpawnCount);
      nbt.putInt("SpawnInterval", this.spawnInterval);
      nbt.putInt("MinDistanceFromCenter", this.minDistanceFromCenter);
      nbt.putInt("Radius", this.radius);
      nbt.put("EntityTypes", this.serializeEntityTypes());
      if (!this.spawnedMobs.isEmpty()) {
         ListTag listTag = new ListTag();
         this.spawnedMobs.forEach(uuid -> listTag.add(NbtUtils.createUUID(uuid)));
         nbt.put("SpawnedMobs", listTag);
      }

      nbt.putInt("StackSize", this.stackSize);
      return nbt;
   }

   private ListTag serializeEntityTypes() {
      ListTag tag = new ListTag();
      this.entityTypes.forEach((entityType, weight) -> {
         CompoundTag compoundTag = new CompoundTag();
         entityType.serializeTo(compoundTag);
         compoundTag.putDouble("Weight", weight);
         tag.add(compoundTag);
      });
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt, VaultBossBaseEntity boss) {
      this.setAttributes(
         nbt.getInt("MaxMobsAliveBeforeNextSpawn"),
         nbt.getInt("MinMobSpawnCount"),
         nbt.getInt("MaxMobSpawnCount"),
         nbt.getInt("SpawnInterval"),
         nbt.getInt("MinDistanceFromCenter"),
         nbt.getInt("Radius"),
         this.deserializeEntityTypes(nbt.getList("EntityTypes", 10))
      );
      if (nbt.contains("SpawnedMobs")) {
         this.spawnedMobs.clear();
         ListTag listTag = nbt.getList("SpawnedMobs", 11);
         listTag.forEach(tag -> this.spawnedMobs.add(NbtUtils.loadUUID(tag)));
      }

      this.stackSize = nbt.getInt("StackSize");
   }

   public SummonGoal setAttributes(
      int maxMobsAliveBeforeNextSpawn,
      int minMobSpawnCount,
      int maxMobSpawnCount,
      int spawnInterval,
      int minDistanceFromCenter,
      int radius,
      WeightedList<MobSpawningUtils.EntitySpawnData> entityTypes
   ) {
      this.maxMobsAliveBeforeNextSpawn = maxMobsAliveBeforeNextSpawn;
      this.minMobSpawnCount = minMobSpawnCount;
      this.maxMobSpawnCount = maxMobSpawnCount;
      this.spawnInterval = spawnInterval;
      this.minDistanceFromCenter = minDistanceFromCenter;
      this.radius = radius;
      this.entityTypes = entityTypes;
      return this;
   }

   private WeightedList<MobSpawningUtils.EntitySpawnData> deserializeEntityTypes(ListTag tag) {
      WeightedList<MobSpawningUtils.EntitySpawnData> weightedList = new WeightedList<>();

      for (Tag element : tag) {
         CompoundTag compoundTag = (CompoundTag)element;
         MobSpawningUtils.EntitySpawnData.from(compoundTag).ifPresent(entitySpawnData -> weightedList.put(entitySpawnData, compoundTag.getDouble("Weight")));
      }

      return weightedList;
   }
}
