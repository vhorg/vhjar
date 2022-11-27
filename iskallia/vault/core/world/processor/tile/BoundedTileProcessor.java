package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;

public class BoundedTileProcessor extends TileProcessor {
   public final int minX;
   public final int minY;
   public final int minZ;
   public final int maxX;
   public final int maxY;
   public final int maxZ;

   public BoundedTileProcessor(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      if (tile.getPos().getX() < this.minX) {
         return null;
      } else if (tile.getPos().getY() < this.minY) {
         return null;
      } else if (tile.getPos().getZ() < this.minZ) {
         return null;
      } else if (tile.getPos().getX() > this.maxX) {
         return null;
      } else if (tile.getPos().getY() > this.maxY) {
         return null;
      } else {
         return tile.getPos().getZ() > this.maxZ ? null : tile;
      }
   }
}
