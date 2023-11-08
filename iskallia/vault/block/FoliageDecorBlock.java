package iskallia.vault.block;

import iskallia.vault.block.entity.FoliageDecorTileEntity;
import iskallia.vault.init.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoliageDecorBlock extends CarpetBlock implements EntityBlock {
   protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);

   public FoliageDecorBlock(Properties pProperties) {
      super(pProperties.noOcclusion());
   }

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      if (level.getBlockEntity(pos) instanceof FoliageDecorTileEntity foliageDecor
         && foliageDecor.getInventory().getItem(0).getItem() instanceof BlockItem blockItem) {
         VoxelShape shape = blockItem.getBlock().defaultBlockState().getShape(level, pos);
         Vec3 offset = blockItem.getBlock().defaultBlockState().getOffset(level, foliageDecor.getBlockPos());
         if (!(blockItem.getBlock() instanceof BambooBlock) && !(blockItem.getBlock() instanceof FlowerBlock)) {
            offset = new Vec3(0.0, 0.0, 0.0);
         }

         return Shapes.join(SHAPE, shape.move(-offset.x(), 0.0625 - offset.y(), -offset.z()), BooleanOp.OR);
      } else {
         return SHAPE;
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      if (level.getBlockEntity(pos) instanceof FoliageDecorTileEntity foliageDecor
         && foliageDecor.getInventory().getItem(0).getItem() instanceof BlockItem blockItem) {
         VoxelShape shape = blockItem.getBlock().defaultBlockState().getShape(level, pos);
         if (!blockItem.getBlock().defaultBlockState().getCollisionShape(level, pos).isEmpty()) {
            Vec3 offset = blockItem.getBlock().defaultBlockState().getOffset(level, foliageDecor.getBlockPos());
            if (!(blockItem.getBlock() instanceof BambooBlock) && !(blockItem.getBlock() instanceof FlowerBlock)) {
               offset = new Vec3(0.0, 0.0, 0.0);
            }

            return Shapes.join(SHAPE, shape.move(-offset.x(), 0.0625 - offset.y(), -offset.z()), BooleanOp.OR);
         }
      }

      return SHAPE;
   }

   public static VoxelShape scaleAndTranslateShape(VoxelShape originalShape, float scaleFactor, float translateY) {
      if (originalShape.isEmpty()) {
         return originalShape;
      } else {
         List<AABB> list = originalShape.toAabbs();
         List<VoxelShape> voxelList = new ArrayList<>();
         VoxelShape shape = Shapes.empty();

         for (AABB aabb : list) {
            Vec3 minCoords = new Vec3(aabb.minX, aabb.minY, aabb.minZ);
            Vec3 maxCoords = new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ);
            double scaledMinX = minCoords.x / scaleFactor;
            double scaledMinY = minCoords.y / scaleFactor + translateY;
            double scaledMinZ = minCoords.z / scaleFactor;
            double scaledMaxX = maxCoords.x / scaleFactor;
            double scaledMaxY = maxCoords.y / scaleFactor + translateY;
            double scaledMaxZ = maxCoords.z / scaleFactor;
            VoxelShape scaledShape = Shapes.box(scaledMinX, scaledMinY, scaledMinZ, scaledMaxX, scaledMaxY, scaledMaxZ);
            voxelList.add(scaledShape);
         }

         for (VoxelShape scaledShape : voxelList) {
            shape = Shapes.join(shape, scaledShape, BooleanOp.OR);
         }

         return shape;
      }
   }

   public BlockState updateShape(
      BlockState p_152926_, Direction p_152927_, BlockState p_152928_, LevelAccessor p_152929_, BlockPos p_152930_, BlockPos p_152931_
   ) {
      return !p_152926_.canSurvive(p_152929_, p_152930_)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(p_152926_, p_152927_, p_152928_, p_152929_, p_152930_, p_152931_);
   }

   public boolean canSurvive(BlockState p_152922_, LevelReader p_152923_, BlockPos p_152924_) {
      return !p_152923_.isEmptyBlock(p_152924_.below());
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.FOLIAGE_DECOR_TILE_ENTITY.create(pos, state);
   }

   public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
      return false;
   }

   @NotNull
   public InteractionResult use(
      @NotNull BlockState state,
      @NotNull Level level,
      @NotNull BlockPos pos,
      @NotNull Player player,
      @NotNull InteractionHand hand,
      @NotNull BlockHitResult hit
   ) {
      if (level.getBlockEntity(pos) instanceof FoliageDecorTileEntity foliageDecor) {
         return foliageDecor.interact(state, level, player, hand, hit) ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pState.is(pNewState.getBlock())) {
         if (pLevel.getBlockEntity(pPos) instanceof FoliageDecorTileEntity foliageDecor) {
            Containers.dropContents(pLevel, pPos, foliageDecor.getInventory());
            pLevel.updateNeighbourForOutputSignal(pPos, this);
         }

         super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
      }
   }
}
