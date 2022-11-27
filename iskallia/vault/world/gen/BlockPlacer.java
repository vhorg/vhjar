package iskallia.vault.world.gen;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BlockPlacer {
   BlockState getState(BlockPos var1, Random var2, Direction var3);
}
