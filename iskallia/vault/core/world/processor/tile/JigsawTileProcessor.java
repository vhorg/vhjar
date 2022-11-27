package iskallia.vault.core.world.processor.tile;

import com.mojang.brigadier.StringReader;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.data.TileParser;
import iskallia.vault.core.world.processor.ProcessorContext;
import iskallia.vault.init.ModBlocks;
import net.minecraft.world.level.block.Blocks;

public class JigsawTileProcessor extends TileProcessor {
   public PartialTile process(PartialTile tile, ProcessorContext context) {
      if (tile.getState().getBlock() == Blocks.JIGSAW && tile.getNbt() != null && tile.getNbt().contains("final_state")) {
         StringReader input = new StringReader(tile.getNbt().getString("final_state"));
         PartialTile replacement = new TileParser(input, ModBlocks.ERROR_BLOCK, true).toTile();
         replacement.fillMissing(tile);
         return replacement;
      } else {
         return tile;
      }
   }
}
