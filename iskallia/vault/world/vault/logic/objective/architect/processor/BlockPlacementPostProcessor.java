package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.block.entity.VaultChestTileEntity;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultObelisk;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlockPlacementPostProcessor extends VaultPieceProcessor {
   private final BlockState toPlace;
   private final int blocksPerSpawn;

   public BlockPlacementPostProcessor(BlockState toPlace, int blocksPerSpawn) {
      this.toPlace = toPlace;
      this.blocksPerSpawn = blocksPerSpawn;
   }

   @Override
   public void postProcess(VaultRaid vault, ServerWorld world, VaultPiece piece, Direction generatedDirection) {
      if (!(piece instanceof VaultObelisk)) {
         AxisAlignedBB box = AxisAlignedBB.func_216363_a(piece.getBoundingBox());
         float size = (float)((box.field_72336_d - box.field_72340_a) * (box.field_72337_e - box.field_72338_b) * (box.field_72334_f - box.field_72339_c));
         float runs = size / this.blocksPerSpawn;

         while (runs > 0.0F && (!(runs < 1.0F) || !(rand.nextFloat() >= runs))) {
            runs--;
            boolean placed = false;

            while (!placed) {
               BlockPos pos = MiscUtils.getRandomPos(box, rand);
               BlockState state = world.func_180495_p(pos);
               if (state.isAir(world, pos)
                  && world.func_180495_p(pos.func_177977_b()).func_224755_d(world, pos, Direction.UP)
                  && world.func_180501_a(pos, this.toPlace, 2)) {
                  placed = true;
                  TileEntity te = world.func_175625_s(pos);
                  if (te instanceof VaultChestTileEntity) {
                     VaultChestTileEntity chest = (VaultChestTileEntity)te;
                     chest.getRarityPool().put(VaultRarity.COMMON, 1);
                  }
               }
            }
         }
      }
   }
}
