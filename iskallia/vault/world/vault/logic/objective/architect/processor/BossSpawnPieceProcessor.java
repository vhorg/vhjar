package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultObelisk;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class BossSpawnPieceProcessor extends VaultPieceProcessor {
   private final ArchitectObjective objective;

   public BossSpawnPieceProcessor(ArchitectObjective objective) {
      this.objective = objective;
   }

   @Override
   public void postProcess(VaultRaid vault, ServerLevel world, VaultPiece piece, Direction generatedDirection) {
      if (piece instanceof VaultObelisk) {
         ;
      }
   }
}
