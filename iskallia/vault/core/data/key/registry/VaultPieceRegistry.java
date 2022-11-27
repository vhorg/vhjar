package iskallia.vault.core.data.key.registry;

import iskallia.vault.core.world.generator.piece.VaultPiece;
import java.util.function.Supplier;

public class VaultPieceRegistry extends KeyRegistry<VaultPiece.Key, Supplier<VaultPiece>> {
   public VaultPieceRegistry add(VaultPiece.Key key) {
      this.register(key);
      return this;
   }
}
