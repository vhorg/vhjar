package iskallia.vault.util;

import java.util.Collection;
import java.util.Stack;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockHelper {
   public static Iterable<BlockPos> getSphericalPositions(BlockPos center, float radius) {
      return getOvalPositions(center, radius, radius);
   }

   public static Iterable<BlockPos> getOvalPositions(BlockPos center, float widthRadius, float heightRadius) {
      Collection<BlockPos> positions = new Stack<>();
      int wRadius = Mth.ceil(widthRadius);
      int hRadius = Mth.ceil(heightRadius);
      BlockPos pos = BlockPos.ZERO;

      for (int xx = -wRadius; xx <= wRadius; xx++) {
         for (int yy = -hRadius; yy <= hRadius; yy++) {
            for (int zz = -wRadius; zz <= wRadius; zz++) {
               if (pos.distSqr(new Vec3i(xx + 0.5F, yy + 0.5F, zz + 0.5F)) <= Math.max(widthRadius, heightRadius)) {
                  positions.add(pos.offset(center).offset(xx, yy, zz));
               }
            }
         }
      }

      return positions;
   }

   @Nullable
   public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> getTicker(
      BlockEntityType<A> type, BlockEntityType<E> targetType, BlockEntityTicker<? super E> ticker
   ) {
      return targetType == type ? ticker : null;
   }
}
