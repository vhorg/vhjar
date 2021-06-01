package iskallia.vault.world.gen;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface BlockPlacer {
   BlockState getState(BlockPos var1, Random var2, Direction var3);
}
