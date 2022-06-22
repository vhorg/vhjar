package iskallia.vault.world.vault.gen.layout;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3i;

public class SpiralHelper {
   public static Vector3i getSpiralPosition(int index, Direction facing, Direction rotation) {
      int k = (int)Math.ceil((Math.sqrt(index + 1) - 1.0) / 2.0);
      int a = 2 * k;
      int b = (a + 1) * (a + 1);
      int x;
      int y;
      if (index + 1 >= b - a) {
         x = k - (b - index - 1);
         y = -k;
      } else {
         b -= a;
         if (index + 1 >= b - a) {
            x = -k;
            y = -k + (b - index - 1);
         } else {
            b -= a;
            if (index + 1 >= b - a) {
               x = -k + (b - index - 1);
               y = k;
            } else {
               x = k;
               y = k - (b - index - a - 1);
            }
         }
      }

      if (facing == Direction.EAST) {
         if (rotation == Direction.SOUTH) {
            y *= -1;
         }
      } else if (facing == Direction.WEST) {
         x *= -1;
         if (rotation == Direction.SOUTH) {
            y *= -1;
         }
      } else if (facing == Direction.NORTH) {
         int temp = x;
         x = y;
         y = -temp;
         if (rotation == Direction.WEST) {
            x *= -1;
         }
      } else {
         if (facing != Direction.SOUTH) {
            return Vector3i.field_177959_e;
         }

         int temp = x;
         x = y;
         y = temp;
         if (rotation == Direction.WEST) {
            x *= -1;
         }
      }

      return new Vector3i(x, 0, y);
   }
}
