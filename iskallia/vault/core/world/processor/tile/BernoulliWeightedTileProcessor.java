package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.processor.ProcessorContext;
import java.util.Optional;

public class BernoulliWeightedTileProcessor extends TargetTileProcessor<WeightedTileProcessor> {
   public TilePredicate target = TilePredicate.TRUE;
   public double probability = 0.0;
   public WeightedList<PartialTile> success = new WeightedList<>();
   public WeightedList<PartialTile> failure = new WeightedList<>();

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      return this.process(tile, this.probability, context);
   }

   public PartialTile process(PartialTile tile, double probability, ProcessorContext context) {
      if (this.target.test(tile)) {
         WeightedList<PartialTile> pool = this.failure;
         if (context.getRandom(tile.getPos()).nextFloat() < probability) {
            pool = this.success;
         }

         Optional<PartialTile> output = pool.getRandom(context.getRandom(tile.getPos()));
         output.ifPresent(out -> out.fillInto(tile));
      }

      return tile;
   }
}
