package iskallia.vault.entity.boss;

import iskallia.vault.block.VaultChestBlock;
import iskallia.vault.client.gui.helper.Easing;
import iskallia.vault.core.vault.influence.VaultGod;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

public class CatalystStage implements IBossStage {
   public static final String NAME = "catalyst";
   private static final ResourceLocation BOSS_TEXTURE = new ResourceLocation("the_vault", "textures/entity/boss/artifact_boss_tenos.png");
   private static final Set<Flag> FLAGS = Set.of(Flag.MOVE, Flag.TARGET);
   private static final TargetingConditions PLAYERS_TARGETING_CONDITIONS = TargetingConditions.forCombat().range(50.0);
   private static final int FLOAT_LEVEL = 10;
   private static final int MOVE_TIME = 40;
   private static final int PROJECTILE_COOLDOWN = 20;
   private final ArtifactBossEntity boss;
   private int projectileTimer = 20;
   private final CatalystStageAttributes attributes;
   private int catalystWaveIndex = -1;
   private int currentWaveCatalystCount = 0;
   private Set<UUID> catalystIds = new HashSet<>();
   private boolean finished = false;
   private boolean movingUp = false;
   private boolean movingDown = false;
   private int moveTimer = 0;
   private int projectilesToShoot = 1;

   public CatalystStage(ArtifactBossEntity boss, CatalystStageAttributes attributes) {
      this.boss = boss;
      this.attributes = attributes;
      this.setProjectilesToShoot(boss);
   }

   private void setProjectilesToShoot(ArtifactBossEntity boss) {
      this.projectilesToShoot = 1 + boss.level.random.nextInt(3);
   }

   @Override
   public void tick() {
      if (!this.boss.level.isClientSide()) {
         if (this.movingUp) {
            this.moveTimer--;
            float yOffset = 10.0F * (1.0F - Easing.EASE_IN_OUT_SINE.calc(this.moveTimer / 40.0F));
            this.boss.setPos(this.boss.getSpawnPosition().x, this.boss.getSpawnPosition().y + yOffset, this.boss.getSpawnPosition().z);
            if (this.moveTimer > 0) {
               return;
            }

            this.movingUp = false;
         } else if (this.movingDown) {
            this.moveTimer--;
            float yOffset = 10.0F * Easing.EASE_IN_OUT_SINE.calc(this.moveTimer / 40.0F);
            this.boss.setPos(this.boss.getSpawnPosition().x, this.boss.getSpawnPosition().y + yOffset, this.boss.getSpawnPosition().z);
            if (this.moveTimer > 0) {
               return;
            }

            this.movingDown = false;
            this.finished = true;
         }

         if (this.boss.level instanceof ServerLevel serverLevel) {
            this.catalystIds.removeIf(id -> serverLevel.getEntity(id) == null);
         }

         if (this.catalystIds.isEmpty()) {
            if (this.catalystWaveIndex < this.attributes.getCatalystWaves().size() - 1) {
               this.catalystWaveIndex++;
               this.spawnCatalysts();
            } else {
               this.movingDown = true;
               this.moveTimer = 40;
            }
         }

         this.projectileTimer--;
         if (this.projectileTimer <= (this.projectilesToShoot - 1) * 2) {
            this.shootAtPlayers((this.projectilesToShoot - 1) * 0.6F);
            this.projectilesToShoot--;
            if (this.projectilesToShoot <= 0) {
               this.projectileTimer = 20;
               this.setProjectilesToShoot(this.boss);
            }
         }
      }
   }

   private void shootAtPlayers(float inaccuracy) {
      Level level = this.boss.getLevel();
      level.getNearbyPlayers(PLAYERS_TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(60.0))
         .forEach(
            player -> {
               double maxInaccuracy = Math.sqrt(Math.sqrt(this.boss.distanceToSqr(player))) * inaccuracy;
               double x = player.getX() - this.boss.getX() + this.boss.level.random.nextGaussian() * maxInaccuracy;
               double y = player.getY(0.5) - this.boss.getY(0.5);
               double z = player.getZ() - this.boss.getZ() + this.boss.level.random.nextGaussian() * maxInaccuracy;
               ConjurationMagicProjectileEntity magicProjectile = new ConjurationMagicProjectileEntity(
                  this.boss.getLevel(), this.boss, x, y, z, player, this.attributes
               );
               magicProjectile.setColor(VaultGod.TENOS.getColor());
               magicProjectile.setPos(magicProjectile.getX(), this.boss.getY(0.5) + 0.5, magicProjectile.getZ());
               this.boss.getLevel().addFreshEntity(magicProjectile);
            }
         );
   }

   private void spawnCatalysts() {
      CatalystStageAttributes.CatalystWave wave = this.attributes.getCatalystWaves().get(this.catalystWaveIndex);
      this.currentWaveCatalystCount = this.boss.level.random.nextInt(wave.minCatalysts(), wave.maxCatalysts() + 1);
      double angle = (Math.PI * 2) / this.currentWaveCatalystCount;

      for (int i = 0; i < this.currentWaveCatalystCount; i++) {
         double catalystAngle = i * angle;
         BossProtectionCatalystEntity.CatalystType[] catalystTypes = BossProtectionCatalystEntity.CatalystType.values();
         BossProtectionCatalystEntity.CatalystType catalystType = catalystTypes[this.boss.level.random.nextInt(catalystTypes.length)];
         BossProtectionCatalystEntity catalyst = new BossProtectionCatalystEntity(
            this.boss.level, this.boss.getSpawnPosition(), (float)catalystAngle, catalystType, this.attributes.getExplosionDamageMultiplier()
         );
         this.boss.level.addFreshEntity(catalyst);
         this.catalystIds.add(catalyst.getUUID());
      }
   }

   @Override
   public boolean isFinished() {
      return this.finished;
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
      this.movingUp = true;
      this.moveTimer = 40;
      this.boss.setNoGravity(true);
   }

   @Override
   public void stop() {
      this.boss.setNoGravity(false);
      this.clearChests();
   }

   private void clearChests() {
      if (!this.boss.level.isClientSide()) {
         BlockPos pos = this.boss.getOnPos();
         BlockPos.betweenClosed(pos.offset(-50, 0, -50), pos.offset(50, 3, 50)).forEach(blockPos -> {
            if (this.boss.level.getBlockState(blockPos).getBlock() instanceof VaultChestBlock) {
               this.boss.level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
            }
         });
      }
   }

   @Override
   public String getName() {
      return "catalyst";
   }

   @Override
   public Optional<AnimationBuilder> getAnimation() {
      return Optional.of(ArtifactBossEntity.SUMMON_ANIM);
   }

   @Override
   public Optional<ResourceLocation> getTextureLocation() {
      return Optional.of(BOSS_TEXTURE);
   }

   @Override
   public Tuple<Integer, Integer> getBossBarTextureVs() {
      return new Tuple(124, 341);
   }

   @Override
   public float getProgress() {
      if (this.catalystWaveIndex == -1) {
         return 1.0F;
      } else {
         int waveCount = this.attributes.getCatalystWaves().size();
         float singleWaveProgress = 1.0F / waveCount;
         return (waveCount - this.catalystWaveIndex) * singleWaveProgress
            - singleWaveProgress * (1.0F - (float)this.catalystIds.size() / this.currentWaveCatalystCount);
      }
   }

   public static CatalystStage fromAttributes(ArtifactBossEntity artifactBossEntity, CompoundTag attributesTag) {
      return new CatalystStage(artifactBossEntity, CatalystStageAttributes.from(attributesTag));
   }

   public static CatalystStage from(ArtifactBossEntity artifactBossEntity, CompoundTag tag) {
      CatalystStage catalystStage = fromAttributes(artifactBossEntity, tag.getCompound("Attributes"));
      catalystStage.catalystWaveIndex = tag.getInt("CatalystWaveIndex");
      catalystStage.currentWaveCatalystCount = tag.getInt("CurrentWaveCatalystCount");
      catalystStage.catalystIds = deserializeCatalystIds(tag.getList("CatalystIds", 11));
      catalystStage.movingUp = tag.getBoolean("MovingUp");
      catalystStage.movingDown = tag.getBoolean("MovingDown");
      catalystStage.moveTimer = tag.getInt("MoveTimer");
      return catalystStage;
   }

   @Override
   public CompoundTag serialize() {
      CompoundTag tag = IBossStage.super.serialize();
      tag.put("Attributes", this.attributes.serialize());
      tag.putInt("CatalystWaveIndex", this.catalystWaveIndex);
      tag.putInt("CurrentWaveCatalystCount", this.currentWaveCatalystCount);
      tag.put("CatalystIds", this.serializeCatalystIds());
      tag.putBoolean("MovingUp", this.movingUp);
      tag.putBoolean("MovingDown", this.movingDown);
      tag.putInt("MoveTimer", this.moveTimer);
      return tag;
   }

   private ListTag serializeCatalystIds() {
      ListTag listTag = new ListTag();

      for (UUID id : this.catalystIds) {
         listTag.add(NbtUtils.createUUID(id));
      }

      return listTag;
   }

   private static Set<UUID> deserializeCatalystIds(ListTag listTag) {
      Set<UUID> catalystIds = new HashSet<>();

      for (Tag tag : listTag) {
         catalystIds.add(NbtUtils.loadUUID(tag));
      }

      return catalystIds;
   }
}
