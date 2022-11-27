package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ExitPortalPieceProcessor extends VaultPieceProcessor {
   private static final PortalPlacer EXIT_PORTAL_PLACER = new PortalPlacer(
      (pos, random, facing) -> (BlockState)ModBlocks.VAULT_PORTAL.defaultBlockState().setValue(VaultPortalBlock.AXIS, facing.getAxis()),
      (pos, random, facing) -> MiscUtils.eitherOf(random, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_ANDESITE).defaultBlockState()
   );
   private final ArchitectObjective objective;

   public ExitPortalPieceProcessor(ArchitectObjective objective) {
      this.objective = objective;
   }

   @Override
   public void postProcess(VaultRaid vault, ServerLevel world, VaultPiece piece, Direction generatedDirection) {
      if (piece instanceof VaultRoom) {
         Direction portalDir = generatedDirection.getClockWise();
         VaultRoom room = (VaultRoom)piece;
         BlockPos at = new BlockPos(room.getCenter()).relative(portalDir, -1);
         this.objective.buildPortal(EXIT_PORTAL_PLACER.place(world, at, portalDir, 3, 5));
      }
   }
}
