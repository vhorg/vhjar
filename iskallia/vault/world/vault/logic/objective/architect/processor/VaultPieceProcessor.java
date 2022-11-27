package iskallia.vault.world.vault.logic.objective.architect.processor;

import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import java.util.Random;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public abstract class VaultPieceProcessor {
   protected static final Random rand = new Random();

   public abstract void postProcess(VaultRaid var1, ServerLevel var2, VaultPiece var3, Direction var4);
}
