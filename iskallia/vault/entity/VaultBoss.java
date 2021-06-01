package iskallia.vault.entity;

import iskallia.vault.world.raid.VaultRaid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

public interface VaultBoss {
   void spawnInTheWorld(VaultRaid var1, ServerWorld var2, BlockPos var3);

   ServerBossInfo getServerBossInfo();
}
