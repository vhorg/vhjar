package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultObelisk;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.logic.VaultSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class VaultSpawnerSpawningPostProcessor extends VaultPieceProcessor {
   private final int blocksPerSpawn;

   public VaultSpawnerSpawningPostProcessor(int blocksPerSpawn) {
      this.blocksPerSpawn = blocksPerSpawn;
   }

   @Override
   public void postProcess(VaultRaid vault, ServerLevel world, VaultPiece piece, Direction generatedDirection) {
      if (!(piece instanceof VaultObelisk)) {
         vault.getProperties().getBase(VaultRaid.LEVEL).ifPresent(vaultLevel -> {
            AABB box = AABB.of(piece.getBoundingBox());
            float size = (float)((box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ));
            float runs = size / this.blocksPerSpawn;

            while (runs > 0.0F && (!(runs < 1.0F) || !(rand.nextFloat() >= runs))) {
               runs--;
               LivingEntity spawned = null;

               while (spawned == null) {
                  BlockPos pos = MiscUtils.getRandomPos(box, rand);
                  spawned = VaultSpawner.spawnMob(vault, world, vaultLevel, pos.getX(), pos.getY(), pos.getZ(), rand);
               }
            }
         });
      }
   }
}
