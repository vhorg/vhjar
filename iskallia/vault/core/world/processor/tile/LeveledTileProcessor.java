package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LeveledTileProcessor extends TileProcessor {
   public Map<Integer, TileProcessor> levels = new LinkedHashMap<>();

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      TileProcessor processor = null;
      int level = context.getVault() == null ? 0 : context.getVault().get(Vault.LEVEL).get();

      for (Entry<Integer, TileProcessor> entry : this.levels.entrySet()) {
         if (entry.getKey() > level) {
            break;
         }

         processor = entry.getValue();
      }

      return processor == null ? tile : processor.process(tile, context);
   }
}
