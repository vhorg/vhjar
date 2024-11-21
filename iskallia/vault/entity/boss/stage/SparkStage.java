package iskallia.vault.entity.boss.stage;

import com.google.common.util.concurrent.AtomicDouble;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.entity.boss.ArtifactBossEntity;
import iskallia.vault.entity.boss.MagicProjectileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.mixin.AccessorChunkMap;
import iskallia.vault.network.message.AbsorbingParticleMessage;
import iskallia.vault.network.message.ClientboundArtifactBossWendarrExplodeMessage;
import iskallia.vault.network.message.ClientboundSafePointPlaceParticleMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.mutable.MutableObject;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

public class SparkStage implements IBossStage {
   private static final TargetingConditions PLAYERS_TARGETING_CONDITIONS = TargetingConditions.forCombat().range(50.0);
   private static final ResourceLocation BOSS_TEXTURE = new ResourceLocation("the_vault", "textures/entity/boss/artifact_boss_wendarr.png");
   private static final int PROJECTILE_CHARGE_NEEDED = 30;
   private static final int SPARK_REMOVAL_DELAY = 20;
   private static final int DAMAGE_DEALT_DELAY = 60;
   private static final Set<Flag> FLAGS = Set.of(Flag.MOVE, Flag.TARGET);
   public static final String NAME = "spark";
   public static final int SAFE_POINT_RADIUS = 4;
   private final ArtifactBossEntity boss;
   private final SparkStageAttributes attributes;
   private int sparkCount;
   private final Set<BlockPos> sparkPositions = new HashSet<>();
   private long lastSparkSpawnTime = 0L;
   private int projectileCharge = 0;
   private long currentSparksExpiration = Long.MAX_VALUE;
   private int sparkRemovalTimer = -1;
   private int stunTimer = 2;
   private int damageDealTimer = 0;
   private final List<BlockPos> safePointCenters = new ArrayList<>();
   private final Map<BlockState, Set<BlockPos>> safePointOriginalBlocks = new HashMap<>();

   public SparkStage(ArtifactBossEntity boss, SparkStageAttributes attributes) {
      this.boss = boss;
      this.attributes = attributes;
      this.sparkCount = boss.getPlayerAdjustedRandomCount(attributes.minSparkCount, attributes.maxSparkCount, 1.0F);
   }

   @Override
   public String getName() {
      return "spark";
   }

   @Override
   public void tick() {
      if (this.boss.getLevel() instanceof ServerLevel serverLevel) {
         if (this.currentSparksExpiration <= serverLevel.getGameTime()) {
            this.projectileCharge = 0;
            this.currentSparksExpiration = Long.MAX_VALUE;
            this.damageDealTimer = 60;
            this.placeSafePoints(serverLevel);
         } else if (this.damageDealTimer > 0) {
            if (this.damageDealTimer == 60) {
               this.sparkPositions
                  .forEach(
                     sparkPosition -> {
                        if (serverLevel.getBlockState(sparkPosition).getBlock() == ModBlocks.SPARK) {
                           this.boss.level.playSound(null, sparkPosition, ModSounds.SPARK_EXPUNGE, SoundSource.BLOCKS, 1.0F, 1.0F);
                           ModNetwork.CHANNEL
                              .send(
                                 PacketDistributor.ALL.noArg(),
                                 new AbsorbingParticleMessage(new Vec3(sparkPosition.getX(), sparkPosition.getY(), sparkPosition.getZ()), this.boss, 16769280)
                              );
                        }
                     }
                  );
               this.boss.level.playSound(null, this.boss.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.0F, 0.75F);
            }

            this.damageDealTimer--;
            if (this.damageDealTimer == 0) {
               this.dealDamageToPlayers(serverLevel);
               this.removeSafePoints(serverLevel);
            }
         } else if (this.stunTimer > 0) {
            this.stunTimer--;
            this.doStunnedLogic(serverLevel);
         } else {
            this.placeSparksAndShootProjectiles(serverLevel);
         }
      }
   }

   private void dealDamageToPlayers(ServerLevel serverLevel) {
      this.sparkRemovalTimer = 20;
      AtomicDouble playerDamage = new AtomicDouble(0.0);
      this.stunTimer = 0;
      this.sparkPositions.forEach(sparkPosition -> {
         if (serverLevel.getBlockState(sparkPosition).getBlock() == ModBlocks.CONVERTED_SPARK) {
            this.stunTimer = this.stunTimer + this.attributes.stunTimePerSpark;
         } else {
            playerDamage.addAndGet(this.attributes.damagePerSpark);
         }
      });
      if (this.stunTimer > 0) {
         this.boss.setStunned(true);
      } else {
         this.endStun(serverLevel);
      }

      serverLevel.getNearbyPlayers(PLAYERS_TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(60.0)).forEach(player -> {
         if (this.playerIsInSafePoint(player)) {
            player.hurt(DamageSource.LIGHTNING_BOLT, (float)this.attributes.damagePerSpark);
         } else {
            player.hurt(DamageSource.LIGHTNING_BOLT, playerDamage.floatValue());
         }
      });
      this.boss.level.playSound(null, this.boss.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 1.0F, 2.0F);
      ModNetwork.CHANNEL
         .send(
            PacketDistributor.ALL.noArg(),
            new ClientboundArtifactBossWendarrExplodeMessage(this.boss.position().x, this.boss.position().y, this.boss.position().z, 60.0, 0.0, 0.0)
         );
   }

   private boolean playerIsInSafePoint(Player player) {
      for (BlockPos safePointCenter : this.safePointCenters) {
         if (safePointCenter.distToCenterSqr(player.getX(), player.getY(), player.getZ()) <= 25.0) {
            return true;
         }
      }

      return false;
   }

   private void removeSafePoints(ServerLevel serverLevel) {
      if (!this.safePointCenters.isEmpty()) {
         this.safePointOriginalBlocks.forEach((blockState, blockPositions) -> blockPositions.forEach(blockPosition -> {
            this.boss.getLevel().setBlock(blockPosition, blockState, 3);
            sendBlockUpdatesToClient(serverLevel, blockPosition);
         }));
         this.safePointCenters.clear();
         this.safePointOriginalBlocks.clear();
      }
   }

   private void endStun(ServerLevel serverLevel) {
      if (!this.boss.isCloseToDeath()) {
         this.sparkCount = this.boss.getPlayerAdjustedRandomCount(this.attributes.minSparkCount, this.attributes.maxSparkCount, 1.0F);
      }

      this.removeSparks(serverLevel);
   }

   private void placeSparksAndShootProjectiles(ServerLevel serverLevel) {
      this.projectileCharge++;
      if (this.projectileCharge >= 30) {
         this.shootAtPlayers();
         this.projectileCharge = 0;
      }

      while (this.needsToSpawnSpark() && this.lastSparkSpawnTime + this.attributes.sparkSpawnInterval <= this.boss.getLevel().getGameTime()) {
         this.placeSpark(serverLevel);
      }
   }

   private void placeSafePoints(ServerLevel serverLevel) {
      if (this.safePointCenters.isEmpty()) {
         this.determineSafePointCenters();
         this.placeSafePointBlocks(serverLevel);
         this.spawnSafePointParticles(serverLevel);
      }
   }

   private void spawnSafePointParticles(ServerLevel serverLevel) {
      this.safePointCenters
         .forEach(
            safePointCenter -> ModNetwork.CHANNEL
               .send(
                  PacketDistributor.TRACKING_CHUNK.with(() -> this.boss.getLevel().getChunkAt(safePointCenter)),
                  new ClientboundSafePointPlaceParticleMessage(safePointCenter)
               )
         );
   }

   private void placeSafePointBlocks(ServerLevel serverLevel) {
      this.safePointCenters
         .forEach(
            safePointCenter -> {
               MutableBlockPos blockPos = new MutableBlockPos();

               for (int x = -4; x <= 4; x++) {
                  for (int z = -4; z <= 4; z++) {
                     blockPos.set(safePointCenter.getX() + x, safePointCenter.getY(), safePointCenter.getZ() + z);
                     long roundedDistance = Math.round(Math.sqrt(safePointCenter.distSqr(blockPos)));
                     if (roundedDistance <= 4L) {
                        if (roundedDistance == 4L) {
                           this.safePointOriginalBlocks
                              .computeIfAbsent(this.boss.getLevel().getBlockState(blockPos), blockState -> new HashSet<>())
                              .add(blockPos.immutable());
                           this.boss.getLevel().setBlock(blockPos, ModBlocks.WENDARR_JEWEL_BLOCK.defaultBlockState(), 3);
                           sendBlockUpdatesToClient(serverLevel, blockPos);
                        } else {
                           this.safePointOriginalBlocks
                              .computeIfAbsent(this.boss.getLevel().getBlockState(blockPos), blockState -> new HashSet<>())
                              .add(blockPos.immutable());
                           this.boss.getLevel().setBlock(blockPos, ModBlocks.WENDARR_LIGHT_SMOOTH_BLOCK.defaultBlockState(), 3);
                           sendBlockUpdatesToClient(serverLevel, blockPos);
                        }
                     }
                  }
               }
            }
         );
   }

   private void determineSafePointCenters() {
      float minDifference = (float) (Math.PI / 3);
      int safePointsCount = 3;
      float firstAngle = this.boss.getLevel().random.nextFloat() * 2.0F * (float) Math.PI;
      float slice = (float)(((Math.PI * 2) - minDifference) / safePointsCount);
      float okPartOfSlice = slice - minDifference;
      Vec3 centerPos = this.boss.getSpawnPosition();

      for (int i = 0; i < safePointsCount; i++) {
         float angle;
         if (i == 0) {
            angle = firstAngle;
         } else {
            angle = firstAngle + (i - 1) * slice + minDifference + this.boss.getLevel().random.nextFloat() * okPartOfSlice;
         }

         float dist = this.attributes.radius - 10 + this.boss.getLevel().random.nextInt(10);
         double x = centerPos.x + dist * Math.cos(angle);
         double z = centerPos.z + dist * Math.sin(angle);
         this.safePointCenters.add(new BlockPos(x, centerPos.y - 1.0, z));
      }
   }

   private void doStunnedLogic(ServerLevel serverLevel) {
      if (this.sparkRemovalTimer > 0) {
         this.sparkRemovalTimer--;
      } else if (this.sparkRemovalTimer == 0) {
         this.sparkRemovalTimer = -1;
         this.removeSparks(serverLevel);
      }

      if (this.stunTimer <= 0) {
         this.boss.setStunned(false);
         this.endStun(serverLevel);
      }
   }

   private void removeSparks(ServerLevel serverLevel) {
      for (BlockPos sparkPosition : this.sparkPositions) {
         serverLevel.destroyBlock(sparkPosition, false);
      }

      this.sparkPositions.clear();
   }

   private void shootAtPlayers() {
      this.boss.getLevel().getNearbyPlayers(PLAYERS_TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(60.0)).forEach(player -> {
         double x = player.getX() - this.boss.getX();
         double y = player.getY(0.5) - this.boss.getY(0.5);
         double z = player.getZ() - this.boss.getZ();
         this.boss.level.playSound(null, this.boss.blockPosition(), ModSounds.ARTIFACT_BOSS_MAGIC_ATTACK, SoundSource.HOSTILE, 1.0F, 1.0F);
         MagicProjectileEntity magicProjectile = new MagicProjectileEntity(this.boss.getLevel(), this.boss, x, y, z, player, 10.0F);
         magicProjectile.setColor(VaultGod.WENDARR.getColor());
         Vec3 vec3 = new Vec3(x, y, z);
         double d0 = vec3.horizontalDistance();
         magicProjectile.setXRot((float)(Mth.atan2(vec3.y, d0) * 180.0F / (float)Math.PI));
         magicProjectile.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI));
         magicProjectile.setPos(magicProjectile.getX(), this.boss.getY(0.5) + 0.5, magicProjectile.getZ());
         this.boss.getLevel().addFreshEntity(magicProjectile);
      });
   }

   public boolean needsToSpawnSpark() {
      return this.sparkPositions.size() < this.sparkCount;
   }

   @Override
   public boolean isFinished() {
      return this.boss.isCloseToDeath();
   }

   @Override
   public boolean makesBossInvulnerable() {
      return !this.boss.isStunned();
   }

   @Override
   public Set<Flag> getControlFlags() {
      return FLAGS;
   }

   @Override
   public void init() {
      this.boss.setScaledHealth(this.attributes.health);
   }

   @Override
   public void start() {
   }

   private void placeSpark(ServerLevel serverLevel) {
      int maxRetries = 10;

      for (int i = 0; i < maxRetries; i++) {
         BlockPos bossPos = this.boss.getOnPos();
         int x = bossPos.getX() + serverLevel.random.nextInt(this.attributes.radius) - this.attributes.radius / 2;
         int y = bossPos.getY() + 2;
         int z = bossPos.getZ() + serverLevel.random.nextInt(this.attributes.radius) - this.attributes.radius / 2;
         BlockPos placePos = new BlockPos(x, y, z);
         if (serverLevel.getBlockState(placePos).isAir()) {
            serverLevel.setBlockAndUpdate(placePos, ModBlocks.SPARK.defaultBlockState());
            serverLevel.getBlockEntity(placePos, ModBlocks.SPARK_TILE_ENTITY).ifPresent(spark -> spark.setLifetime(this.attributes.sparkLifespan));
            sendBlockUpdatesToClient(serverLevel, placePos);
            this.lastSparkSpawnTime = serverLevel.getGameTime();
            this.currentSparksExpiration = serverLevel.getGameTime() + this.attributes.sparkLifespan;
            this.sparkPositions.add(placePos);
            return;
         }
      }
   }

   private static void sendBlockUpdatesToClient(ServerLevel serverLevel, BlockPos placePos) {
      ChunkPos chunkPos = new ChunkPos(placePos);
      serverLevel.getServer().tell(new TickTask(serverLevel.getServer().getTickCount() + 1, () -> {
         serverLevel.getChunkSource().chunkMap.getPlayers(chunkPos, false).forEach(player -> {
            serverLevel.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);
            ((AccessorChunkMap)serverLevel.getChunkSource().chunkMap).callUpdateChunkTracking(player, chunkPos, new MutableObject(), false, true);
         });
         serverLevel.getChunkSource().blockChanged(placePos);
      }));
   }

   @Override
   public void stop() {
   }

   @Override
   public void finish() {
      if (this.boss.getLevel() instanceof ServerLevel serverLevel) {
         this.removeSparks(serverLevel);
      }

      this.boss.setStunned(false);
   }

   @Override
   public CompoundTag serialize() {
      CompoundTag tag = IBossStage.super.serialize();
      tag.put("Attributes", this.attributes.serialize());
      tag.putInt("SparkCount", this.sparkCount);
      ListTag sparkPositionsTag = new ListTag();

      for (BlockPos sparkPosition : this.sparkPositions) {
         sparkPositionsTag.add(LongTag.valueOf(sparkPosition.asLong()));
      }

      tag.put("SparkPositions", sparkPositionsTag);
      tag.putLong("CurrentSparksExpiration", this.currentSparksExpiration);
      tag.putInt("SparkRemovalTimer", this.sparkRemovalTimer);
      tag.putInt("DamageDealTimer", this.damageDealTimer);
      tag.putInt("StunTimer", this.stunTimer);
      ListTag safePointCentersTag = new ListTag();

      for (BlockPos safePointCenter : this.safePointCenters) {
         safePointCentersTag.add(LongTag.valueOf(safePointCenter.asLong()));
      }

      tag.put("SafePointCenters", safePointCentersTag);
      ListTag safePointOriginalBlocksTag = new ListTag();

      for (Entry<BlockState, Set<BlockPos>> entry : this.safePointOriginalBlocks.entrySet()) {
         CompoundTag safePointOriginalBlockTag = new CompoundTag();
         safePointOriginalBlockTag.putString("BlockState", entry.getKey().getBlock().getRegistryName().toString());
         ListTag positions = new ListTag();

         for (BlockPos safePointOriginalBlockPosition : entry.getValue()) {
            positions.add(LongTag.valueOf(safePointOriginalBlockPosition.asLong()));
         }

         safePointOriginalBlockTag.put("BlockPositions", positions);
         safePointOriginalBlocksTag.add(safePointOriginalBlockTag);
      }

      tag.put("SafePointOriginalBlocks", safePointOriginalBlocksTag);
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
      return new Tuple(62, 330);
   }

   @Override
   public float getProgress() {
      return this.boss.getHealth() / this.attributes.health;
   }

   public static SparkStage fromAttributes(ArtifactBossEntity artifactBossEntity, CompoundTag attributesTag) {
      return new SparkStage(artifactBossEntity, SparkStageAttributes.from(attributesTag));
   }

   public static SparkStage from(ArtifactBossEntity artifactBossEntity, CompoundTag tag) {
      SparkStage stage = fromAttributes(artifactBossEntity, tag.getCompound("Attributes"));
      stage.sparkCount = tag.getInt("SparkCount");

      for (Tag sparkPositionTag : tag.getList("SparkPositions", 4)) {
         stage.sparkPositions.add(BlockPos.of(((LongTag)sparkPositionTag).getAsLong()));
      }

      stage.currentSparksExpiration = tag.getLong("CurrentSparksExpiration");
      stage.sparkRemovalTimer = tag.getInt("SparkRemovalTimer");
      stage.damageDealTimer = tag.getInt("DamageDealTimer");
      stage.stunTimer = tag.getInt("StunTimer");

      for (Tag safePointCenterTag : tag.getList("SafePointCenters", 4)) {
         stage.safePointCenters.add(BlockPos.of(((LongTag)safePointCenterTag).getAsLong()));
      }

      for (Tag safePointOriginalBlockTag : tag.getList("SafePointOriginalBlocks", 10)) {
         CompoundTag safePointOriginalBlockCompoundTag = (CompoundTag)safePointOriginalBlockTag;
         Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(safePointOriginalBlockCompoundTag.getString("BlockState")));
         if (block != null) {
            BlockState blockState = block.defaultBlockState();
            Set<BlockPos> blockPositions = new HashSet<>();

            for (Tag blockPositionTag : safePointOriginalBlockCompoundTag.getList("BlockPositions", 4)) {
               blockPositions.add(BlockPos.of(((LongTag)blockPositionTag).getAsLong()));
            }

            stage.safePointOriginalBlocks.put(blockState, blockPositions);
         }
      }

      return stage;
   }
}
