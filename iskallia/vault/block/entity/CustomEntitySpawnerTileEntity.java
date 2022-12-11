package iskallia.vault.block.entity;

import iskallia.vault.VaultMod;
import iskallia.vault.config.CustomEntitySpawnerConfig;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.ServerVaults;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CustomEntitySpawnerTileEntity extends BaseSpawnerTileEntity {
   private static final int MAX_PLAYER_CHECK_COOOLDOWN = 10;
   @Nullable
   private CustomEntitySpawnerConfig.SpawnerGroup spawnerGroup;
   private String spawnerGroupName;
   private int playerCheckCooldown = 0;
   private long lastCheckTime = -1L;

   public CustomEntitySpawnerTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.CUSTOM_ENTITY_SPAWNER_TILE_ENTITY, pos, state);
   }

   public static void tick(Level level, BlockPos blockPos, CustomEntitySpawnerTileEntity te) {
      if (level instanceof ServerLevel serverLevel) {
         te.initSpawnerGroup();
         if (te.spawnerGroup == null) {
            VaultMod.LOGGER.warn("Custom Entity Spawner failed to spawn as there was no valid spawn group found in config");
            level.removeBlock(blockPos, false);
         } else if (te.lastCheckTime + te.playerCheckCooldown <= level.getGameTime()) {
            te.lastCheckTime = level.getGameTime();
            Player player;
            if ((player = level.getNearestPlayer(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 100.0, true)) != null) {
               double distSqr = player.distanceToSqr(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
               CustomEntitySpawnerConfig.SpawnerGroup spawnerGroup = te.spawnerGroup;
               if (!(spawnerGroup.blockCheckRadius * spawnerGroup.blockCheckRadius < distSqr)) {
                  spawnEntity(level, blockPos, serverLevel, spawnerGroup);
                  level.removeBlock(blockPos, false);
               }
            }
         }
      }
   }

   private static void spawnEntity(Level level, BlockPos blockPos, ServerLevel serverLevel, CustomEntitySpawnerConfig.SpawnerGroup spawnerGroup) {
      CustomEntitySpawnerConfig.SpawnerEntity spawnerEntity = spawnerGroup.entities.getRandom(level.random);
      if (spawnerEntity == null) {
         VaultMod.LOGGER
            .warn("Custom Entity Spawner failed to spawn as there was no valid entity found in config for spawn group with minLevel {}", spawnerGroup.minLevel);
      } else {
         Entity entity = spawnEntity(
            blockPos,
            serverLevel,
            spawnerEntity.type,
            spawnerEntity.nbt,
            false,
            () -> VaultMod.LOGGER.warn("Custom Entity Spawner failed to spawn \"{}\" as it does not exist in entityType registry", spawnerEntity.type)
         );
         if (entity != null && spawnerEntity.randomMotion) {
            float pitch = (float)(level.random.nextFloat() * Math.PI * 2.0);
            float yaw = (float)(level.random.nextFloat() * Math.PI * 2.0);
            float xBaseMotion = -Mth.sin(yaw * (float) (Math.PI / 180.0)) * Mth.cos(pitch * (float) (Math.PI / 180.0));
            float yBaseMotion = -Mth.sin(pitch * (float) (Math.PI / 180.0));
            float zBaseMotion = Mth.cos(yaw * (float) (Math.PI / 180.0)) * Mth.cos(pitch * (float) (Math.PI / 180.0));
            float velocity = level.random.nextFloat() * 2.0F;
            Vec3 deltaMovement = new Vec3(xBaseMotion, yBaseMotion, zBaseMotion).normalize().scale(velocity);
            entity.setDeltaMovement(deltaMovement);
            if (entity instanceof AbstractHurtingProjectile projectile) {
               projectile.xPower = deltaMovement.x();
               projectile.yPower = deltaMovement.y();
               projectile.zPower = deltaMovement.z();
            }
         }
      }
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.putString("spawnerGroupName", this.spawnerGroupName);
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.spawnerGroupName = tag.getString("spawnerGroupName");
   }

   private void initSpawnerGroup() {
      if (this.spawnerGroup == null) {
         int vaultLevel = ServerVaults.get(this.level).map(vault -> vault.get(Vault.LEVEL).get()).orElse(0);
         List<CustomEntitySpawnerConfig.SpawnerGroup> spawnerGroups = ModConfigs.CUSTOM_ENTITY_SPAWNER.spawnerGroups.get(this.spawnerGroupName);
         if (spawnerGroups == null) {
            return;
         }

         for (CustomEntitySpawnerConfig.SpawnerGroup sg : spawnerGroups) {
            if (sg.minLevel <= vaultLevel && (this.spawnerGroup == null || sg.minLevel > this.spawnerGroup.minLevel)) {
               this.spawnerGroup = sg;
            }
         }

         if (this.spawnerGroup != null) {
            this.playerCheckCooldown = Math.min(this.spawnerGroup.blockCheckRadius / 4, 10);
         }
      }
   }

   public void setRandomSpawn() {
      String[] spawnerGroupNames = ModConfigs.CUSTOM_ENTITY_SPAWNER.spawnerGroups.keySet().toArray(new String[0]);
      this.spawnerGroupName = spawnerGroupNames[this.level.random.nextInt(spawnerGroupNames.length)];
      VaultMod.LOGGER.info("Placed Custom Entity Spawner had \"{}\" spawn group set", this.spawnerGroupName);
   }
}
