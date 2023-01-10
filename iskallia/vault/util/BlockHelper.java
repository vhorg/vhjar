package iskallia.vault.util;

import java.util.Collection;
import java.util.Stack;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockHelper {
   public static Iterable<BlockPos> getSpherePositions(BlockPos center, float radius) {
      return getEllipsoidPositions(center, radius, radius, radius);
   }

   public static int withSpherePositions(BlockPos center, float radius, Consumer<BlockPos> consumer) {
      return withEllipsoidPositions(center, radius, radius, radius, consumer);
   }

   public static Iterable<BlockPos> getEllipsoidPositions(BlockPos center, float radiusX, float radiusY, float radiusZ) {
      Collection<BlockPos> positions = new Stack<>();
      withEllipsoidPositions(center, radiusX, radiusY, radiusZ, positions::add);
      return positions;
   }

   public static int withEllipsoidPositions(BlockPos center, float radiusX, float radiusY, float radiusZ, Consumer<BlockPos> consumer) {
      int count = 0;
      int rx = Mth.ceil(radiusX);
      int ry = Mth.ceil(radiusY);
      int rz = Mth.ceil(radiusZ);

      for (int x = -rx; x <= rx; x++) {
         for (int y = -ry; y <= ry; y++) {
            for (int z = -rz; z <= rz; z++) {
               double xa = (double)x / rx;
               double ya = (double)y / ry;
               double za = (double)z / rz;
               if (xa * xa + ya * ya + za * za < 1.0) {
                  consumer.accept(new BlockPos(center.getX() + x, center.getY() + y, center.getZ() + z));
                  count++;
               }
            }
         }
      }

      return count;
   }

   public static int withVerticalCylinderPositions(BlockPos center, float radiusX, float height, float radiusZ, Consumer<BlockPos> consumer) {
      int count = 0;
      int rx = Mth.ceil(radiusX);
      int ry = Mth.ceil(height);
      int rz = Mth.ceil(radiusZ);

      for (int x = -rx; x <= rx; x++) {
         for (int y = -ry; y <= ry; y++) {
            for (int z = -rz; z <= rz; z++) {
               double xa = (double)x / rx;
               double za = (double)z / rz;
               if (xa * xa + za * za < 1.0) {
                  consumer.accept(new BlockPos(center.getX() + x, center.getY() + y, center.getZ() + z));
                  count++;
               }
            }
         }
      }

      return count;
   }

   @Nullable
   public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> getTicker(
      BlockEntityType<A> type, BlockEntityType<E> targetType, BlockEntityTicker<? super E> ticker
   ) {
      return targetType == type ? ticker : null;
   }
}
