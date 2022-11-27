package iskallia.vault.world.vault.gen.layout;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;

public class SpiralHelper {
   public static Vec3i getSpiralPosition(int index, Direction facing, Rotation rotation) {
      return switch (rotation) {
         case CLOCKWISE_90 -> getSpiralPosition(index, facing, facing.getClockWise());
         case COUNTERCLOCKWISE_90 -> getSpiralPosition(index, facing, facing.getCounterClockWise());
         default -> throw new UnsupportedOperationException("Spiral does not support " + rotation + " rotation");
      };
   }

   public static Vec3i getSpiralPosition(int index, Direction facing, Direction rotation) {
      int k = (int)Math.ceil((Math.sqrt(index + 1) - 1.0) / 2.0);
      int a = 2 * k;
      int b = (a + 1) * (a + 1);
      int x;
      int y;
      if (index + 1 >= b - a) {
         x = -(y = -k) - (b - index - 1);
      } else {
         b -= a;
         if (index + 1 >= b - a) {
            y = (x = -k) + (b - index - 1);
         } else {
            b -= a;
            if (index + 1 >= b - a) {
               y = k;
               x = -k + (b - index - 1);
            } else {
               x = k;
               y = k - (b - index - a - 1);
            }
         }
      }

      switch (facing) {
         case EAST:
            if (rotation == Direction.NORTH) {
               y *= -1;
            }
            break;
         case WEST:
            x *= -1;
            if (rotation == Direction.NORTH) {
               y *= -1;
            }
            break;
         case NORTH:
            int tempx = x;
            x = y;
            y = tempx;
            if (rotation == Direction.EAST) {
               x *= -1;
            }
            break;
         case SOUTH:
            int temp = x;
            x = y;
            y = -temp;
            if (rotation == Direction.EAST) {
               x *= -1;
            }
            break;
         default:
            return Vec3i.ZERO;
      }

      return new Vec3i(x, 0, y);
   }
}
