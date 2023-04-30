package iskallia.vault.block.entity.base;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BookAnimatingTileEntity extends BlockEntity {
   private static final Random RANDOM = new Random();
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   public float rot;
   public float oRot;
   public float tRot;

   public BookAnimatingTileEntity(BlockEntityType<?> tileEntityType, BlockPos blockPos, BlockState blockState) {
      super(tileEntityType, blockPos, blockState);
   }

   protected boolean canOpenBookModel(Player nearestPlayer, Level level, BlockPos blockPos, BlockState blockState, BookAnimatingTileEntity tileEntity) {
      return true;
   }

   public static void bookAnimationTick(Level level, BlockPos pos, BlockState state, BookAnimatingTileEntity tileEntity) {
      tileEntity.oOpen = tileEntity.open;
      tileEntity.oRot = tileEntity.rot;
      Player player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6.0, false);
      if (player != null && tileEntity.canOpenBookModel(player, level, pos, state, tileEntity)) {
         double d0 = player.getX() - (pos.getX() + 0.5);
         double d1 = player.getZ() - (pos.getZ() + 0.5);
         tileEntity.tRot = (float)Mth.atan2(d1, d0);
         tileEntity.open += 0.1F;
         if (tileEntity.open < 0.5F || RANDOM.nextInt(40) == 0) {
            float f1 = tileEntity.flipT;

            do {
               tileEntity.flipT = tileEntity.flipT + (RANDOM.nextInt(4) - RANDOM.nextInt(4));
            } while (f1 == tileEntity.flipT);
         }
      } else {
         tileEntity.tRot += 0.02F;
         tileEntity.open -= 0.1F;
      }

      while (tileEntity.rot >= (float) Math.PI) {
         tileEntity.rot -= (float) (Math.PI * 2);
      }

      while (tileEntity.rot < (float) -Math.PI) {
         tileEntity.rot += (float) (Math.PI * 2);
      }

      while (tileEntity.tRot >= (float) Math.PI) {
         tileEntity.tRot -= (float) (Math.PI * 2);
      }

      while (tileEntity.tRot < (float) -Math.PI) {
         tileEntity.tRot += (float) (Math.PI * 2);
      }

      float f2 = tileEntity.tRot - tileEntity.rot;

      while (f2 >= (float) Math.PI) {
         f2 -= (float) (Math.PI * 2);
      }

      while (f2 < (float) -Math.PI) {
         f2 += (float) (Math.PI * 2);
      }

      tileEntity.rot += f2 * 0.4F;
      tileEntity.open = Mth.clamp(tileEntity.open, 0.0F, 1.0F);
      tileEntity.time++;
      tileEntity.oFlip = tileEntity.flip;
      float f = (tileEntity.flipT - tileEntity.flip) * 0.4F;
      float f3 = 0.2F;
      f = Mth.clamp(f, -0.2F, 0.2F);
      tileEntity.flipA = tileEntity.flipA + (f - tileEntity.flipA) * 0.9F;
      tileEntity.flip = tileEntity.flip + tileEntity.flipA;
   }
}
