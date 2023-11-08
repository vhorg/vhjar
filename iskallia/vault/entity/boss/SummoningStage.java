package iskallia.vault.entity.boss;

import iskallia.vault.VaultMod;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

public class SummoningStage implements IBossStage {
   private static final ResourceLocation BOSS_TEXTURE = new ResourceLocation("the_vault", "textures/entity/boss/artifact_boss_velara.png");
   public static final String NAME = "summoning";
   private static final Set<Flag> FLAGS = Set.of(Flag.MOVE, Flag.TARGET);
   private static final String TAG_BOSS_SUMMONED = "boss_summoned";
   private final ArtifactBossEntity boss;
   private final SummoningStageAttributes summoningStageAttributes;
   private long lastSpawnTime = 0L;
   private int totalMobGroupsToSummon;
   private int mobGroupsSummoned;
   private int totalMobsSpawned = 0;
   private final Set<UUID> spawnedMobs = new HashSet<>();
   private long stageStartTime = Long.MAX_VALUE;
   private long lastMobDeathCheckTime = 0L;
   private Consumer<LivingDeathEvent> onLivingDeath;
   boolean spawningFinished = false;

   public SummoningStage(ArtifactBossEntity boss, SummoningStageAttributes summoningStageAttributes) {
      this.boss = boss;
      this.summoningStageAttributes = summoningStageAttributes;
      this.totalMobGroupsToSummon = boss.getLevel().getRandom().nextInt(summoningStageAttributes.minGroupCount, summoningStageAttributes.maxGroupCount + 1);
   }

   @Override
   public String getName() {
      return "summoning";
   }

   @Override
   public void tick() {
      if (this.boss.getLevel() instanceof ServerLevel serverLevel && this.lastMobDeathCheckTime + 20L < this.boss.getLevel().getGameTime()) {
         this.lastMobDeathCheckTime = this.boss.getLevel().getGameTime();
         this.spawnedMobs.removeIf(uuid -> serverLevel.getEntity(uuid) == null);
      }

      if (this.mobGroupsSummoned >= this.totalMobGroupsToSummon) {
         this.spawningFinished = true;
      } else if (this.boss.getLevel() instanceof ServerLevel serverLevel) {
         if (this.lastSpawnTime + this.summoningStageAttributes.spawnDelay <= serverLevel.getGameTime()) {
            int mobsToSpawn;
            if (this.summoningStageAttributes.minMobCount > this.summoningStageAttributes.maxMobCount) {
               mobsToSpawn = this.boss.getPlayerAdjustedRandomCount(this.summoningStageAttributes.minMobCount, this.summoningStageAttributes.minMobCount, 0.4F);
               VaultMod.LOGGER.error("minMobCount is greater than maxMobCount in summoning stage attributes, defaulting to minMobCount");
            } else {
               mobsToSpawn = this.boss.getPlayerAdjustedRandomCount(this.summoningStageAttributes.minMobCount, this.summoningStageAttributes.maxMobCount, 0.4F);
            }

            for (int i = 0; i < mobsToSpawn; i++) {
               this.summoningStageAttributes
                  .entityTypes
                  .getRandom(serverLevel.getRandom())
                  .ifPresent(
                     entityType -> this.spawnEntity(
                        this.boss, serverLevel, this.summoningStageAttributes.radius, entityType.entityType(), entityType.entityNbt()
                     )
                  );
               this.lastSpawnTime = serverLevel.getGameTime();
            }

            this.mobGroupsSummoned++;
         }
      }
   }

   private void spawnEntity(ArtifactBossEntity artifactBossEntity, ServerLevel serverLevel, double radius, EntityType<?> entityType, CompoundTag entityNbt) {
      double x = artifactBossEntity.getX() + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * radius + 0.5;
      double y = artifactBossEntity.getY();
      double z = artifactBossEntity.getZ() + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * radius + 0.5;
      BlockPos spawnPos = new BlockPos(x, y, z);
      Entity entity = entityType.spawn(serverLevel, null, null, spawnPos, MobSpawnType.SPAWNER, false, false);
      if (entity == null) {
         VaultMod.LOGGER.error("Unable to spawn entity type {} because its factory returned null", entityType.getRegistryName());
      } else {
         if (entityNbt != null) {
            CompoundTag entityTag = entity.saveWithoutId(new CompoundTag());
            entityTag.merge(entityNbt.copy());
            entity.load(entityTag);
         }

         if (entity instanceof Mob) {
            ((Mob)entity).spawnAnim();
         }

         entity.getTags().add("boss_summoned");
         this.spawnedMobs.add(entity.getUUID());
         this.totalMobsSpawned++;
      }
   }

   @Override
   public boolean isFinished() {
      return this.spawningFinished && this.spawnedMobs.isEmpty();
   }

   @Override
   public boolean makesBossInvulnerable() {
      return true;
   }

   @Override
   public Set<Flag> getControlFlags() {
      return FLAGS;
   }

   @Override
   public void start() {
      this.onLivingDeath = this::onSummonedMobDeath;
      MinecraftForge.EVENT_BUS.addListener(this.onLivingDeath);
      this.stageStartTime = this.boss.getLevel().getGameTime();
   }

   private void onSummonedMobDeath(LivingDeathEvent event) {
      Entity entity = event.getEntity();
      if (entity.getTags().contains("boss_summoned")) {
         this.spawnedMobs.remove(entity.getUUID());
      }
   }

   @Override
   public void stop() {
      MinecraftForge.EVENT_BUS.unregister(this.onLivingDeath);
   }

   @Override
   public CompoundTag serialize() {
      CompoundTag tag = IBossStage.super.serialize();
      tag.put("SummoningStageAttributes", this.summoningStageAttributes.serialize());
      tag.putLong("StageStartTime", this.stageStartTime);
      tag.putInt("TotalMobGroupsToSummon", this.totalMobGroupsToSummon);
      tag.putInt("MobGroupsSummoned", this.mobGroupsSummoned);
      tag.putInt("TotalMobsSpawned", this.totalMobsSpawned);
      tag.putLong("LastSpawnTime", this.lastSpawnTime);
      tag.putBoolean("SpawningFinished", this.spawningFinished);
      tag.put("SpawnedMobs", this.serializeSpawnedMobs());
      return tag;
   }

   @Override
   public Optional<AnimationBuilder> getAnimation() {
      return Optional.of(ArtifactBossEntity.SUMMON_CONTINUOUS_ANIM);
   }

   @Override
   public Optional<ResourceLocation> getTextureLocation() {
      return Optional.of(BOSS_TEXTURE);
   }

   @Override
   public Tuple<Integer, Integer> getBossBarTextureVs() {
      return new Tuple(248, 352);
   }

   @Override
   public float getProgress() {
      float partial = (float)this.mobGroupsSummoned / this.totalMobGroupsToSummon;
      return 1.0F - partial + partial * this.spawnedMobs.size() / this.totalMobsSpawned;
   }

   public static SummoningStage fromAttributes(ArtifactBossEntity artifactBossEntity, CompoundTag attributesTag) {
      return new SummoningStage(artifactBossEntity, SummoningStageAttributes.from(attributesTag));
   }

   public static SummoningStage from(ArtifactBossEntity artifactBossEntity, CompoundTag tag) {
      SummoningStage summoningStage = fromAttributes(artifactBossEntity, tag.getCompound("SummoningStageAttributes"));
      summoningStage.stageStartTime = tag.getLong("StageStartTime");
      summoningStage.totalMobGroupsToSummon = tag.getInt("TotalMobGroupsToSummon");
      summoningStage.mobGroupsSummoned = tag.getInt("MobGroupsSummoned");
      summoningStage.totalMobsSpawned = tag.getInt("TotalMobsSpawned");
      summoningStage.lastSpawnTime = tag.getLong("LastSpawnTime");
      summoningStage.spawningFinished = tag.getBoolean("SpawningFinished");
      summoningStage.spawnedMobs.addAll(deserializeSpawnedMobs(tag.getList("SpawnedMobs", 11)));
      return summoningStage;
   }

   private ListTag serializeSpawnedMobs() {
      ListTag listTag = new ListTag();
      this.spawnedMobs.forEach(uuid -> listTag.add(NbtUtils.createUUID(uuid)));
      return listTag;
   }

   private static Set<UUID> deserializeSpawnedMobs(ListTag listTag) {
      Set<UUID> mobUuids = new HashSet<>();
      listTag.forEach(tag -> mobUuids.add(NbtUtils.loadUUID(tag)));
      return mobUuids;
   }
}
