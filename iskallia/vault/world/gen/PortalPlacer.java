package iskallia.vault.world.gen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class PortalPlacer {
   private final BlockPlacer portalPlacer;
   private final BlockPlacer framePlacer;

   public PortalPlacer(BlockPlacer portal, BlockPlacer frame) {
      this.portalPlacer = portal;
      this.framePlacer = frame;
   }

   public List<BlockPos> place(IWorld world, BlockPos pos, Direction facing, int width, int height) {
      pos = pos.func_177972_a(Direction.DOWN).func_177972_a(facing.func_176734_d());
      List<BlockPos> portalPlacements = new ArrayList<>();

      for (int y = 0; y < height + 2; y++) {
         this.place(world, pos.func_177981_b(y), facing, this.framePlacer);
         this.place(world, pos.func_177967_a(facing, width + 1).func_177981_b(y), facing, this.framePlacer);

         for (int x = 1; x < width + 1; x++) {
            if (y != 0 && y != height + 1) {
               BlockPos placePos = pos.func_177967_a(facing, x).func_177981_b(y);
               if (this.place(world, placePos, facing, this.portalPlacer)) {
                  portalPlacements.add(placePos);
               }
            } else {
               this.place(world, pos.func_177967_a(facing, x).func_177981_b(y), facing, this.framePlacer);
            }
         }
      }

      return portalPlacements;
   }

   protected boolean place(IWorld world, BlockPos pos, Direction direction, BlockPlacer provider) {
      return this.place(world, pos, provider.getState(pos, world.func_201674_k(), direction));
   }

   protected boolean place(IWorld world, BlockPos pos, BlockState state) {
      return state != null ? world.func_180501_a(pos, state, 3) : false;
   }
}
