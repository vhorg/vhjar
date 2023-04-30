package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;

public class JigsawTileProcessor extends TileProcessor {
   public PartialTile process(PartialTile tile, ProcessorContext context) {
      if (tile.getState().is(Blocks.JIGSAW)) {
         CompoundTag nbt = tile.getEntity().asWhole().orElse(null);
         if (nbt != null && nbt.contains("final_state")) {
            PartialTile replacement = PartialTile.parse(nbt.getString("final_state"), true).orElse(PartialTile.ERROR);
            replacement.fillInto(tile);
         }
      }

      return tile;
   }
}
