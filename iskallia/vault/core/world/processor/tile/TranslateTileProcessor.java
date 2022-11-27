package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.core.BlockPos;

public class TranslateTileProcessor extends TileProcessor {
   public final int offsetX;
   public final int offsetY;
   public final int offsetZ;

   public TranslateTileProcessor(int offsetX, int offsetY, int offsetZ) {
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.offsetZ = offsetZ;
   }

   public BlockPos translate(BlockPos pos) {
      return new BlockPos(pos.getX() + this.offsetX, pos.getY() + this.offsetY, pos.getZ() + this.offsetZ);
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      tile.setPos(this.translate(tile.getPos()));
      return tile;
   }
}
