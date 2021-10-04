package iskallia.vault.util;

import java.util.Arrays;
import java.util.List;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class VoxelUtils {
   public static VoxelShape combineAll(IBooleanFunction fct, VoxelShape... shapes) {
      return combineAll(fct, Arrays.asList(shapes));
   }

   public static VoxelShape combineAll(IBooleanFunction fct, List<VoxelShape> shapes) {
      if (shapes.isEmpty()) {
         return VoxelShapes.func_197880_a();
      } else {
         VoxelShape first = shapes.get(0);

         for (int i = 1; i < shapes.size(); i++) {
            first = VoxelShapes.func_197882_b(first, shapes.get(i), fct);
         }

         return first;
      }
   }
}
