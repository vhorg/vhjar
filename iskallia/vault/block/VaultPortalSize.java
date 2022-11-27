package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.StatePredicate;

public class VaultPortalSize {
   private final LevelAccessor world;
   private final Axis axis;
   private final Direction rightDir;
   private int portalBlockCount;
   @Nullable
   private BlockPos bottomLeft;
   private int height;
   private final int width;
   private final StatePredicate positionPredicate;

   public VaultPortalSize(LevelAccessor worldIn, BlockPos pos, Axis axisIn, StatePredicate positionPredicate) {
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

   public static Optional<VaultPortalSize> getPortalSize(LevelAccessor world, BlockPos pos, Axis axis, StatePredicate positionPredicate) {
      return getPortalSize(world, pos, size -> size.isValid() && size.portalBlockCount == 0, axis, positionPredicate);
   }

   public static Optional<VaultPortalSize> getPortalSize(
      LevelAccessor world, BlockPos pos, Predicate<VaultPortalSize> sizePredicate, Axis axis, StatePredicate positionPredicate
   ) {
      Optional<VaultPortalSize> optional = Optional.of(new VaultPortalSize(world, pos, axis, positionPredicate)).filter(sizePredicate);
      if (optional.isPresent()) {
         return optional;
      } else {
         Axis direction$axis = axis == Axis.X ? Axis.Z : Axis.X;
         return Optional.of(new VaultPortalSize(world, pos, direction$axis, positionPredicate)).filter(sizePredicate);
      }
   }

   public static List<BlockPos> getFrame(LevelAccessor world, BlockPos pos) {
      List<BlockPos> positions = new ArrayList<>();
      Optional<VaultPortalSize> portalSize = findPortalSizeFromPortalBlock(world, pos);
      if (portalSize.isPresent()) {
         VaultPortalSize size = portalSize.get();
         BlockPos current = size.bottomLeft == null ? null : size.bottomLeft.relative(size.rightDir.getOpposite()).below();
         if (current != null) {
            positions.add(current);
            findAndAddPositions(world, positions, size, current);
         }
      }

      return positions;
   }

   private static void findAndAddPositions(LevelAccessor world, List<BlockPos> positions, VaultPortalSize size, BlockPos current) {
      for (int up = 0; up <= size.height; up++) {
         if (!VaultPortalBlock.FRAME.test(world.getBlockState(current.above()), world, current.above())) {
            current = current.above();
            positions.add(current);
            break;
         }

         current = current.above();
         positions.add(current);
      }

      for (int right = 0; right <= size.width; right++) {
         if (!VaultPortalBlock.FRAME.test(world.getBlockState(current.relative(size.rightDir)), world, current.relative(size.rightDir))) {
            current = current.relative(size.rightDir);
            positions.add(current);
            break;
         }

         current = current.relative(size.rightDir);
         positions.add(current);
      }

      for (int down = 0; down <= size.height; down++) {
         if (!VaultPortalBlock.FRAME.test(world.getBlockState(current.below()), world, current.below())) {
            current = current.below();
            positions.add(current);
            break;
         }

         current = current.below();
         positions.add(current);
      }

      for (int left = 0; left < size.width; left++) {
         if (!VaultPortalBlock.FRAME
            .test(world.getBlockState(current.relative(size.rightDir.getOpposite())), world, current.relative(size.rightDir.getOpposite()))) {
            positions.add(current.above());
            break;
         }

         current = current.relative(size.rightDir.getOpposite());
         positions.add(current);
      }
   }

   private static Optional<VaultPortalSize> findPortalSizeFromPortalBlock(LevelAccessor world, BlockPos pos) {
      Optional<VaultPortalSize> portalSize = getPortalSize(world, pos.north(), VaultPortalSize::isValid, Axis.Z, VaultPortalBlock.FRAME);
      if (!portalSize.isPresent()) {
         portalSize = getPortalSize(world, pos.south(), VaultPortalSize::isValid, Axis.Z, VaultPortalBlock.FRAME);
      }

      if (!portalSize.isPresent()) {
         portalSize = getPortalSize(world, pos.east(), VaultPortalSize::isValid, Axis.X, VaultPortalBlock.FRAME);
      }

      if (!portalSize.isPresent()) {
         portalSize = getPortalSize(world, pos.west(), VaultPortalSize::isValid, Axis.X, VaultPortalBlock.FRAME);
      }

      return portalSize;
   }

   private static boolean canConnect(BlockState state) {
      return state.isAir() || state.is(ModBlocks.VAULT_PORTAL) || state.is(ModBlocks.OTHER_SIDE_PORTAL);
   }

   @Nullable
   private BlockPos computeBottomLeft(BlockPos pos) {
      int i = Math.max(0, pos.getY() - 21);

      while (pos.getY() > i && canConnect(this.world.getBlockState(pos.below()))) {
         pos = pos.below();
      }

      Direction direction = this.rightDir.getOpposite();
      int j = this.computeWidth(pos, direction) - 1;
      return j < 0 ? null : pos.relative(direction, j);
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
      MutableBlockPos blockpos$mutable = new MutableBlockPos();

      for (int i = 0; i <= 21; i++) {
         blockpos$mutable.set(pos).move(direction, i);
         BlockState blockstate = this.world.getBlockState(blockpos$mutable);
         if (!canConnect(blockstate)) {
            if (this.positionPredicate.test(blockstate, this.world, blockpos$mutable)) {
               return i;
            }
            break;
         }

         BlockState blockstate1 = this.world.getBlockState(blockpos$mutable.move(Direction.DOWN));
         if (!this.positionPredicate.test(blockstate1, this.world, blockpos$mutable)) {
            break;
         }
      }

      return 0;
   }

   private int computeHeight() {
      MutableBlockPos blockpos$mutable = new MutableBlockPos();
      int i = this.getFrameColumnCount(blockpos$mutable);
      return i >= 3 && i <= 21 && this.computeHeight(blockpos$mutable, i) ? i : 0;
   }

   private boolean computeHeight(MutableBlockPos mutablePos, int upDisplacement) {
      for (int i = 0; i < this.width; i++) {
         MutableBlockPos blockpos$mutable = mutablePos.set(this.bottomLeft).move(Direction.UP, upDisplacement).move(this.rightDir, i);
         if (!this.positionPredicate.test(this.world.getBlockState(blockpos$mutable), this.world, blockpos$mutable)) {
            return false;
         }
      }

      return true;
   }

   private int getFrameColumnCount(MutableBlockPos mutablePos) {
      for (int i = 0; i < 21; i++) {
         mutablePos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
         if (!this.positionPredicate.test(this.world.getBlockState(mutablePos), this.world, mutablePos)) {
            return i;
         }

         mutablePos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
         if (!this.positionPredicate.test(this.world.getBlockState(mutablePos), this.world, mutablePos)) {
            return i;
         }

         for (int j = 0; j < this.width; j++) {
            mutablePos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
            BlockState blockstate = this.world.getBlockState(mutablePos);
            if (!canConnect(blockstate)) {
               return i;
            }

            if (blockstate.is(ModBlocks.VAULT_PORTAL) || blockstate.is(ModBlocks.OTHER_SIDE_PORTAL)) {
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
      BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach(placer);
   }

   public boolean validatePortal() {
      return this.isValid() && this.portalBlockCount == this.width * this.height;
   }

   public Direction getRightDir() {
      return this.rightDir;
   }
}
