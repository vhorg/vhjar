package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.block.VaultLootableBlock;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultObelisk;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BossSpawnPieceProcessor extends VaultPieceProcessor {
   private final ArchitectObjective objective;

   public BossSpawnPieceProcessor(ArchitectObjective objective) {
      this.objective = objective;
   }

   @Override
   public void postProcess(VaultRaid vault, ServerWorld world, VaultPiece piece, Direction generatedDirection) {
      if (piece instanceof VaultObelisk) {
         BlockPos stabilizerPos = BlockPos.func_229383_a_(piece.getBoundingBox())
            .map(pos -> new Tuple(pos, world.func_180495_p(pos)))
            .filter(
               tpl -> ((BlockState)tpl.func_76340_b()).func_177230_c() instanceof VaultLootableBlock
                  && ((VaultLootableBlock)((BlockState)tpl.func_76340_b()).func_177230_c()).getType() == VaultLootableBlock.Type.VAULT_OBJECTIVE
            )
            .findFirst()
            .<BlockPos>map(Tuple::func_76341_a)
            .orElse(null);
         if (stabilizerPos != null && world.func_217377_a(stabilizerPos, false)) {
            this.objective.spawnBoss(vault, world, stabilizerPos);
         }
      }
   }
}
