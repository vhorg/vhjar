package iskallia.vault.block.entity;

import iskallia.vault.VaultMod;
import iskallia.vault.config.EliteSpawnerConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EliteSpawnerTileEntity extends BaseSpawnerTileEntity {
   private String spawnGroupName;

   public EliteSpawnerTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.ELITE_SPAWNER_TILE_ENTITY, pos, state);
   }

   public static void tick(Level level, BlockPos blockPos, EliteSpawnerTileEntity te) {
      if (level instanceof ServerLevel serverLevel
         && level.getNearestPlayer(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, ModConfigs.ELITE_SPAWNER.blockCheckRadius, true)
            != null) {
         EliteSpawnerConfig.SpawnerGroup spawnGroup = ModConfigs.ELITE_SPAWNER.spawnerGroups.get(te.spawnGroupName);
         if (spawnGroup == null) {
            VaultMod.LOGGER.warn("Elite Spawner failed to get spawn group \"{}\" as it does not exist in config", te.spawnGroupName);
         } else {
            int numberOfMinions = serverLevel.random.nextInt(spawnGroup.min, spawnGroup.max + 1);

            for (int i = 0; i < numberOfMinions; i++) {
               EliteSpawnerConfig.SpawnerEntity randomMinion = spawnGroup.minions.getRandom(serverLevel.random);
               if (randomMinion != null) {
                  ResourceLocation entityName = randomMinion.entityName;
                  spawnEntity(blockPos, serverLevel, entityName, randomMinion.entityNbt, true, () -> logEntityTypeMissing(te.spawnGroupName, entityName));
               }
            }

            spawnEntity(
               blockPos,
               serverLevel,
               spawnGroup.elite.entityName,
               spawnGroup.elite.entityNbt,
               true,
               () -> logEntityTypeMissing(te.spawnGroupName, spawnGroup.elite.entityName)
            );
            level.removeBlock(blockPos, false);
         }
      }
   }

   private static void logEntityTypeMissing(String spawnGroupName, ResourceLocation entityName) {
      VaultMod.LOGGER
         .warn("Elite Spawner failed to spawn \"{}\" defined in spawn group \"{}\" as it does not exist in entityType registry", entityName, spawnGroupName);
   }

   public void setSpawnGroupName(String spawnGroupName) {
      this.spawnGroupName = spawnGroupName;
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      tag.putString("spawnGroupName", this.spawnGroupName);
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.spawnGroupName = tag.getString("spawnGroupName");
   }

   public void setRandomSpawn() {
      String[] spawnerGroupNames = ModConfigs.ELITE_SPAWNER.spawnerGroups.keySet().toArray(new String[0]);
      String spawnerGroupName = spawnerGroupNames[this.level.random.nextInt(spawnerGroupNames.length)];
      this.setSpawnGroupName(spawnerGroupName);
      VaultMod.LOGGER.info("Placed Elite Spawner had \"{}\" spawn group set", spawnerGroupName);
   }
}
