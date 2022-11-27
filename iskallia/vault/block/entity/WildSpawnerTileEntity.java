package iskallia.vault.block.entity;

import iskallia.vault.VaultMod;
import iskallia.vault.config.WildSpawnerConfig;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.data.ServerVaults;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WildSpawnerTileEntity extends BaseSpawnerTileEntity {
   private static final int MAX_PLAYER_CHECK_COOOLDOWN = 10;
   @Nullable
   private WildSpawnerConfig.SpawnerGroup spawnerGroup;
   private int playerCheckCooldown = 0;
   private long lastCheckTime = -1L;

   public WildSpawnerTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.WILD_SPAWNER_TILE_ENTITY, pos, state);
   }

   public static void tick(Level level, BlockPos blockPos, WildSpawnerTileEntity te) {
      if (level instanceof ServerLevel serverLevel) {
         te.initSpawnerGroup();
         if (te.spawnerGroup == null) {
            VaultMod.LOGGER.warn("Wild Spawner failed to spawn as there was no valid spawn group found in config");
            level.removeBlock(blockPos, false);
         } else if (te.lastCheckTime + te.playerCheckCooldown <= level.getGameTime()) {
            te.lastCheckTime = level.getGameTime();
            Player player;
            if ((player = level.getNearestPlayer(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 100.0, true)) != null) {
               double distSqr = player.distanceToSqr(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
               WildSpawnerConfig.SpawnerGroup spawnerGroup = te.spawnerGroup;
               if (!(spawnerGroup.blockCheckRadius * spawnerGroup.blockCheckRadius < distSqr)) {
                  spawnEntity(level, blockPos, serverLevel, spawnerGroup);
                  level.removeBlock(blockPos, false);
               }
            }
         }
      }
   }

   private static void spawnEntity(Level level, BlockPos blockPos, ServerLevel serverLevel, WildSpawnerConfig.SpawnerGroup spawnerGroup) {
      WildSpawnerConfig.SpawnerEntity spawnerEntity = spawnerGroup.entities.getRandom(level.random);
      if (spawnerEntity == null) {
         VaultMod.LOGGER
            .warn("Wild Spawner failed to spawn as there was no valid entity found in config for spawn group with minLevel {}", spawnerGroup.minLevel);
      } else {
         spawnEntity(
            blockPos,
            serverLevel,
            spawnerEntity.type,
            spawnerEntity.nbt,
            false,
            () -> VaultMod.LOGGER.warn("Wild Spawner failed to spawn \"{}\" as it does not exist in entityType registry", spawnerEntity.type)
         );
      }
   }

   private void initSpawnerGroup() {
      if (this.spawnerGroup == null) {
         int vaultLevel = ServerVaults.get(this.level).map(vault -> vault.get(Vault.LEVEL).get()).orElse(0);

         for (WildSpawnerConfig.SpawnerGroup sg : ModConfigs.WILD_SPAWNER.spawnerGroups) {
            if (sg.minLevel <= vaultLevel && (this.spawnerGroup == null || sg.minLevel > this.spawnerGroup.minLevel)) {
               this.spawnerGroup = sg;
            }
         }

         if (this.spawnerGroup != null) {
            this.playerCheckCooldown = Math.min(this.spawnerGroup.blockCheckRadius / 4, 10);
         }
      }
   }
}
