package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import java.util.Optional;

public class WeightedTileProcessor extends TargetTileProcessor<WeightedTileProcessor> {
   protected final WeightedList<PartialTile> output = new WeightedList<>();

   public WeightedList<PartialTile> getOutput() {
      return this.output;
   }

   public WeightedTileProcessor into(String target, int weight) {
      PartialTile tile = PartialTile.parse(target, true).orElse(PartialTile.ERROR);
      return this.into(tile, weight);
   }

   public WeightedTileProcessor into(PartialTile tile, int weight) {
      this.output.put(tile, (Number)weight);
      return this;
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      if (this.predicate.test(tile)) {
         Optional<PartialTile> output = this.output.getRandom(context.getRandom(tile.getPos()));
         output.ifPresent(out -> out.fillInto(tile));
      }

      return tile;
   }
}
