package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.IPositionPredicate;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.IWorld;

public class VaultPortalSize {
   private final IWorld world;
   private final Axis axis;
   private final Direction rightDir;
   private int portalBlockCount;
   @Nullable
   private BlockPos bottomLeft;
   private int height;
   private int width;
   private IPositionPredicate positionPredicate;

   public VaultPortalSize(IWorld worldIn, BlockPos pos, Axis axisIn, IPositionPredicate positionPredicate) {
      this.world = worldIn;
      this.axis = axisIn;
      this.rightDir = axisIn == Axis.X ? Direction.WEST : Direction.SOUTH;
      this.positionPredicate = positionPredicate;
      this.bottomLeft = this.computeBottomLeft(pos);
      if (this.bottomLeft == null) {
         this.bottomLeft = pos;
         this.width = 1;
         this.height = 1;
      } else {
         this.width = this.computeWidth();
         if (this.width > 0) {
            this.height = this.computeHeight();
         }
      }
   }

   public static Optional<VaultPortalSize> getPortalSize(IWorld world, BlockPos pos, Axis axis, IPositionPredicate positionPredicate) {
      return getPortalSize(world, pos, size -> size.isValid() && size.portalBlockCount == 0, axis, positionPredicate);
   }

   public static Optional<VaultPortalSize> getPortalSize(
      IWorld world, BlockPos pos, Predicate<VaultPortalSize> sizePredicate, Axis axis, IPositionPredicate positionPredicate
   ) {
      Optional<VaultPortalSize> optional = Optional.of(new VaultPortalSize(world, pos, axis, positionPredicate)).filter(sizePredicate);
      if (optional.isPresent()) {
         return optional;
      } else {
         Axis direction$axis = axis == Axis.X ? Axis.Z : Axis.X;
         return Optional.of(new VaultPortalSize(world, pos, direction$axis, positionPredicate)).filter(sizePredicate);
      }
   }

   private static boolean canConnect(BlockState state) {
      return state.func_196958_f() || state.func_203425_a(ModBlocks.VAULT_PORTAL) || state.func_203425_a(ModBlocks.OTHER_SIDE_PORTAL);
   }

   @Nullable
   private BlockPos computeBottomLeft(BlockPos pos) {
      int i = Math.max(0, pos.func_177956_o() - 21);

      while (pos.func_177956_o() > i && canConnect(this.world.func_180495_p(pos.func_177977_b()))) {
         pos = pos.func_177977_b();
      }

      Direction direction = this.rightDir.func_176734_d();
      int j = this.computeWidth(pos, direction) - 1;
      return j < 0 ? null : pos.func_177967_a(direction, j);
   }

   public Axis getAxis() {
      return this.axis;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public BlockPos getBottomLeft() {
      return this.bottomLeft;
   }

   private int computeWidth() {
      int i = this.computeWidth(this.bottomLeft, this.rightDir);
      return i >= 2 && i <= 21 ? i : 0;
   }

   private int computeWidth(BlockPos pos, Direction direction) {
      Mutable blockpos$mutable = new Mutable();

      for (int i = 0; i <= 21; i++) {
         blockpos$mutable.func_189533_g(pos).func_189534_c(direction, i);
         BlockState blockstate = this.world.func_180495_p(blockpos$mutable);
         if (!canConnect(blockstate)) {
            if (this.positionPredicate.test(blockstate, this.world, blockpos$mutable)) {
               return i;
            }
            break;
         }

         BlockState blockstate1 = this.world.func_180495_p(blockpos$mutable.func_189536_c(Direction.DOWN));
         if (!this.positionPredicate.test(blockstate1, this.world, blockpos$mutable)) {
            break;
         }
      }

      return 0;
   }

   private int computeHeight() {
      Mutable blockpos$mutable = new Mutable();
      int i = this.getFrameColumnCount(blockpos$mutable);
      return i >= 3 && i <= 21 && this.computeHeight(blockpos$mutable, i) ? i : 0;
   }

   private boolean computeHeight(Mutable mutablePos, int upDisplacement) {
      for (int i = 0; i < this.width; i++) {
         Mutable blockpos$mutable = mutablePos.func_189533_g(this.bottomLeft).func_189534_c(Direction.UP, upDisplacement).func_189534_c(this.rightDir, i);
         if (!this.positionPredicate.test(this.world.func_180495_p(blockpos$mutable), this.world, blockpos$mutable)) {
            return false;
         }
      }

      return true;
   }

   private int getFrameColumnCount(Mutable mutablePos) {
      for (int i = 0; i < 21; i++) {
         mutablePos.func_189533_g(this.bottomLeft).func_189534_c(Direction.UP, i).func_189534_c(this.rightDir, -1);
         if (!this.positionPredicate.test(this.world.func_180495_p(mutablePos), this.world, mutablePos)) {
            return i;
         }

         mutablePos.func_189533_g(this.bottomLeft).func_189534_c(Direction.UP, i).func_189534_c(this.rightDir, this.width);
         if (!this.positionPredicate.test(this.world.func_180495_p(mutablePos), this.world, mutablePos)) {
            return i;
         }

         for (int j = 0; j < this.width; j++) {
            mutablePos.func_189533_g(this.bottomLeft).func_189534_c(Direction.UP, i).func_189534_c(this.rightDir, j);
            BlockState blockstate = this.world.func_180495_p(mutablePos);
            if (!canConnect(blockstate)) {
               return i;
            }

            if (blockstate.func_203425_a(ModBlocks.VAULT_PORTAL) || blockstate.func_203425_a(ModBlocks.OTHER_SIDE_PORTAL)) {
               this.portalBlockCount++;
            }
         }
      }

      return 21;
   }

   public boolean isValid() {
      return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
   }

   public void placePortalBlocks(Consumer<BlockPos> placer) {
      BlockPos.func_218278_a(this.bottomLeft, this.bottomLeft.func_177967_a(Direction.UP, this.height - 1).func_177967_a(this.rightDir, this.width - 1))
         .forEach(placer);
   }

   public boolean validatePortal() {
      return this.isValid() && this.portalBlockCount == this.width * this.height;
   }

   public Direction getRightDir() {
      return this.rightDir;
   }
}
