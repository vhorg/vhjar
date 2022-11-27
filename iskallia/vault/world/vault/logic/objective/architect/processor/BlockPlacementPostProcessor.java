package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultObelisk;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BlockPlacementPostProcessor extends VaultPieceProcessor {
   private final BlockState toPlace;
   private final int blocksPerSpawn;

   public BlockPlacementPostProcessor(BlockState toPlace, int blocksPerSpawn) {
      this.toPlace = toPlace;
      this.blocksPerSpawn = blocksPerSpawn;
   }

   @Override
   public void postProcess(VaultRaid vault, ServerLevel world, VaultPiece piece, Direction generatedDirection) {
      if (!(piece instanceof VaultObelisk)) {
         AABB box = AABB.of(piece.getBoundingBox());
         float size = (float)((box.maxX - box.minX) * (box.maxY - box.minY) * (box.maxZ - box.minZ));
         float runs = size / this.blocksPerSpawn;

         while (runs > 0.0F && (!(runs < 1.0F) || !(rand.nextFloat() >= runs))) {
            runs--;
            boolean placed = false;

            while (!placed) {
               BlockPos pos = MiscUtils.getRandomPos(box, rand);
               BlockState state = world.getBlockState(pos);
               if (state.isAir() && world.getBlockState(pos.below()).isFaceSturdy(world, pos, Direction.UP) && world.setBlock(pos, this.toPlace, 2)) {
                  placed = true;
               }
            }
         }
      }
   }
}
