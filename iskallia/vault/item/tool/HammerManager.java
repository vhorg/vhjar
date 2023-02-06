package iskallia.vault.item.tool;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;

public class HammerManager {
   public List<HammerTile> tiles = new ArrayList<>();

   public boolean contains(BlockPos pos) {
      return this.tiles.stream().anyMatch(tile -> tile.destroyPos.equals(pos));
   }
}
