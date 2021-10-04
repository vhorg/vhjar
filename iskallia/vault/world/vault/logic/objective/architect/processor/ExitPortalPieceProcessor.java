package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.block.VaultPortalBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.gen.PortalPlacer;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.gen.piece.VaultRoom;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class ExitPortalPieceProcessor extends VaultPieceProcessor {
   private static final PortalPlacer EXIT_PORTAL_PLACER = new PortalPlacer(
      (pos, random, facing) -> (BlockState)ModBlocks.VAULT_PORTAL.func_176223_P().func_206870_a(VaultPortalBlock.field_176550_a, facing.func_176740_k()),
      (pos, random, facing) -> MiscUtils.eitherOf(random, Blocks.field_196656_g, Blocks.field_196657_h, Blocks.field_196657_h).func_176223_P()
   );
   private final ArchitectObjective objective;

   public ExitPortalPieceProcessor(ArchitectObjective objective) {
      this.objective = objective;
   }

   @Override
   public void postProcess(VaultRaid vault, ServerWorld world, VaultPiece piece, Direction generatedDirection) {
      if (piece instanceof VaultRoom) {
         Direction portalDir = generatedDirection.func_176746_e();
         VaultRoom room = (VaultRoom)piece;
         BlockPos at = new BlockPos(room.getCenter()).func_177967_a(portalDir, -1);
         this.objective.buildPortal(EXIT_PORTAL_PLACER.place(world, at, portalDir, 3, 5));
      }
   }
}
