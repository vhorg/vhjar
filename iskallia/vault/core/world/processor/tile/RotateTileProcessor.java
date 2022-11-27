package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.processor.ProcessorContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;

public class RotateTileProcessor extends TileProcessor {
   public final Rotation rotation;
   public final int pivotX;
   public final int pivotZ;
   public final boolean centered;
   private final int termX;
   private final int termZ;

   public RotateTileProcessor(Rotation rotation, int pivotX, int pivotZ, boolean centered) {
      this.rotation = rotation;
      this.pivotX = pivotX;
      this.pivotZ = pivotZ;
      this.centered = centered;
      switch (this.rotation) {
         case COUNTERCLOCKWISE_90:
            this.termX = this.pivotX - this.pivotZ - (this.centered ? 0 : 1);
            this.termZ = this.pivotX + this.pivotZ;
            break;
         case CLOCKWISE_90:
            this.termX = this.pivotX + this.pivotZ;
            this.termZ = this.pivotZ - this.pivotX - (this.centered ? 0 : 1);
            break;
         case CLOCKWISE_180:
            this.termX = this.pivotX + this.pivotX - (this.centered ? 0 : 1);
            this.termZ = this.pivotZ + this.pivotZ - (this.centered ? 0 : 1);
            break;
         default:
            this.termX = 0;
            this.termZ = 0;
      }
   }

   public BlockPos transform(BlockPos pos) {
      return switch (this.rotation) {
         case COUNTERCLOCKWISE_90 -> new BlockPos(this.termX + pos.getZ(), pos.getY(), this.termZ - pos.getX());
         case CLOCKWISE_90 -> new BlockPos(this.termX - pos.getZ(), pos.getY(), this.termZ + pos.getX());
         case CLOCKWISE_180 -> new BlockPos(this.termX - pos.getX(), pos.getY(), this.termZ - pos.getZ());
         default -> pos;
      };
   }

   public PartialTile process(PartialTile tile, ProcessorContext context) {
      tile.setPos(this.transform(tile.getPos()));
      tile.getState().rotate(this.rotation);
      return tile;
   }
}
