package iskallia.vault.world.gen;

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

   public void place(IWorld world, BlockPos pos, Direction facing, int width, int height) {
      pos = pos.func_177972_a(Direction.DOWN).func_177972_a(facing.func_176734_d());

      for (int y = 0; y < height + 2; y++) {
         this.place(world, pos.func_177981_b(y), facing, this.framePlacer);
         this.place(world, pos.func_177967_a(facing, width + 1).func_177981_b(y), facing, this.framePlacer);

         for (int x = 1; x < width + 1; x++) {
            this.place(world, pos.func_177967_a(facing, x).func_177981_b(y), facing, y != 0 && y != height + 1 ? this.portalPlacer : this.framePlacer);
         }
      }
   }

   protected void place(IWorld world, BlockPos pos, BlockState state) {
      if (state != null) {
         world.func_180501_a(pos, state, 1);
      }
   }

   protected void place(IWorld world, BlockPos pos, Direction direction, BlockPlacer provider) {
      this.place(world, pos, provider.getState(pos, world.func_201674_k(), direction));
   }
}
