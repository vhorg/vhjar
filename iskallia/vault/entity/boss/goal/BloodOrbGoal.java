package iskallia.vault.entity.boss.goal;

import iskallia.vault.entity.boss.BloodOrbEntity;
import iskallia.vault.entity.boss.VaultBossBaseEntity;
import iskallia.vault.entity.boss.VaultBossEntity;
import iskallia.vault.entity.boss.trait.ITrait;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModEntities;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;

public class BloodOrbGoal extends Goal implements ITrait {
   private static final TargetingConditions PLAYERS_TARGETING_CONDITIONS = TargetingConditions.forCombat().range(50.0);
   public static final String TYPE = "blood_orb";
   private final VaultBossBaseEntity boss;
   private static final int BLEED_EFFECT_APPLICATION_COOLDOWN = 20;
   private int bloodOrbSpawnIntervalMin = 40;
   private int bloodOrbSpawnIntervalMax = 100;
   private int bloodOrbSpawnRadius = 20;
   private int maxBloodOrbs = 4;
   private Set<UUID> bloodOrbs = new HashSet<>();
   private int bloodOrbSpawnTimer = 0;
   private int bleedEffectTimer = 0;

   public BloodOrbGoal(VaultBossBaseEntity boss) {
      this.boss = boss;
   }

   public BloodOrbGoal setAttributes(int bloodOrbSpawnIntervalMin, int bloodOrbSpawnIntervalMax, int bloodOrbSpawnRadius, int maxBloodOrbs) {
      this.bloodOrbSpawnIntervalMin = bloodOrbSpawnIntervalMin;
      this.bloodOrbSpawnIntervalMax = bloodOrbSpawnIntervalMax;
      this.bloodOrbSpawnRadius = bloodOrbSpawnRadius;
      this.maxBloodOrbs = maxBloodOrbs;
      return this;
   }

   @Override
   public String getType() {
      return "blood_orb";
   }

   @Override
   public void apply(VaultBossEntity boss) {
      boss.addTraitGoal(this);
   }

   @Override
   public void addStack(ITrait trait) {
      if (trait instanceof BloodOrbGoal bloodOrbGoal) {
         int stackSize = bloodOrbGoal.bloodOrbSpawnIntervalMin / this.bloodOrbSpawnIntervalMin;
         this.bloodOrbSpawnIntervalMin = bloodOrbGoal.bloodOrbSpawnIntervalMin * ++stackSize;
         this.bloodOrbSpawnIntervalMax = bloodOrbGoal.bloodOrbSpawnIntervalMax * stackSize;
      }
   }

   public boolean canUse() {
      return true;
   }

   public void tick() {
      super.tick();
      if (this.boss.level instanceof ServerLevel serverLevel) {
         this.bloodOrbs.removeIf(uuid -> serverLevel.getEntity(uuid) == null);
         this.spawnBloodOrb(serverLevel);
         this.applyBleedEffect();
      }
   }

   private void spawnBloodOrb(ServerLevel serverLevel) {
      if (this.bloodOrbSpawnTimer > 0) {
         this.bloodOrbSpawnTimer--;
      }

      if (this.bloodOrbSpawnTimer <= 0) {
         if (this.bloodOrbs.size() < this.maxBloodOrbs) {
            Vec3 spawnPosition = this.boss.getSpawnPosition();
            double x = spawnPosition.x() + this.boss.level.random.nextInt(-this.bloodOrbSpawnRadius, this.bloodOrbSpawnRadius);
            double y = spawnPosition.y() + this.boss.level.random.nextInt(-2, 2);
            double z = spawnPosition.z() + this.boss.level.random.nextInt(-this.bloodOrbSpawnRadius, this.bloodOrbSpawnRadius);
            BlockPos below = new BlockPos(x, y - 1.0, z);

            int retries;
            for (retries = 15;
               retries > 0
                  && (
                     !this.boss.level.noCollision(ModEntities.BLOOD_ORB.getAABB(x, y, z))
                        || !this.boss.level.getBlockState(below).isFaceSturdy(this.boss.level, below, Direction.UP)
                  );
               retries--
            ) {
               x = spawnPosition.x() + this.boss.level.random.nextInt(-this.bloodOrbSpawnRadius, this.bloodOrbSpawnRadius);
               y = spawnPosition.y() + this.boss.level.random.nextInt(-2, 2);
               z = spawnPosition.z() + this.boss.level.random.nextInt(-this.bloodOrbSpawnRadius, this.bloodOrbSpawnRadius);
               below = new BlockPos(x, y - 1.0, z);
            }

            if (retries == 0) {
               return;
            }

            Vec3 bloodOrbPos = new Vec3(x, y, z);
            BloodOrbEntity bloodOrbEntity = new BloodOrbEntity(this.boss.level);
            bloodOrbEntity.setPos(bloodOrbPos);
            bloodOrbEntity.setNoGravity(false);
            serverLevel.addFreshEntity(bloodOrbEntity);
            this.bloodOrbs.add(bloodOrbEntity.getUUID());
         }

         this.setBloodOrbSpawnCooldown();
      }
   }

   private void applyBleedEffect() {
      if (this.bleedEffectTimer > 0) {
         this.bleedEffectTimer--;
      }

      if (this.bleedEffectTimer <= 0 && !this.bloodOrbs.isEmpty()) {
         this.boss
            .level
            .getNearbyPlayers(PLAYERS_TARGETING_CONDITIONS, this.boss, this.boss.getBoundingBox().inflate(60.0))
            .forEach(player -> player.addEffect(new MobEffectInstance(ModEffects.BLEED, 60, this.bloodOrbs.size() - 1), null));
         this.bleedEffectTimer = 20;
      }
   }

   private void setBloodOrbSpawnCooldown() {
      int playerCount = this.boss.getPlayerCount();
      int bloodOrbSpawnCooldown = this.boss.level.getRandom().nextInt(this.bloodOrbSpawnIntervalMin, this.bloodOrbSpawnIntervalMax);
      this.bloodOrbSpawnTimer = Math.max(20, (int)(bloodOrbSpawnCooldown - bloodOrbSpawnCooldown * 0.2 * playerCount));
   }

   public void stop() {
      super.stop();
      this.bloodOrbs.forEach(bloodOrb -> {
         if (this.boss.level instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(bloodOrb);
            if (entity != null) {
               entity.kill();
            }
         }
      });
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = new CompoundTag();
      nbt.putInt("BloodOrbSpawnIntervalMin", this.bloodOrbSpawnIntervalMin);
      nbt.putInt("BloodOrbSpawnIntervalMax", this.bloodOrbSpawnIntervalMax);
      nbt.putInt("BloodOrbSpawnRadius", this.bloodOrbSpawnRadius);
      nbt.putInt("MaxBloodOrbs", this.maxBloodOrbs);
      nbt.put("BloodOrbs", this.serializeBloodOrbs(this.bloodOrbs));
      return nbt;
   }

   private ListTag serializeBloodOrbs(Set<UUID> bloodOrbs) {
      ListTag bloodOrbList = new ListTag();

      for (UUID bloodOrb : bloodOrbs) {
         bloodOrbList.add(NbtUtils.createUUID(bloodOrb));
      }

      return bloodOrbList;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt) {
      this.bloodOrbSpawnIntervalMin = nbt.getInt("BloodOrbSpawnIntervalMin");
      this.bloodOrbSpawnIntervalMax = nbt.getInt("BloodOrbSpawnIntervalMax");
      this.bloodOrbSpawnRadius = nbt.getInt("BloodOrbSpawnRadius");
      this.maxBloodOrbs = nbt.getInt("MaxBloodOrbs");
      if (nbt.contains("BloodOrbs", 9)) {
         this.bloodOrbs = this.deserializeBloodOrbs(nbt.getList("BloodOrbs", 11));
      }
   }

   private Set<UUID> deserializeBloodOrbs(ListTag bloodOrbList) {
      Set<UUID> bloodOrbs = new HashSet<>();

      for (int i = 0; i < bloodOrbList.size(); i++) {
         bloodOrbs.add(NbtUtils.loadUUID(bloodOrbList.get(i)));
      }

      return bloodOrbs;
   }
}
