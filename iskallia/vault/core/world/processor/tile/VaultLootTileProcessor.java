package iskallia.vault.core.world.processor.tile;

import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.common.PlaceholderGenerationEvent;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import iskallia.vault.init.ModBlocks;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class VaultLootTileProcessor extends TileProcessor {
   public PlaceholderBlock.Type target;
   public Map<Integer, TileProcessor> levels = new LinkedHashMap<>();

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      if (!tile.getState().is(ModBlocks.PLACEHOLDER)) {
         return tile;
      } else if (tile.getState().get(PlaceholderBlock.TYPE) != this.target) {
         return tile;
      } else {
         Direction facing = tile.getState().get(PlaceholderBlock.FACING);
         if (facing != null && facing.getAxis() != Axis.Y) {
            tile.getState().set(BlockStateProperties.HORIZONTAL_FACING, facing);
         } else {
            Direction randomFacing = Direction.from2DDataValue(context.random.nextInt(4));
            tile.getState().set(BlockStateProperties.HORIZONTAL_FACING, randomFacing);
         }

         TileProcessor processor = null;
         int level = context.vault == null ? 0 : context.vault.get(Vault.LEVEL).get();

         for (Entry<Integer, TileProcessor> entry : this.levels.entrySet()) {
            if (entry.getKey() > level) {
               break;
            }

            processor = entry.getValue();
         }

         if (processor instanceof BernoulliWeightedTileProcessor bernoulli) {
            PlaceholderGenerationEvent.Data result = CommonEvents.PLACEHOLDER_GENERATION
               .invoke(context.vault, this, tile, bernoulli.probability, bernoulli.success, bernoulli.failure);
            return bernoulli.process(tile, result.getProbability(), context);
         } else {
            return processor == null ? tile : processor.process(tile, context);
         }
      }
   }
}
