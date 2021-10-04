package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import java.util.Random;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;

public abstract class VaultPieceProcessor {
   protected static final Random rand = new Random();

   public abstract void postProcess(VaultRaid var1, ServerWorld var2, VaultPiece var3, Direction var4);
}
