package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;

public class MirrorTileProcessor extends TileProcessor {
   public final Mirror mirror;
   public final int plane;
   public final boolean centered;
   private final int term;

   public MirrorTileProcessor(Mirror mirror, int plane, boolean centered) {
      this.mirror = mirror;
      this.plane = plane;
      this.centered = centered;
      this.term = this.plane + this.plane - (this.centered ? 0 : 1);
   }

   public BlockPos transform(BlockPos pos) {
      return switch (this.mirror) {
         case FRONT_BACK -> new BlockPos(this.term - pos.getX(), pos.getY(), pos.getZ());
         case LEFT_RIGHT -> new BlockPos(pos.getX(), pos.getY(), this.term - pos.getZ());
         case NONE -> pos;
         default -> throw new IncompatibleClassChangeError();
      };
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      tile.setPos(this.transform(tile.getPos()));
      tile.getState().mirror(this.mirror);
      return tile;
   }
}
