package iskallia.vault.core.world.data.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelReader;

public interface TilePlacement<T> extends TilePredicate {
   boolean isSubsetOf(T var1);

   boolean isSubsetOf(LevelReader var1, BlockPos var2);

   void fillInto(T var1);

   void place(CommonLevelAccessor var1, BlockPos var2, int var3);

   T copy();
}
