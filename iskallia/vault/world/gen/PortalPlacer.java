package iskallia.vault.world.gen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class PortalPlacer {
   private final BlockPlacer portalPlacer;
   private final BlockPlacer framePlacer;

   public PortalPlacer(BlockPlacer portal, BlockPlacer frame) {
      this.portalPlacer = portal;
      this.framePlacer = frame;
   }

   public List<BlockPos> place(LevelAccessor world, BlockPos pos, Direction facing, int width, int height) {
      pos = pos.relative(Direction.DOWN).relative(facing.getOpposite());
      List<BlockPos> portalPlacements = new ArrayList<>();

      for (int y = 0; y < height + 2; y++) {
         this.place(world, pos.above(y), facing, this.framePlacer);
         this.place(world, pos.relative(facing, width + 1).above(y), facing, this.framePlacer);

         for (int x = 1; x < width + 1; x++) {
            if (y != 0 && y != height + 1) {
               BlockPos placePos = pos.relative(facing, x).above(y);
               if (this.place(world, placePos, facing, this.portalPlacer)) {
                  portalPlacements.add(placePos);
               }
            } else {
               this.place(world, pos.relative(facing, x).above(y), facing, this.framePlacer);
            }
         }
      }

      return portalPlacements;
   }

   protected boolean place(LevelAccessor world, BlockPos pos, Direction direction, BlockPlacer provider) {
      return this.place(world, pos, provider.getState(pos, world.getRandom(), direction));
   }

   protected boolean place(LevelAccessor world, BlockPos pos, BlockState state) {
      return state != null ? world.setBlock(pos, state, 3) : false;
   }
}
