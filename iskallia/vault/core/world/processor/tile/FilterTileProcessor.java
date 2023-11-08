package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.processor.ProcessorContext;

public class FilterTileProcessor extends TileProcessor {
   private final TilePredicate target;

   public FilterTileProcessor(TilePredicate target) {
      this.target = target;
   }

   public FilterTileProcessor(String target) {
      this(TilePredicate.of(target, true).orElse(TilePredicate.FALSE));
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      return this.target.test(tile) ? null : tile;
   }
}
