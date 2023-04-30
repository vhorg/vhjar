package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;

public class IdentityTileProcessor extends TileProcessor {
   public PartialTile process(PartialTile tile, ProcessorContext context) {
      return tile;
   }
}
