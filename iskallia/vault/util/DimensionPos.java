package iskallia.vault.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record DimensionPos(ResourceKey<Level> dimension, BlockPos pos) {
   public boolean isInRange(ResourceKey<Level> dimension, BlockPos pos, int range) {
      return this.dimension.equals(dimension) && this.pos.closerThan(pos, range);
   }
}
