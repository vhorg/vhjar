package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.world.level.block.Blocks;

public class StructureVoidTileProcessor extends TileProcessor {
   public PartialTile process(PartialTile tile, ProcessorContext context) {
      return tile.getState().is(Blocks.STRUCTURE_VOID) ? null : tile;
   }
}
